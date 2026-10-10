package com.fertilegrounds.blight;

import com.fertilegrounds.block.ModBlocks;
import com.fertilegrounds.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockState;

/** Turning soil into Blighted Dirt and back, shared by spreading, the witch curse, and cures. */
public final class Blight {

  private Blight() {}

  /**
   * Whether blight can take the block at {@code pos}: it's in {@code fertilegrounds:blightable} and
   * open to the sky side, so blight creeps across the surface instead of through buried dirt.
   */
  public static boolean canTake(final BlockGetter level, final BlockPos pos) {
    final BlockPos above = pos.above();
    return level.getBlockState(pos).is(ModTags.BLIGHTABLE)
        && !level.getBlockState(above).isFaceSturdy(level, above, Direction.DOWN);
  }

  /** Turns the block at {@code pos} into Blighted Dirt, withering the plant on top first. */
  public static void take(final ServerLevel level, final BlockPos pos) {
    final BlockPos above = pos.above();
    if (isPlant(level.getBlockState(above))) {
      // Swapping the plant before the soil means it never pops off and drops its items. The top
      // half of a tall plant removes itself once its bottom half is gone.
      level.setBlockAndUpdate(above, Blocks.DEAD_BUSH.defaultBlockState());
      effects(level, above, SoundEvents.CROP_BREAK, ParticleTypes.SMOKE, 4);
    }
    level.setBlockAndUpdate(pos, ModBlocks.BLIGHTED_DIRT.defaultBlockState());
    effects(level, pos, SoundEvents.SCULK_BLOCK_SPREAD, ParticleTypes.ASH, 6);
  }

  /**
   * Turns Blighted Dirt at {@code pos} back into dirt.
   *
   * @return false if the block isn't Blighted Dirt
   */
  public static boolean cure(final ServerLevel level, final BlockPos pos) {
    if (!level.getBlockState(pos).is(ModBlocks.BLIGHTED_DIRT)) {
      return false;
    }
    level.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
    effects(level, pos, SoundEvents.COMPOSTER_FILL_SUCCESS, ParticleTypes.HAPPY_VILLAGER, 8);
    return true;
  }

  /**
   * Cures every Blighted Dirt in the square of {@code radius} around {@code center}, at its level
   * and one below, which is where the soil under a crop sits.
   */
  public static void cureArea(final ServerLevel level, final BlockPos center, final int radius) {
    for (final BlockPos pos :
        BlockPos.betweenClosed(
            center.offset(-radius, -1, -radius), center.offset(radius, 0, radius))) {
      cure(level, pos);
    }
  }

  private static boolean isPlant(final BlockState state) {
    final Block block = state.getBlock();
    return block instanceof VegetationBlock
        || state.is(BlockTags.CROPS)
        || state.is(ModTags.FAIRY_CROPS);
  }

  private static void effects(
      final ServerLevel level,
      final BlockPos pos,
      final SoundEvent sound,
      final ParticleOptions particle,
      final int count) {
    level.playSound(null, pos, sound, SoundSource.BLOCKS, 0.6F, 1.0F);
    level.sendParticles(
        particle, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, count, 0.3, 0.2, 0.3, 0.0);
  }
}
