package com.fertilegrounds.fairy;

import com.fertilegrounds.FertileGrounds;
import com.fertilegrounds.entity.BeanFairy;
import com.fertilegrounds.util.ModCriteria;
import com.fertilegrounds.util.ModSounds;
import java.util.Collection;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

/**
 * What happens around a fairy visit, in one place, so a fairy that flew and a visit grown at dawn
 * give players the same feedback.
 */
public final class FairyVisitEvents {

  /** Players this close to a visit hear about it. */
  private static final double NEARBY_DISTANCE = 32;

  private FairyVisitEvents() {}

  /**
   * Called once each time a visit grows crops.
   *
   * @param overnight true when the visit grew at dawn because no fairy flew during the night
   * @return the players near the visit
   */
  public static List<ServerPlayer> onVisitGrown(
      final ServerLevel level, final FairyVisit visit, final boolean overnight) {
    final BlockPos pos = visit.pos();
    FertileGrounds.LOGGER.info(
        "Fairy visit grew crops at {}{}", pos.toShortString(), overnight ? " overnight" : "");
    final boolean harvestMoon =
        overnight ? HarvestMoon.wasLastNight(level) : HarvestMoon.isTonight(level);
    final List<ServerPlayer> nearby = playersNear(level, pos);
    for (final ServerPlayer player : nearby) {
      ModCriteria.FAIRY_VISIT.trigger(player);
      if (harvestMoon) {
        ModCriteria.HARVEST_MOON_VISIT.trigger(player);
      }
    }
    FairyGiftDrops.maybeDrop(level, visit, harvestMoon);
    return nearby;
  }

  /** A fairy just appeared for a visit: chime, a few sparkles, and a note to players nearby. */
  public static void onFairyArrived(final ServerLevel level, final BeanFairy fairy) {
    fairy.playSound(ModSounds.BEAN_FAIRY_ARRIVE, 1.0F, 1.0F);
    level.sendParticles(
        ParticleTypes.END_ROD, fairy.getX(), fairy.getY(), fairy.getZ(), 5, 0.2, 0.2, 0.2, 0.01);
    for (final ServerPlayer player : playersNear(level, fairy.blockPosition())) {
      player.sendOverlayMessage(Component.translatable("message.fertilegrounds.fairy_arriving"));
    }
  }

  /**
   * Tells players that a fairy came while they slept. The caller gathers players from every visit
   * grown that dawn into one set, so each player hears it once.
   */
  public static void tellVisitedOvernight(final Collection<ServerPlayer> players) {
    for (final ServerPlayer player : players) {
      player.sendSystemMessage(Component.translatable("message.fertilegrounds.visited_overnight"));
    }
  }

  private static List<ServerPlayer> playersNear(final ServerLevel level, final BlockPos pos) {
    final Vec3 center = Vec3.atCenterOf(pos);
    return level.getPlayers(
        player -> player.distanceToSqr(center) <= NEARBY_DISTANCE * NEARBY_DISTANCE);
  }
}
