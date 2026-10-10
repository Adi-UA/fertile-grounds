package com.fertilegrounds.hotel;

import java.util.stream.LongStream;

/**
 * The rules for what an occupied Fairy Hotel does to its farm, kept free of world access so JUnit
 * can test them.
 */
public final class FairyHotelRules {

  private FairyHotelRules() {}

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
