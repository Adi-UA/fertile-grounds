package com.fertilegrounds.gametest;

import com.fertilegrounds.util.ModGameRules;
import java.util.Map;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.gamerules.GameRule;

/** The mod's gamerules: their defaults, and the range check on a percent rule. */
public class TuningGameruleTests {

  private static final Map<GameRule<Integer>, Integer> DEFAULTS =
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

  @GameTest
  public void defaultsMatchTheSpec(final GameTestHelper helper) {
    DEFAULTS.forEach(
        (rule, expected) -> helper.assertValueEqual(rule.defaultValue(), expected, rule.id()));
    helper.assertTrue(
        ModGameRules.FAIRY_HOTEL_SPEED.defaultValue(), ModGameRules.FAIRY_HOTEL_SPEED.id());
    helper.assertTrue(
        ModGameRules.GIVE_GUIDE_BOOK.defaultValue(), ModGameRules.GIVE_GUIDE_BOOK.id());
    helper.assertTrue(ModGameRules.FAIRY_GROVE.defaultValue(), ModGameRules.FAIRY_GROVE.id());
    helper.succeed();
  }

  /** The command rejects 150 for a percent rule and accepts 100; the rule ends back at 1. */
  @GameTest
  public void percentRuleRejectsOutOfRange(final GameTestHelper helper) {
    final GameRule<Integer> rule = ModGameRules.FAIRY_VISIT_CHANCE;
    TestSupport.command(helper, "gamerule " + rule.id() + " 150");
    helper.assertValueEqual(value(helper, rule), 1, "after setting 150");
    TestSupport.command(helper, "gamerule " + rule.id() + " 100");
    helper.assertValueEqual(value(helper, rule), 100, "after setting 100");
    TestSupport.command(helper, "gamerule " + rule.id() + " 1");
    helper.succeed();
  }

  private static int value(final GameTestHelper helper, final GameRule<Integer> rule) {
    return helper.getLevel().getGameRules().get(rule);
  }
}
