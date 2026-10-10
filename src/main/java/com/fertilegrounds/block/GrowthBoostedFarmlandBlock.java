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
 * A farmland variant that passively fertilizes whatever crop is planted on top of it, on top of
 * vanilla farmland's normal moisture/hydration behavior.
 *
 * <p>Since 26.3, vanilla {@link FarmlandBlock} takes the block it reverts to on trampling or drying
 * out as a constructor argument, so passing this tier's own dirt block is all it takes to keep a
 * crafted block from downgrading into plain vanilla dirt.
 */
public class GrowthBoostedFarmlandBlock extends FarmlandBlock {

  private final GameRule<Integer> boostChanceRule;

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
