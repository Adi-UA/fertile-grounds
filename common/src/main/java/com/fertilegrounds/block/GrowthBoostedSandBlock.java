package com.fertilegrounds.block;

import com.fertilegrounds.util.GrowthBoostUtil;
import com.fertilegrounds.util.ModGameRules;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRule;

/**
 * Sand that passively fertilizes what grows on it (cactus, sugar cane, stems). Crops still can't be
 * planted on it, and it falls like vanilla sand.
 */
public class GrowthBoostedSandBlock extends FallingBlock {

  /**
   * This Minecraft version needs every falling block to name a codec. Vanilla only uses it to
   * describe the block type, so it rebuilds the one Enriched Sand the mod registers.
   */
  public static final MapCodec<GrowthBoostedSandBlock> CODEC =
      simpleCodec(
          properties ->
              new GrowthBoostedSandBlock(
                  properties, ModGameRules.ENRICHED_BOOST_CHANCE, SoilTier.ENRICHED));

  private final GameRule<Integer> boostChanceRule;

  private final SoilTier tier;

  /**
   * @param boostChanceRule the percent chance per random tick of fertilizing the plant above, read
   *     on every tick so {@code /gamerule} changes apply at once
   */
  public GrowthBoostedSandBlock(
      final Properties properties, final GameRule<Integer> boostChanceRule, final SoilTier tier) {
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
        world, pos, random, world.getGameRules().get(this.boostChanceRule));
  }

  /** The gamerule holding this soil's boost chance, which Jade shows. */
  public GameRule<Integer> boostChanceRule() {
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
