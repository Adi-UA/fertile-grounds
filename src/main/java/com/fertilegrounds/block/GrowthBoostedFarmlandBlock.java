package com.fertilegrounds.block;

import com.fertilegrounds.util.GrowthBoostUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A farmland variant that passively fertilizes whatever crop is planted on top of it, on top of
 * vanilla farmland's normal moisture/hydration behavior.
 *
 * <p>Since 26.3, vanilla {@link FarmlandBlock} takes the block it reverts to on trampling or drying
 * out as a constructor argument, so passing this tier's own dirt block is all it takes to keep a
 * crafted block from downgrading into plain vanilla dirt.
 */
public class GrowthBoostedFarmlandBlock extends FarmlandBlock {

  private final float growthBoostChance;

  /**
   * @param properties block properties, e.g. from {@code ModBlockProperties.farmlandProperties()}
   * @param growthBoostChance per-random-tick probability (0.0-1.0) of fertilizing the plant above
   * @param driedOutBlock the block this reverts to on trample or drought (this tier's own dirt)
   */
  public GrowthBoostedFarmlandBlock(
      final Properties properties, final float growthBoostChance, final Block driedOutBlock) {
    super(driedOutBlock, properties);
    this.growthBoostChance = growthBoostChance;
  }

  @Override
  protected void randomTick(
      final BlockState state,
      final ServerLevel world,
      final BlockPos pos,
      final RandomSource random) {
    super.randomTick(state, world, pos, random);
    GrowthBoostUtil.tryBoostAbove(world, pos, random, this.growthBoostChance);
  }
}
