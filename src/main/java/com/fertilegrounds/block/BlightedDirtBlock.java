package com.fertilegrounds.block;

import com.fertilegrounds.blight.Blight;
import com.fertilegrounds.blight.BlightRules;
import com.fertilegrounds.util.ModGameRules;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Cursed soil that creeps into touching soil one block at a time, the way grass and mycelium
 * spread: each random tick tries one random neighbor. See {@link Blight} for what taking a block
 * does.
 */
public class BlightedDirtBlock extends Block {

  public BlightedDirtBlock(final Properties properties) {
    super(properties);
  }

  @Override
  protected void randomTick(
      final BlockState state,
      final ServerLevel level,
      final BlockPos pos,
      final RandomSource random) {
    final BlockPos target = pos.relative(Direction.getRandom(random));
    if (!Blight.canTake(level, target)) {
      return;
    }
    final SoilTier tier =
        level.getBlockState(target).getBlock() instanceof TieredSoil soil ? soil.tier() : null;
    final int base = level.getGameRules().get(ModGameRules.BLIGHT_SPREAD_CHANCE);
    if (random.nextDouble() * 100 < BlightRules.spreadChance(base, tier)) {
      Blight.take(level, target);
    }
  }

  /** Ash drifts up now and then, like the spores over mycelium. */
  @Override
  public void animateTick(
      final BlockState state, final Level level, final BlockPos pos, final RandomSource random) {
    if (random.nextInt(10) == 0) {
      level.addParticle(
          ParticleTypes.ASH,
          pos.getX() + random.nextDouble(),
          pos.getY() + 1.1,
          pos.getZ() + random.nextDouble(),
          0.0,
          0.0,
          0.0);
    }
  }
}
