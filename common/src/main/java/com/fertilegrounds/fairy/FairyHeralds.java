package com.fertilegrounds.fairy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

/**
 * Glowing motes that drift over a patch in the minute before a fairy arrives, so a player nearby
 * can tell a visit is coming. Only shown when a player is close enough to see them.
 */
final class FairyHeralds {

  /** Heralds update twice a second. */
  static final int INTERVAL_TICKS = 10;

  /** The chime plays every 5 seconds, not every update. */
  private static final int SOUND_INTERVAL_TICKS = 100;

  private static final double PLAYER_DISTANCE = 32;

  private FairyHeralds() {}

  static void tick(final ServerLevel level) {
    final long now = level.getGameTime();
    final boolean isNight = level.isNight();
    for (final FairyVisit visit : FairyVisits.get(level)) {
      final BlockPos pos = visit.pos();
      if (!FairyTiming.isHeraldTime(visit.arrivalTick(), now, isNight)
          || !level.isLoaded(pos)
          || !level.hasNearbyAlivePlayer(
              pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, PLAYER_DISTANCE)) {
        continue;
      }
      level.sendParticles(
          ParticleTypes.GLOW,
          pos.getX() + 0.5,
          pos.getY() + 1.5,
          pos.getZ() + 0.5,
          3,
          FairyVisitScheduler.GROWTH_RADIUS,
          0.5,
          FairyVisitScheduler.GROWTH_RADIUS,
          0.0);
      if (now % SOUND_INTERVAL_TICKS == 0) {
        level.playSound(
            null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.AMBIENT, 0.3F, 1.5F);
      }
    }
  }
}
