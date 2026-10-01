package com.fertilegrounds.fairy;

import com.fertilegrounds.util.ModIdsUtil;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * The list of pending fairy visits for a world. Stored with Fabric's data attachment API, which
 * saves it alongside the world, so there's no save file handling to write here.
 */
public final class FairyVisits {

  private static final AttachmentType<List<FairyVisit>> PENDING =
      AttachmentRegistry.createPersistent(
          ModIdsUtil.id("pending_fairy_visits"), FairyVisit.CODEC.listOf());

  private FairyVisits() {}

  /** Loads this class so the attachment type is registered before any world loads. */
  public static void register() {}

  public static List<FairyVisit> get(final ServerLevel level) {
    return level.getAttachedOrElse(PENDING, List.of());
  }

  public static boolean hasVisitAt(final ServerLevel level, final BlockPos pos) {
    return get(level).stream().anyMatch(visit -> visit.pos().equals(pos));
  }

  public static void add(final ServerLevel level, final FairyVisit visit) {
    final List<FairyVisit> updated = new ArrayList<>(get(level));
    updated.add(visit);
    level.setAttached(PENDING, List.copyOf(updated));
  }

  public static void remove(final ServerLevel level, final FairyVisit visit) {
    final List<FairyVisit> updated = new ArrayList<>(get(level));
    updated.remove(visit);
    level.setAttached(PENDING, List.copyOf(updated));
  }
}
