package com.fertilegrounds.gametest;

import com.fertilegrounds.util.ModGameRules;
import net.minecraft.gametest.framework.BeforeBatch;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;

/**
 * The time, weather, and gamerules each group of tests runs with. Tests naming the same {@link
 * FertileTest#environment} run together as one vanilla batch, and each batch's setup here runs
 * before its tests start. Every setup first puts back the world the test server started with, so no
 * batch inherits another's changes. Tests that move the clock or change gamerules get their own
 * environment, so they never run alongside tests that expect the defaults.
 */
public class TestEnvironments {

  /** The rules and time the test server started with, read before the first batch. */
  private static GameRules startRules;

  private static long startTime;

  @BeforeBatch(batch = "minecraft:default")
  public void defaults(final ServerLevel level) {
    reset(level);
  }

  @BeforeBatch(batch = "fertilegrounds:no_players")
  public void noPlayers(final ServerLevel level) {
    reset(level);
  }

  @BeforeBatch(batch = "fertilegrounds:no_random_ticks")
  public void noRandomTicks(final ServerLevel level) {
    quiet(level);
  }

  @BeforeBatch(batch = "fertilegrounds:blight_overnight")
  public void blightOvernight(final ServerLevel level) {
    quiet(level);
  }

  @BeforeBatch(batch = "fertilegrounds:chime_petting")
  public void chimePetting(final ServerLevel level) {
    quiet(level);
  }

  @BeforeBatch(batch = "fertilegrounds:clock_jumps")
  public void clockJumps(final ServerLevel level) {
    quiet(level);
  }

  @BeforeBatch(batch = "fertilegrounds:grove")
  public void grove(final ServerLevel level) {
    quiet(level);
  }

  @BeforeBatch(batch = "fertilegrounds:hotel_petting")
  public void hotelPetting(final ServerLevel level) {
    quiet(level);
  }

  @BeforeBatch(batch = "fertilegrounds:jar_expiry")
  public void jarExpiry(final ServerLevel level) {
    quiet(level);
  }

  @BeforeBatch(batch = "fertilegrounds:overnight_visits")
  public void overnightVisits(final ServerLevel level) {
    quiet(level);
  }

  @BeforeBatch(batch = "fertilegrounds:night")
  public void night(final ServerLevel level) {
    quiet(level);
    level.setDayTime(18000);
  }

  @BeforeBatch(batch = "fertilegrounds:quick_visit")
  public void quickVisit(final ServerLevel level) {
    quiet(level);
    setInt(level, ModGameRules.FAIRY_LINGER_SECONDS, 0);
  }

  @BeforeBatch(batch = "fertilegrounds:grove_off")
  public void groveOff(final ServerLevel level) {
    quiet(level);
    setBool(level, ModGameRules.FAIRY_GROVE, false);
  }

  @BeforeBatch(batch = "fertilegrounds:gifts")
  public void gifts(final ServerLevel level) {
    quiet(level);
    setInt(level, ModGameRules.FAIRY_GIFT_CHANCE_NATURAL, 100);
    setInt(level, ModGameRules.FAIRY_GIFT_CHANCE_DUST, 100);
  }

  @BeforeBatch(batch = "fertilegrounds:gift_cooldown")
  public void giftCooldown(final ServerLevel level) {
    gifts(level);
  }

  @BeforeBatch(batch = "fertilegrounds:natural_visits")
  public void naturalVisits(final ServerLevel level) {
    quiet(level);
    setInt(level, ModGameRules.FAIRY_VISIT_CHANCE, 100);
  }

  @BeforeBatch(batch = "fertilegrounds:fast_random_ticks")
  public void fastRandomTicks(final ServerLevel level) {
    reset(level);
    setInt(level, GameRules.RULE_RANDOMTICKING, 3000);
    setInt(level, ModGameRules.FAIRY_VISIT_CHANCE, 0);
  }

  @BeforeBatch(batch = "fertilegrounds:hotel_growth")
  public void hotelGrowth(final ServerLevel level) {
    reset(level);
    setInt(level, GameRules.RULE_RANDOMTICKING, 100);
    setInt(level, ModGameRules.FAIRY_VISIT_CHANCE, 0);
    setInt(level, ModGameRules.FAIRY_HOTEL_HONEY_CHANCE, 0);
  }

  @BeforeBatch(batch = "fertilegrounds:hotel_honey")
  public void hotelHoney(final ServerLevel level) {
    reset(level);
    setInt(level, GameRules.RULE_RANDOMTICKING, 100);
    setInt(level, ModGameRules.FAIRY_VISIT_CHANCE, 0);
    setInt(level, ModGameRules.FAIRY_HOTEL_BOOST_CHANCE, 0);
    setInt(level, ModGameRules.FAIRY_HOTEL_HONEY_CHANCE, 100);
  }

  @BeforeBatch(batch = "fertilegrounds:hotel_speed_off")
  public void hotelSpeedOff(final ServerLevel level) {
    quiet(level);
    setBool(level, ModGameRules.FAIRY_HOTEL_SPEED, false);
  }

  @BeforeBatch(batch = "fertilegrounds:no_guide_book")
  public void noGuideBook(final ServerLevel level) {
    reset(level);
    setBool(level, ModGameRules.GIVE_GUIDE_BOOK, false);
  }

  /** No random ticks, no natural fairy visits, and clear skies, so only the test changes things. */
  private static void quiet(final ServerLevel level) {
    reset(level);
    setInt(level, GameRules.RULE_RANDOMTICKING, 0);
    setInt(level, ModGameRules.FAIRY_VISIT_CHANCE, 0);
    level.setWeatherParameters(20000000, 0, false, false);
  }

  private static void reset(final ServerLevel level) {
    if (startRules == null) {
      startRules = level.getGameRules().copy();
      startTime = level.getDayTime();
    }
    level.getGameRules().assignFrom(startRules, level.getServer());
    level.setDayTime(startTime);
  }

  private static void setInt(
      final ServerLevel level, final GameRules.Key<GameRules.IntegerValue> rule, final int value) {
    level.getGameRules().getRule(rule).set(value, level.getServer());
  }

  private static void setBool(
      final ServerLevel level,
      final GameRules.Key<GameRules.BooleanValue> rule,
      final boolean value) {
    level.getGameRules().getRule(rule).set(value, level.getServer());
  }
}
