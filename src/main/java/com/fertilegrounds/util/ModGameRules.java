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

  private ModGameRules() {}

  /** Loads this class so the static fields above register their rules during mod startup. */
  public static void register() {}

  private static GameRule<Integer> percent(
      final String path, final int defaultValue, final GameRuleCategory category) {
    return GameRuleBuilder.forInteger(defaultValue)
        .range(0, 100)
        .category(category)
        .buildAndRegister(ModIdsUtil.id(path));
  }
}
