package com.fertilegrounds.fairy;

import org.jetbrains.annotations.Nullable;

/**
 * The rules for when a Bean Fairy shows up, kept free of Minecraft types so they can be unit tested
 * without starting the game.
 */
public final class FairyTiming {

  /** Chance per player, per night, that a fairy visits a crop near them on its own. */
  public static final float NATURAL_VISIT_CHANCE = 0.01F;

  /** How far into the night a natural visit can land (10 seconds to 5 minutes, in ticks). */
  public static final int NATURAL_DELAY_MIN_TICKS = 200;

  public static final int NATURAL_DELAY_MAX_TICKS = 6000;

  /** How long a fairy takes to arrive when dust is used after dark (10 to 30 seconds). */
  public static final int DUST_DELAY_MIN_TICKS = 200;

  public static final int DUST_DELAY_MAX_TICKS = 600;

  /** What changed between the previous check and this one. */
  public enum Transition {
    NONE,
    NIGHT_STARTED,
    DAY_STARTED
  }

  private FairyTiming() {}

  /**
   * @param wasNight the night state from the previous check, or null on the first check after the
   *     server starts (no transition is reported then, so a restart at night doesn't count as a new
   *     night)
   * @param isNight the night state now
   */
  public static Transition transition(@Nullable final Boolean wasNight, final boolean isNight) {
    if (wasNight == null || wasNight == isNight) {
      return Transition.NONE;
    }
    return isNight ? Transition.NIGHT_STARTED : Transition.DAY_STARTED;
  }

  /**
   * The earliest game tick a dust-summoned fairy may arrive. During the day there is no delay to
   * add, because visits only ever happen at night: the fairy comes as soon as night falls.
   */
  public static long dustArrivalTick(final long now, final boolean isNight, final int delayTicks) {
    return isNight ? now + delayTicks : now;
  }

  /** A pending visit happens once it's night and its arrival tick has passed. */
  public static boolean isDue(final long arrivalTick, final long now, final boolean isNight) {
    return isNight && now >= arrivalTick;
  }
}
