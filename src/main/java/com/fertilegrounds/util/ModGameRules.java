package com.fertilegrounds.util;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;

/**
 * Every tunable number in the mod, as a per-world gamerule ({@code /gamerule
 * fertilegrounds:<name>}). Gamerules only store whole numbers, so chances are percentages from 0 to
 * 100. Code reads a rule each time it needs it, so a change applies without a restart.
 */
public final class ModGameRules {

  /** Chance per player, per night, that a fairy visits a crop near them on its own. */
  public static final GameRule<Integer> FAIRY_VISIT_CHANCE =
      percent("fairy_visit_chance", 1, GameRuleCategory.SPAWNING);

  /** Per-random-tick chance that Enriched Farmland or Enriched Sand fertilizes the plant above. */
  public static final GameRule<Integer> ENRICHED_BOOST_CHANCE =
      percent("enriched_boost_chance", 15, GameRuleCategory.UPDATES);

  /** Same for Super Enriched Farmland: about 3.3x tier 1, gated behind Glowstone Dust. */
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
    return GameRuleBuilder.forInteger(defaultValue)
        .range(min, max)
        .category(category)
        .buildAndRegister(ModIdsUtil.id(path));
  }
}
