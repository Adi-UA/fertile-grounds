package com.fertilegrounds.fairy;

import com.fertilegrounds.util.ModTags;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * What around a crop draws a Bean Fairy: how many different fairy flowers grow nearby, and whether
 * a beehive or bee nest is close.
 */
public record FarmAttractions(int flowerTypes, boolean hasHive) {

  private static final int RADIUS = 8;

  private static final int HEIGHT = 4;

  /**
   * Scans the 17x9x17 box around {@code crop} once. Flowers count by block, not block state, so a
   * tall flower's two halves count as one type.
   */
  public static FarmAttractions around(final LevelReader level, final BlockPos crop) {
    final Set<Block> flowers = new HashSet<>();
    boolean hasHive = false;
    for (final BlockPos pos :
        BlockPos.betweenClosed(
            crop.offset(-RADIUS, -HEIGHT, -RADIUS), crop.offset(RADIUS, HEIGHT, RADIUS))) {
      final BlockState state = level.getBlockState(pos);
      if (state.is(ModTags.FAIRY_FLOWERS)) {
        flowers.add(state.getBlock());
      } else if (state.is(BlockTags.BEEHIVES)) {
        hasHive = true;
      }
    }
    return new FarmAttractions(flowers.size(), hasHive);
  }
}
