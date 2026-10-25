package com.fertilegrounds.block;

import com.fertilegrounds.FertileGrounds;
import com.fertilegrounds.hotel.FairyHotelLookup;
import com.fertilegrounds.util.GrowthBoostUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Farmland that passively fertilizes the crop on top. In this Minecraft version vanilla {@link
 * FarmBlock} reverts to plain dirt when trampled or dried out, so both are reimplemented here, with
 * vanilla's conditions and odds, to revert to {@link #driedOutBlock}. Trampling also skips farmland
 * near an occupied Fairy Hotel.
 */
public class GrowthBoostedFarmlandBlock extends FarmBlock {

  private static final float TRAMPLE_ROLL_FALL_DISTANCE_OFFSET = 0.5F;
  private static final float MIN_TRAMPLE_BOUNDING_BOX_VOLUME = 0.512F;

  private final GameRules.Key<GameRules.IntegerValue> boostChanceRule;

  private final Block driedOutBlock;

  private final SoilTier tier;

  /**
   * @param boostChanceRule the percent chance per random tick of fertilizing the plant above, read
   *     on every tick so {@code /gamerule} changes apply at once
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
      final float fallDistance) {
    maybeTrampleToDirt(level, state, pos, entity, fallDistance);
    entity.causeFallDamage(fallDistance, 1.0F, entity.damageSources().fall());
  }

  /**
   * Whether crops on top grow at the moist-farmland speed. Forge asks this through {@code
   * IForgeBlock}, whose default only says yes for vanilla farmland; vanilla, Fabric, and NeoForge
   * never call it, since they already treat any {@link FarmBlock} that way.
   */
  public boolean isFertile(final BlockState state, final BlockGetter level, final BlockPos pos) {
    return state.getValue(MOISTURE) > 0;
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
      final BlockState state, final Level level, final BlockPos pos) {
    return this.tier.comparatorSignal();
  }

  /**
   * Vanilla's {@code FarmBlock.fallOn} trample roll, with this tier's revert target and the hotel
   * check. The checks keep vanilla's order, so the random roll never runs on the client.
   */
  private void maybeTrampleToDirt(
      final Level level,
      final BlockState state,
      final BlockPos pos,
      final Entity entity,
      final float fallDistance) {
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
    if (passesTrampleRoll && !FairyHotelLookup.protectsFromTrampling(serverLevel, pos)) {
      revertToDriedOutBlock(state, level, pos, entity);
    }
  }

  /**
   * Vanilla's {@code FarmBlock} moisture tick, copied because its water checks are private and its
   * dry-out case reverts to {@code Blocks.DIRT}.
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
