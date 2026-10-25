package com.fertilegrounds.util;

import com.fertilegrounds.FertileGrounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Shared "passively fertilize the plant above me" behavior used by every boosted block in this mod
 * (both farmland tiers and enriched sand), so each block's randomTick is a single call instead of a
 * copy-pasted check.
 */
public final class GrowthBoostUtil {

  private GrowthBoostUtil() {}

  /**
   * Attempts one bone-meal-equivalent growth stage on whatever is directly above {@code pos}, with
   * a {@code chancePercent} in 100 chance per call. Mirrors the check-and-apply sequence vanilla's
   * BoneMealItem uses on a right-click, minus the item-consumption side effects, since no item is
   * involved here.
   *
   * <p>The random roll happens before any world/block-state reads, so the common case (a failed
   * roll at low chance values) costs nothing.
   */
  public static void tryBoostAbove(
      final ServerLevel world,
      final BlockPos pos,
      final RandomSource random,
      final int chancePercent) {
    tryBoostAt(world, pos.above(), random, chancePercent);
  }

  /**
   * Same as {@link #tryBoostAbove}, for the plant at {@code pos} itself.
   *
   * @return true if the plant grew
   */
  public static boolean tryBoostAt(
      final ServerLevel world,
      final BlockPos pos,
      final RandomSource random,
      final int chancePercent) {
    if (random.nextInt(100) >= chancePercent) {
      return false;
    }

    final BlockState state = world.getBlockState(pos);
    if (!(state.getBlock() instanceof BonemealableBlock bonemealable)) {
      return false;
    }
    if (!bonemealable.isValidBonemealTarget(world, pos, state)) {
      return false;
    }
    if (!bonemealable.isBonemealSuccess(world, random, pos, state)) {
      return false;
    }

    bonemealable.performBonemeal(world, random, pos, state);
    FertileGrounds.LOGGER.debug("Passively fertilized {} at {}", state.getBlock(), pos);
    return true;
  }
}
