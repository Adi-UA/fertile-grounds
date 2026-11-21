package com.fertilegrounds.hotel;

import java.util.stream.LongStream;

/**
 * The rules for what an occupied Fairy Hotel does to its farm, kept free of world access so JUnit
 * can test them.
 */
public final class FairyHotelRules {

  /** A bee carrying nectar near a hotel pollinates for it: one boost attempt more. */
  public static final int BEE_EXTRA_ATTEMPTS = 1;

  private FairyHotelRules() {}

  /** Ticks from {@code clockTime} to the next dawn (clock time 0 of the next day). */
  public static int ticksUntilDawn(final long clockTime) {
    return (int)
        (JarredFairyRules.TICKS_PER_DAY - Math.floorMod(clockTime, JarredFairyRules.TICKS_PER_DAY));
  }

  /**
   * Whether {@code clockTime} falls within {@code window} ticks after a dawn, so a check that runs
   * every {@code window} ticks sees each dawn exactly once.
   */
  public static boolean isJustAfterDawn(final long clockTime, final int window) {
    return Math.floorMod(clockTime, JarredFairyRules.TICKS_PER_DAY) < window;
  }

  /** How many crop boost attempts a hotel makes on one random tick. */
  public static int boostAttempts(final boolean nectarBeeNearby) {
    return 1 + (nectarBeeNearby ? BEE_EXTRA_ATTEMPTS : 0);
  }

  /**
   * Whether the hotel at {@code self} leads its cluster, so it alone boosts crops and adds honey.
   * The leader is the hotel with the smallest packed position ({@code BlockPos.asLong()}) among
   * those near it, so every hotel works out the same answer with no shared state.
   *
   * @param hotelsNearby the packed positions of occupied hotels near {@code self}, which may
   *     include {@code self}
   */
  public static boolean isClusterLeader(final long self, final LongStream hotelsNearby) {
    return hotelsNearby.allMatch(other -> self <= other);
  }
}
