package com.fertilegrounds.util;

import com.fertilegrounds.fairy.GrowthRings;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Instantly grows crops to full size, used by the Bean Fairy. Only blocks in the {@code
 * fertilegrounds:fairy_crops} tag count, so a fairy never turns grass into flowers or saplings into
 * trees.
 */
public final class CropGrowthUtil {

  /**
   * Safety cap for crops that grow one bone meal step at a time (stems, pitcher crops). The slowest
   * vanilla crop needs 7 steps.
   */
  private static final int MAX_GROWTH_STEPS = 16;

  private CropGrowthUtil() {}

  /** True if the block at {@code pos} is a crop that still has room to grow. */
  public static boolean canGrow(final LevelReader level, final BlockPos pos) {
    final BlockState state = level.getBlockState(pos);
    return state.is(ModTags.FAIRY_CROPS)
        && state.getBlock() instanceof BonemealableBlock crop
        && crop.isValidBonemealTarget(level, pos, state, BonemealSource.INTERACTION);
  }

  /**
   * Fully grows every crop in a square of {@code radius} around {@code center}, checking one block
   * above and below too so slightly uneven farms still get covered.
   *
   * @return how many crops grew
   */
  public static int growArea(final ServerLevel level, final BlockPos center, final int radius) {
    int grown = 0;
    for (final BlockPos pos :
        BlockPos.betweenClosed(
            center.offset(-radius, -1, -radius), center.offset(radius, 1, radius))) {
      if (canGrow(level, pos)) {
        growFully(level, pos.immutable());
        grown++;
      }
    }
    return grown;
  }

  /**
   * Fully grows the crops in one ring around {@code center} (see {@link GrowthRings}), checking one
   * block above and below each spot like {@link #growArea}.
   *
   * @return where crops grew
   */
  public static List<BlockPos> growRing(
      final ServerLevel level, final BlockPos center, final int ring) {
    final List<BlockPos> grown = new ArrayList<>();
    for (final GrowthRings.Offset offset : GrowthRings.ring(ring)) {
      for (int dy = -1; dy <= 1; dy++) {
        final BlockPos pos = center.offset(offset.dx(), dy, offset.dz());
        if (canGrow(level, pos)) {
          growFully(level, pos);
          grown.add(pos);
        }
      }
    }
    return grown;
  }

  private static void growFully(final ServerLevel level, final BlockPos pos) {
    final BlockState state = level.getBlockState(pos);
    if (state.getBlock() instanceof CropBlock crop) {
      level.setBlock(pos, crop.getStateForAge(crop.getMaxAge()), 2);
      return;
    }
    // Stems and pitcher crops aren't CropBlocks, so apply bone meal until they're done.
    for (int step = 0; step < MAX_GROWTH_STEPS && canGrow(level, pos); step++) {
      final BlockState current = level.getBlockState(pos);
      ((BonemealableBlock) current.getBlock())
          .performBonemeal(level, level.getRandom(), pos, current, BonemealSource.INTERACTION);
    }
  }
}
