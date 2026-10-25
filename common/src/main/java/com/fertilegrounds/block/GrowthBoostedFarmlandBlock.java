package com.fertilegrounds.block;

import com.fertilegrounds.FertileGrounds;
import com.fertilegrounds.hotel.FairyHotelLookup;
import com.fertilegrounds.util.GrowthBoostUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

/**
 * A farmland variant that passively fertilizes whatever crop is planted on top of it, on top of
 * vanilla farmland's normal moisture/hydration behavior.
 *
 * <p>Vanilla {@link FarmBlock} has two ways to revert to dirt on its own (trampling and drying
 * out), and before 26.3 both hardcode the target as {@code Blocks.DIRT}. Left unchanged, either
 * would silently downgrade a crafted block into plain vanilla dirt instead of this tier's own dirt
 * block. Both are reimplemented here to revert to {@link #driedOutBlock} instead — same vanilla
 * conditions and odds, just a corrected target. Trampling also skips farmland near an occupied
 * Fairy Hotel, like {@code FarmlandBlockMixin} does for vanilla farmland.
 */
public class GrowthBoostedFarmlandBlock extends FarmBlock {

  private static final float TRAMPLE_ROLL_FALL_DISTANCE_OFFSET = 0.5F;
  private static final float MIN_TRAMPLE_BOUNDING_BOX_VOLUME = 0.512F;

  /** Farmland this far from a hotel, horizontally, is safe from trampling. */
  private static final int PROTECTED_REACH = 4;

  /** And this far up or down. */
  private static final int PROTECTED_HEIGHT = 2;

  private final GameRules.Key<GameRules.IntegerValue> boostChanceRule;

  private final Block driedOutBlock;

  private final SoilTier tier;

  /**
   * @param properties block properties, e.g. from {@code ModBlockProperties.farmlandProperties()}
   * @param boostChanceRule the gamerule holding the per-random-tick percent chance of fertilizing
   *     the plant above, read on every tick so {@code /gamerule} changes apply at once
   * @param driedOutBlock the block this reverts to on trample or drought (this tier's own dirt)
   * @param tier the tier a comparator reads
   */
  public GrowthBoostedFarmlandBlock(
      final Properties properties,
      final GameRules.Key<GameRules.IntegerValue> boostChanceRule,
      final Block driedOutBlock,
      final SoilTier tier) {
    super(properties);
    this.boostChanceRule = boostChanceRule;
    this.driedOutBlock = driedOutBlock;
    this.tier = tier;
  }

  @Override
  protected void randomTick(
      final BlockState state,
      final ServerLevel world,
      final BlockPos pos,
      final RandomSource random) {
    tickMoisture(state, world, pos);
    GrowthBoostUtil.tryBoostAbove(
        world, pos, random, world.getGameRules().getInt(this.boostChanceRule));
  }

  @Override
  public void fallOn(
      final Level level,
      final BlockState state,
      final BlockPos pos,
      final Entity entity,
      final double fallDistance) {
    maybeTrampleToDirt(level, state, pos, entity, fallDistance);
    entity.causeFallDamage(fallDistance, 1.0F, entity.damageSources().fall());
  }

  /** The gamerule holding this soil's boost chance, which Jade shows. */
  public GameRules.Key<GameRules.IntegerValue> boostChanceRule() {
    return this.boostChanceRule;
  }

  /** A comparator reads this soil's tier. */
  @Override
  protected boolean hasAnalogOutputSignal(final BlockState state) {
    return true;
  }

  @Override
  protected int getAnalogOutputSignal(
      final BlockState state, final Level level, final BlockPos pos, final Direction direction) {
    return this.tier.comparatorSignal();
  }

  /**
   * Same trample roll and conditions as vanilla {@code FarmBlock.fallOn}: only living entities
   * heavy/wide enough, only when mobGriefing allows it for non-players, harder falls are more
   * likely to trigger it. The only changes are the revert target (see {@link
   * #revertToDriedOutBlock}) and the hotel check.
   *
   * <p>Written as early-return guard clauses, in the same order as vanilla's original {@code &&}
   * chain, so the client-side check still runs first and the RNG roll still never happens on the
   * client — collapsing this into eagerly-evaluated booleans up front would change that.
   */
  private void maybeTrampleToDirt(
      final Level level,
      final BlockState state,
      final BlockPos pos,
      final Entity entity,
      final double fallDistance) {
    if (!(level instanceof ServerLevel serverLevel) || !(entity instanceof LivingEntity)) {
      return;
    }

    final boolean mobGriefingAllowsRevert =
        entity instanceof Player
            || serverLevel.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
    if (!mobGriefingAllowsRevert) {
      return;
    }

    final boolean isBigEnoughToTrample =
        entity.getBbWidth() * entity.getBbWidth() * entity.getBbHeight()
            > MIN_TRAMPLE_BOUNDING_BOX_VOLUME;
    if (!isBigEnoughToTrample) {
      return;
    }

    final boolean passesTrampleRoll =
        level.getRandom().nextFloat() < fallDistance - TRAMPLE_ROLL_FALL_DISTANCE_OFFSET;
    if (passesTrampleRoll
        && !FairyHotelLookup.isNearOccupiedHotel(
            serverLevel, pos, PROTECTED_REACH, PROTECTED_HEIGHT)) {
      revertToDriedOutBlock(state, level, pos, entity);
    }
  }

  /**
   * Reimplementation of {@code FarmBlock}'s moisture tick: vanilla's version is unusable as a base
   * to extend because its water/maintenance checks are private, and its dry-out case is hardcoded
   * to revert to {@code Blocks.DIRT}.
   */
  private void tickMoisture(final BlockState state, final ServerLevel world, final BlockPos pos) {
    final int moisture = state.getValue(MOISTURE);
    final boolean hydrated = isNearWater(world, pos) || world.isRainingAt(pos.above());

    if (hydrated) {
      if (moisture < MAX_MOISTURE) {
        world.setBlock(pos, state.setValue(MOISTURE, MAX_MOISTURE), 2);
      }
      return;
    }

    if (moisture > 0) {
      world.setBlock(pos, state.setValue(MOISTURE, moisture - 1), 2);
      return;
    }

    if (!world.getBlockState(pos.above()).is(BlockTags.MAINTAINS_FARMLAND)) {
      revertToDriedOutBlock(state, world, pos, null);
    }
  }

  private static boolean isNearWater(final ServerLevel world, final BlockPos pos) {
    for (final BlockPos candidate :
        BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 1, 4))) {
      if (world.getFluidState(candidate).is(FluidTags.WATER)) {
        return true;
      }
    }
    return false;
  }

  private void revertToDriedOutBlock(
      final BlockState state, final Level world, final BlockPos pos, @Nullable final Entity cause) {
    final BlockState newState =
        Block.pushEntitiesUp(state, this.driedOutBlock.defaultBlockState(), world, pos);
    world.setBlockAndUpdate(pos, newState);
    world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(cause, newState));
    FertileGrounds.LOGGER.debug(
        "{} reverted to {} at {} (cause: {})", state.getBlock(), this.driedOutBlock, pos, cause);
  }
}
