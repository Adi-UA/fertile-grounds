package com.fertilegrounds.fairy;

import com.fertilegrounds.util.ModSavedData;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/** The list of pending fairy visits for a world, saved with it through {@link ModSavedData}. */
public final class FairyVisits {

  private FairyVisits() {}

  public static List<FairyVisit> get(final ServerLevel level) {
    return ModSavedData.PENDING_FAIRY_VISITS.get(level);
  }

  public static boolean hasVisitAt(final ServerLevel level, final BlockPos pos) {
    return get(level).stream().anyMatch(visit -> visit.pos().equals(pos));
  }

  public static void add(final ServerLevel level, final FairyVisit visit) {
    final List<FairyVisit> updated = new ArrayList<>(get(level));
    updated.add(visit);
    ModSavedData.PENDING_FAIRY_VISITS.set(level, List.copyOf(updated));
  }

  public static void remove(final ServerLevel level, final FairyVisit visit) {
    final List<FairyVisit> updated = new ArrayList<>(get(level));
    updated.remove(visit);
    ModSavedData.PENDING_FAIRY_VISITS.set(level, List.copyOf(updated));
  }
}
