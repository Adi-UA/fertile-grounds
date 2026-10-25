package com.fertilegrounds.util;

import com.fertilegrounds.platform.Platform;
import net.minecraft.world.level.GameRules;

/**
 * Every tunable number in the mod, as a per-world gamerule ({@code /gamerule
 * fertilegrounds:<name>}). Gamerules store only whole numbers, so chances are percentages from 0 to
 * 100, and code reads a rule each time it needs it, so a change applies without a restart. Each
 * loader registers the rules ({@link Platform#intRule}), since this Minecraft version keeps that
 * private.
 */
public final class ModGameRules {

  /** Chance per player, per night, that a fairy visits a crop near them on its own. */
  public static final GameRules.Key<GameRules.IntegerValue> FAIRY_VISIT_CHANCE =
      percent("fairy_visit_chance", 1, GameRules.Category.SPAWNING);

  /** Per-random-tick chance that Enriched Farmland or Enriched Sand fertilizes the plant above. */
  public static final GameRules.Key<GameRules.IntegerValue> ENRICHED_BOOST_CHANCE =
      percent("enriched_boost_chance", 15, GameRules.Category.UPDATES);

  /** The same chance for Super Enriched Farmland. */
  public static final GameRules.Key<GameRules.IntegerValue> SUPER_ENRICHED_BOOST_CHANCE =
      percent("super_enriched_boost_chance", 50, GameRules.Category.UPDATES);

  /** Each distinct fairy flower type near a crop adds this percent of the base visit chance. */
  public static final GameRules.Key<GameRules.IntegerValue> FAIRY_FLOWER_BONUS =
      percent("fairy_flower_bonus", 10, GameRules.Category.SPAWNING);

  /** A beehive or bee nest near a crop adds this percent of the base visit chance. */
  public static final GameRules.Key<GameRules.IntegerValue> FAIRY_HIVE_BONUS =
      percent("fairy_hive_bonus", 50, GameRules.Category.SPAWNING);

  /** On full-moon nights (the Harvest Moon), the visit chance is multiplied by this. */
  public static final GameRules.Key<GameRules.IntegerValue> HARVEST_MOON_MULTIPLIER =
      integer("harvest_moon_multiplier", 3, 1, 10, GameRules.Category.SPAWNING);

  /** How long a fairy stays drifting over its patch after growing it, in seconds. */
  public static final GameRules.Key<GameRules.IntegerValue> FAIRY_LINGER_SECONDS =
      integer("fairy_linger_seconds", 60, 0, 600, GameRules.Category.SPAWNING);

  /** Chance a natural visit leaves its owner a gift. */
  public static final GameRules.Key<GameRules.IntegerValue> FAIRY_GIFT_CHANCE_NATURAL =
      percent("fairy_gift_chance_natural", 30, GameRules.Category.DROPS);

  /** Chance a Fairy Dust visit leaves its owner a gift. */
  public static final GameRules.Key<GameRules.IntegerValue> FAIRY_GIFT_CHANCE_DUST =
      percent("fairy_gift_chance_dust", 10, GameRules.Category.DROPS);

  /** In-game days a player waits after one gift before they can get another. */
  public static final GameRules.Key<GameRules.IntegerValue> FAIRY_GIFT_COOLDOWN_DAYS =
      integer("fairy_gift_cooldown_days", 3, 0, 30, GameRules.Category.DROPS);

  /** In-game days a fairy caught in a bottle waits before it slips away and leaves a flower. */
  public static final GameRules.Key<GameRules.IntegerValue> JARRED_FAIRY_DAYS =
      integer("jarred_fairy_days", 3, 1, 30, GameRules.Category.MISC);

  /** Chance per random tick that an occupied Fairy Hotel advances a fairy crop near it. */
  public static final GameRules.Key<GameRules.IntegerValue> FAIRY_HOTEL_BOOST_CHANCE =
      percent("fairy_hotel_boost_chance", 50, GameRules.Category.UPDATES);

  /** The same chance for a hotel whose fairy was brushed since the last dawn. */
  public static final GameRules.Key<GameRules.IntegerValue> FAIRY_HOTEL_PETTED_BOOST_CHANCE =
      percent("fairy_hotel_petted_boost_chance", 65, GameRules.Category.UPDATES);

  /** Chance per random tick that an occupied Fairy Hotel adds a honey level to a hive near it. */
  public static final GameRules.Key<GameRules.IntegerValue> FAIRY_HOTEL_HONEY_CHANCE =
      percent("fairy_hotel_honey_chance", 10, GameRules.Category.UPDATES);

  /** Share of fully grown crops near a Chime Hotel that makes it ring. */
  public static final GameRules.Key<GameRules.IntegerValue> CHIME_READY_PERCENT =
      integer("chime_ready_percent", 80, 1, 100, GameRules.Category.MISC);

  /** Whether players near an occupied Fairy Hotel get Fairy Speed. */
  public static final GameRules.Key<GameRules.BooleanValue> FAIRY_HOTEL_SPEED =
      bool("fairy_hotel_speed", true, GameRules.Category.PLAYER);

  /** Whether each player gets the Fertile Grounds Guide on their first join to a world. */
  public static final GameRules.Key<GameRules.BooleanValue> GIVE_GUIDE_BOOK =
      bool("give_guide_book", true, GameRules.Category.PLAYER);

  /** Whether a diamond on a Chime Hotel shrine builds a Fairy Grove. */
  public static final GameRules.Key<GameRules.BooleanValue> FAIRY_GROVE =
      bool("fairy_grove", true, GameRules.Category.MISC);

  private ModGameRules() {}

  /** Loads this class so the static fields above register their rules during mod startup. */
  public static void register() {}

  private static GameRules.Key<GameRules.IntegerValue> percent(
      final String path, final int defaultValue, final GameRules.Category category) {
    return integer(path, defaultValue, 0, 100, category);
  }

  private static GameRules.Key<GameRules.IntegerValue> integer(
      final String path,
      final int defaultValue,
      final int min,
      final int max,
      final GameRules.Category category) {
    return Platform.INSTANCE.intRule(name(path), category, defaultValue, min, max);
  }

  private static GameRules.Key<GameRules.BooleanValue> bool(
      final String path, final boolean defaultValue, final GameRules.Category category) {
    return Platform.INSTANCE.boolRule(name(path), category, defaultValue);
  }

  /** The name {@code /gamerule} shows, such as {@code fertilegrounds:fairy_visit_chance}. */
  private static String name(final String path) {
    return ModIdsUtil.id(path).toString();
  }
}
