package com.fertilegrounds.block;

import com.fertilegrounds.util.GrowthBoostUtil;
import com.fertilegrounds.util.ModGameRules;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A sand variant that passively fertilizes whatever grows directly on top of it (cactus, sugar
 * cane, melon/pumpkin stems), without introducing any new plantable surface — unlike the farmland
 * tiers, ordinary crops still can't be planted here; this only speeds up things that already grow
 * on sand. Falls under gravity exactly like vanilla sand ({@link FallingBlock}).
 */
public class GrowthBoostedSandBlock extends FallingBlock {

  /**
   * Before 26.3, every falling block names a codec. Vanilla only uses it to describe the block
   * type, so it rebuilds the one Enriched Sand the mod registers.
   */
  public static final MapCodec<GrowthBoostedSandBlock> CODEC =
      simpleCodec(
          properties ->
              new GrowthBoostedSandBlock(
                  properties, ModGameRules.ENRICHED_BOOST_CHANCE, SoilTier.ENRICHED));

  private final GameRules.Key<GameRules.IntegerValue> boostChanceRule;

  private final SoilTier tier;

  /**
   * @param properties block properties, e.g. from {@code ModBlockProperties.sandProperties()}
   * @param boostChanceRule the gamerule holding the per-random-tick percent chance of fertilizing
   *     the plant above, read on every tick so {@code /gamerule} changes apply at once
   * @param tier the tier a comparator reads
   */
  public GrowthBoostedSandBlock(
      final Properties properties,
      final GameRules.Key<GameRules.IntegerValue> boostChanceRule,
      final SoilTier tier) {
    super(properties);
    this.boostChanceRule = boostChanceRule;
    this.tier = tier;
  }

  @Override
  protected MapCodec<? extends FallingBlock> codec() {
    return CODEC;
  }

  @Override
  public int getDustColor(final BlockState state, final BlockGetter world, final BlockPos pos) {
    return state.getMapColor(world, pos).col;
  }

  @Override
  public void randomTick(
      final BlockState state,
      final ServerLevel world,
      final BlockPos pos,
      final RandomSource random) {
    super.randomTick(state, world, pos, random);
    GrowthBoostUtil.tryBoostAbove(
        world, pos, random, world.getGameRules().getInt(this.boostChanceRule));
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
}
