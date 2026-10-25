package com.fertilegrounds.block;

import com.fertilegrounds.util.GrowthBoostUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRule;

/**
 * Farmland that passively fertilizes the crop on top. Vanilla {@link FarmlandBlock} takes the block
 * it reverts to in its constructor, so trampled or dry soil turns back into this tier's dirt.
 */
public class GrowthBoostedFarmlandBlock extends FarmlandBlock {

  private final GameRule<Integer> boostChanceRule;

  private final SoilTier tier;

  /**
   * @param boostChanceRule the percent chance per random tick of fertilizing the plant above, read
   *     on every tick so {@code /gamerule} changes apply at once
   */
  public GrowthBoostedFarmlandBlock(
      final Properties properties,
      final GameRule<Integer> boostChanceRule,
      final Block driedOutBlock,
      final SoilTier tier) {
    super(driedOutBlock, properties);
    this.boostChanceRule = boostChanceRule;
    this.tier = tier;
  }

  @Override
  protected void randomTick(
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
