package com.fertilegrounds.util;

import com.fertilegrounds.FertileGrounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

/** How every boosted soil block passively fertilizes the plant above it. */
public final class GrowthBoostUtil {

  private GrowthBoostUtil() {}

  /**
   * Has a {@code chancePercent} in 100 chance to apply one bone meal step to the plant above {@code
   * pos}, as vanilla's bone meal does. The roll comes first, so a miss reads no blocks.
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
    if (!bonemealable.isValidBonemealTarget(world, pos, state, BonemealSource.INTERACTION)) {
      return false;
    }
    if (!bonemealable.isBonemealSuccess(world, random, pos, state, BonemealSource.INTERACTION)) {
      return false;
    }

    bonemealable.performBonemeal(world, random, pos, state, BonemealSource.INTERACTION);
    FertileGrounds.LOGGER.debug("Passively fertilized {} at {}", state.getBlock(), pos);
    return true;
  }
}
