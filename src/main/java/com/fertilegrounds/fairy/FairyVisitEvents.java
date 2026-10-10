package com.fertilegrounds.fairy;

import com.fertilegrounds.FertileGrounds;
import java.util.List;
import net.minecraft.core.BlockPos;
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
      final ServerLevel level, final BlockPos pos, final boolean overnight) {
    FertileGrounds.LOGGER.info(
        "Fairy visit grew crops at {}{}", pos.toShortString(), overnight ? " overnight" : "");
    return playersNear(level, pos);
  }

  private static List<ServerPlayer> playersNear(final ServerLevel level, final BlockPos pos) {
    final Vec3 center = Vec3.atCenterOf(pos);
    return level.getPlayers(
        player -> player.distanceToSqr(center) <= NEARBY_DISTANCE * NEARBY_DISTANCE);
  }
}
