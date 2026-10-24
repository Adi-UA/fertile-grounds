package com.fertilegrounds.fairy;

import org.jetbrains.annotations.Nullable;

/**
 * The rules for when a Bean Fairy shows up, kept free of Minecraft types so they can be unit tested
 * without starting the game.
 */
public final class FairyTiming {

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

  /** At most this many distinct flower types count toward the flower bonus. */
  public static final int MAX_FLOWER_TYPES = 10;

  /**
   * The gamerule values that turn a farm's surroundings into a better visit chance.
   *
   * @param flowerPercent each distinct flower type adds this percent of the base chance
   * @param hivePercent a nearby beehive adds this percent of the base chance
   * @param harvestMoonMultiplier the full-moon multiplier on the final chance
   */
  public record Bonuses(int flowerPercent, int hivePercent, int harvestMoonMultiplier) {}

  /**
   * The natural visit chance for one crop, in percent: the base chance, plus a share of it for each
   * distinct flower type (up to {@link #MAX_FLOWER_TYPES}) and for a beehive, times the Harvest
   * Moon multiplier on a full moon, capped at 100.
   */
  public static double visitChance(
      final int basePercent,
      final int flowerTypes,
      final boolean hasHive,
      final boolean fullMoon,
      final Bonuses bonuses) {
    final double flowerShare =
        Math.min(flowerTypes, MAX_FLOWER_TYPES) * bonuses.flowerPercent() / 100.0;
    final double hiveShare = hasHive ? bonuses.hivePercent() / 100.0 : 0;
    final double chance = basePercent * (1 + flowerShare + hiveShare);
    return Math.min(100, fullMoon ? chance * bonuses.harvestMoonMultiplier() : chance);
  }

  /**
   * Whether a player gets a natural visit tonight.
   *
   * @param chancePercent the chance from {@link #visitChance}, 0 to 100
   * @param roll a uniform random number from 0 (inclusive) to 100 (exclusive)
   */
  public static boolean rollsNaturalVisit(final double chancePercent, final double roll) {
    return roll < chancePercent;
  }

  /** Heralds appear this long before a fairy arrives (1 minute). */
  public static final int HERALD_LEAD_TICKS = 1200;

  /**
   * Whether fireflies should herald a pending visit: at night, from a minute before it's due. A
   * visit scheduled less than a minute ahead (Fairy Dust after dark) is heralded right away.
   */
  public static boolean isHeraldTime(
      final long arrivalTick, final long now, final boolean isNight) {
    return isNight && arrivalTick - now <= HERALD_LEAD_TICKS;
  }

  /** A pending visit happens once it's night and its arrival tick has passed. */
  public static boolean isDue(final long arrivalTick, final long now, final boolean isNight) {
    return isNight && now >= arrivalTick;
  }
}
