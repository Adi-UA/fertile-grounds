package com.fertilegrounds.hotel;

import java.util.List;
import java.util.function.IntUnaryOperator;

/**
 * The rules for a fairy caught in a bottle, kept free of world access so JUnit can test them: when
 * it slips away, and which flower it leaves behind.
 */
public final class JarredFairyRules {

  /** One in-game day, in game ticks. */
  public static final long TICKS_PER_DAY = 24000;

  private JarredFairyRules() {}

  /**
   * The game time a fairy caught at {@code caughtAt} slips away. It is stored on the item, so the
   * fairy ages the same in a chest, an inventory, or an unloaded chunk without ticking.
   */
  public static long expiresAt(final long caughtAt, final int days) {
    return caughtAt + days * TICKS_PER_DAY;
  }

  public static boolean hasSlippedAway(final long expiresAt, final long now) {
    return now >= expiresAt;
  }

  /**
   * The flower a fairy leaves behind: a random one of {@code flowers}, or {@code fallback} when a
   * pack has emptied the tag.
   *
   * @param nextInt returns a random number from 0 up to (not including) its argument
   */
  public static <T> T leftFlower(
      final List<T> flowers, final T fallback, final IntUnaryOperator nextInt) {
    return flowers.isEmpty() ? fallback : flowers.get(nextInt.applyAsInt(flowers.size()));
  }
}
