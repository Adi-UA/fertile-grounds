package com.fertilegrounds.gametest;

import com.fertilegrounds.util.ModGameRules;
import java.util.List;
import java.util.Map;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.GameRules;

/** The mod's gamerules: their defaults, and the range check on a percent rule. */
public class TuningGameruleTests {

  private static final Map<GameRules.Key<GameRules.IntegerValue>, Integer> DEFAULTS =
      Map.ofEntries(
          Map.entry(ModGameRules.FAIRY_VISIT_CHANCE, 1),
          Map.entry(ModGameRules.ENRICHED_BOOST_CHANCE, 15),
          Map.entry(ModGameRules.SUPER_ENRICHED_BOOST_CHANCE, 50),
          Map.entry(ModGameRules.FAIRY_FLOWER_BONUS, 10),
          Map.entry(ModGameRules.FAIRY_HIVE_BONUS, 50),
          Map.entry(ModGameRules.HARVEST_MOON_MULTIPLIER, 3),
          Map.entry(ModGameRules.FAIRY_LINGER_SECONDS, 60),
          Map.entry(ModGameRules.FAIRY_GIFT_CHANCE_NATURAL, 30),
          Map.entry(ModGameRules.FAIRY_GIFT_CHANCE_DUST, 10),
          Map.entry(ModGameRules.FAIRY_GIFT_COOLDOWN_DAYS, 3),
          Map.entry(ModGameRules.JARRED_FAIRY_DAYS, 3),
          Map.entry(ModGameRules.FAIRY_HOTEL_BOOST_CHANCE, 50),
          Map.entry(ModGameRules.FAIRY_HOTEL_PETTED_BOOST_CHANCE, 65),
          Map.entry(ModGameRules.FAIRY_HOTEL_HONEY_CHANCE, 10),
          Map.entry(ModGameRules.CHIME_READY_PERCENT, 80));

  @FertileTest
  public void defaultsMatchTheSpec(final GameTestHelper helper) {
    // A new rule set holds every rule at its default.
    final GameRules defaults = new GameRules();
    DEFAULTS.forEach(
        (rule, expected) -> helper.assertValueEqual(defaults.getInt(rule), expected, rule.getId()));
    for (final GameRules.Key<GameRules.BooleanValue> rule :
        List.of(
            ModGameRules.FAIRY_HOTEL_SPEED,
            ModGameRules.GIVE_GUIDE_BOOK,
            ModGameRules.FAIRY_GROVE)) {
      helper.assertTrue(defaults.getBoolean(rule), rule.getId());
    }
    helper.succeed();
  }

  /** The command rejects 150 for a percent rule and accepts 100; the rule ends back at 1. */
  @FertileTest
  public void percentRuleRejectsOutOfRange(final GameTestHelper helper) {
    final GameRules.Key<GameRules.IntegerValue> rule = ModGameRules.FAIRY_VISIT_CHANCE;
    TestSupport.command(helper, "gamerule " + rule.getId() + " 150");
    helper.assertValueEqual(value(helper, rule), 1, "after setting 150");
    TestSupport.command(helper, "gamerule " + rule.getId() + " 100");
    helper.assertValueEqual(value(helper, rule), 100, "after setting 100");
    TestSupport.command(helper, "gamerule " + rule.getId() + " 1");
    helper.succeed();
  }

  private static int value(
      final GameTestHelper helper, final GameRules.Key<GameRules.IntegerValue> rule) {
    return helper.getLevel().getGameRules().getInt(rule);
  }
}
