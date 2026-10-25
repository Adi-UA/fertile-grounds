package com.fertilegrounds.util;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.world.level.gamerules.GameRuleType;
import net.minecraft.world.level.gamerules.GameRuleTypeVisitor;

/**
 * Every tunable number in the mod, as a per-world gamerule ({@code /gamerule
 * fertilegrounds:<name>}). Gamerules store only whole numbers, so chances are percentages from 0 to
 * 100, and code reads a rule each time it needs it, so a change applies without a restart.
 */
public final class ModGameRules {

  /** Chance per player, per night, that a fairy visits a crop near them on its own. */
  public static final GameRule<Integer> FAIRY_VISIT_CHANCE =
      percent("fairy_visit_chance", 1, GameRuleCategory.SPAWNING);

  /** Per-random-tick chance that Enriched Farmland or Enriched Sand fertilizes the plant above. */
  public static final GameRule<Integer> ENRICHED_BOOST_CHANCE =
      percent("enriched_boost_chance", 15, GameRuleCategory.UPDATES);

  /** The same chance for Super Enriched Farmland. */
  public static final GameRule<Integer> SUPER_ENRICHED_BOOST_CHANCE =
      percent("super_enriched_boost_chance", 50, GameRuleCategory.UPDATES);

  /** Each distinct fairy flower type near a crop adds this percent of the base visit chance. */
  public static final GameRule<Integer> FAIRY_FLOWER_BONUS =
      percent("fairy_flower_bonus", 10, GameRuleCategory.SPAWNING);

  /** A beehive or bee nest near a crop adds this percent of the base visit chance. */
  public static final GameRule<Integer> FAIRY_HIVE_BONUS =
      percent("fairy_hive_bonus", 50, GameRuleCategory.SPAWNING);

  /** On full-moon nights (the Harvest Moon), the visit chance is multiplied by this. */
  public static final GameRule<Integer> HARVEST_MOON_MULTIPLIER =
      integer("harvest_moon_multiplier", 3, 1, 10, GameRuleCategory.SPAWNING);

  /** How long a fairy stays drifting over its patch after growing it, in seconds. */
  public static final GameRule<Integer> FAIRY_LINGER_SECONDS =
      integer("fairy_linger_seconds", 60, 0, 600, GameRuleCategory.SPAWNING);

  /** Chance a natural visit leaves its owner a gift. */
  public static final GameRule<Integer> FAIRY_GIFT_CHANCE_NATURAL =
      percent("fairy_gift_chance_natural", 30, GameRuleCategory.DROPS);

  /** Chance a Fairy Dust visit leaves its owner a gift. */
  public static final GameRule<Integer> FAIRY_GIFT_CHANCE_DUST =
      percent("fairy_gift_chance_dust", 10, GameRuleCategory.DROPS);

  /** In-game days a player waits after one gift before they can get another. */
  public static final GameRule<Integer> FAIRY_GIFT_COOLDOWN_DAYS =
      integer("fairy_gift_cooldown_days", 3, 0, 30, GameRuleCategory.DROPS);

  /** In-game days a fairy caught in a bottle waits before it slips away and leaves a flower. */
  public static final GameRule<Integer> JARRED_FAIRY_DAYS =
      integer("jarred_fairy_days", 3, 1, 30, GameRuleCategory.MISC);

  /** Chance per random tick that an occupied Fairy Hotel advances a fairy crop near it. */
  public static final GameRule<Integer> FAIRY_HOTEL_BOOST_CHANCE =
      percent("fairy_hotel_boost_chance", 50, GameRuleCategory.UPDATES);

  /** The same chance for a hotel whose fairy was brushed since the last dawn. */
  public static final GameRule<Integer> FAIRY_HOTEL_PETTED_BOOST_CHANCE =
      percent("fairy_hotel_petted_boost_chance", 65, GameRuleCategory.UPDATES);

  /** Chance per random tick that an occupied Fairy Hotel adds a honey level to a hive near it. */
  public static final GameRule<Integer> FAIRY_HOTEL_HONEY_CHANCE =
      percent("fairy_hotel_honey_chance", 10, GameRuleCategory.UPDATES);

  /** Share of fully grown crops near a Chime Hotel that makes it ring. */
  public static final GameRule<Integer> CHIME_READY_PERCENT =
      integer("chime_ready_percent", 80, 1, 100, GameRuleCategory.MISC);

  /** Whether players near an occupied Fairy Hotel get Fairy Speed. */
  public static final GameRule<Boolean> FAIRY_HOTEL_SPEED =
      bool("fairy_hotel_speed", true, GameRuleCategory.PLAYER);

  /** Whether each player gets the Fertile Grounds Guide on their first join to a world. */
  public static final GameRule<Boolean> GIVE_GUIDE_BOOK =
      bool("give_guide_book", true, GameRuleCategory.PLAYER);

  /** Whether a diamond on a Chime Hotel shrine builds a Fairy Grove. */
  public static final GameRule<Boolean> FAIRY_GROVE =
      bool("fairy_grove", true, GameRuleCategory.MISC);

  private ModGameRules() {}

  /** Loads this class so the static fields above register their rules during mod startup. */
  public static void register() {}

  private static GameRule<Integer> percent(
      final String path, final int defaultValue, final GameRuleCategory category) {
    return integer(path, defaultValue, 0, 100, category);
  }

  private static GameRule<Integer> integer(
      final String path,
      final int defaultValue,
      final int min,
      final int max,
      final GameRuleCategory category) {
    return register(
        path,
        new GameRule<>(
            category,
            GameRuleType.INT,
            IntegerArgumentType.integer(min, max),
            GameRuleTypeVisitor::visitInteger,
            Codec.intRange(min, max),
            value -> value,
            defaultValue,
            FeatureFlagSet.of()));
  }

  private static GameRule<Boolean> bool(
      final String path, final boolean defaultValue, final GameRuleCategory category) {
    return register(
        path,
        new GameRule<>(
            category,
            GameRuleType.BOOL,
            BoolArgumentType.bool(),
            GameRuleTypeVisitor::visitBoolean,
            Codec.BOOL,
            value -> value ? 1 : 0,
            defaultValue,
            FeatureFlagSet.of()));
  }

  private static <T> GameRule<T> register(final String path, final GameRule<T> rule) {
    return Registry.register(BuiltInRegistries.GAME_RULE, ModIdsUtil.id(path), rule);
  }
}
