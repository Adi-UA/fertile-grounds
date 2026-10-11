package com.fertilegrounds.fairy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fertilegrounds.fairy.FairyTiming.Transition;
import org.junit.jupiter.api.Test;

class FairyTimingTest {

  @Test
  void firstCheckAfterStartupIsNeverATransition() {
    assertEquals(Transition.NONE, FairyTiming.transition(null, true));
    assertEquals(Transition.NONE, FairyTiming.transition(null, false));
  }

  @Test
  void detectsNightfallAndDawn() {
    assertEquals(Transition.NIGHT_STARTED, FairyTiming.transition(false, true));
    assertEquals(Transition.DAY_STARTED, FairyTiming.transition(true, false));
  }

  @Test
  void noTransitionWhileStateHolds() {
    assertEquals(Transition.NONE, FairyTiming.transition(true, true));
    assertEquals(Transition.NONE, FairyTiming.transition(false, false));
  }

  @Test
  void dustUsedDuringDayIsDueAsSoonAsNightFalls() {
    final long usedAt = 1000;
    final long arrival = FairyTiming.dustArrivalTick(usedAt, false, 400);
    assertFalse(FairyTiming.isDue(arrival, usedAt + 5000, false));
    assertTrue(FairyTiming.isDue(arrival, usedAt + 5000, true));
  }

  @Test
  void dustUsedAtNightWaitsForTheDelay() {
    final long usedAt = 1000;
    final long arrival = FairyTiming.dustArrivalTick(usedAt, true, 400);
    assertFalse(FairyTiming.isDue(arrival, usedAt + 399, true));
    assertTrue(FairyTiming.isDue(arrival, usedAt + 400, true));
  }

  @Test
  void nothingIsDueDuringTheDay() {
    assertFalse(FairyTiming.isDue(0, 1_000_000, false));
  }

  private static final FairyTiming.Bonuses DEFAULTS = new FairyTiming.Bonuses(10, 50, 3);

  @Test
  void zeroChanceNeverRollsAVisit() {
    assertFalse(FairyTiming.rollsNaturalVisit(0, 0.0));
  }

  @Test
  void rollMustLandUnderTheChance() {
    assertTrue(FairyTiming.rollsNaturalVisit(1.3, 1.29));
    assertFalse(FairyTiming.rollsNaturalVisit(1.3, 1.3));
  }

  @Test
  void fullChanceAlwaysRollsAVisit() {
    assertTrue(FairyTiming.rollsNaturalVisit(100, 99.999));
  }

  @Test
  void noBonusesKeepTheBaseChance() {
    assertEquals(1.0, FairyTiming.visitChance(1, 0, false, false, DEFAULTS), 1e-9);
  }

  @Test
  void eachFlowerTypeAddsATenthOfTheBase() {
    assertEquals(1.3, FairyTiming.visitChance(1, 3, false, false, DEFAULTS), 1e-9);
  }

  @Test
  void flowerTypesAreCappedAtTen() {
    assertEquals(
        FairyTiming.visitChance(1, 10, false, false, DEFAULTS),
        FairyTiming.visitChance(1, 14, false, false, DEFAULTS),
        1e-9);
  }

  @Test
  void aHiveAddsHalfTheBase() {
    assertEquals(1.5, FairyTiming.visitChance(1, 0, true, false, DEFAULTS), 1e-9);
  }

  @Test
  void harvestMoonTriplesAFullFarm() {
    assertEquals(7.5, FairyTiming.visitChance(1, 10, true, true, DEFAULTS), 1e-9);
  }

  @Test
  void chanceNeverPassesOneHundred() {
    assertEquals(100.0, FairyTiming.visitChance(50, 10, true, true, DEFAULTS), 1e-9);
  }

  @Test
  void heraldsStartAMinuteBeforeArrival() {
    assertFalse(FairyTiming.isHeraldTime(10_000, 10_000 - 1201, true));
    assertTrue(FairyTiming.isHeraldTime(10_000, 10_000 - 1200, true));
  }

  @Test
  void aVisitDueSoonIsHeraldedRightAway() {
    assertTrue(FairyTiming.isHeraldTime(10_000 + 600, 10_000, true));
  }

  @Test
  void noHeraldsDuringTheDay() {
    assertFalse(FairyTiming.isHeraldTime(10_000, 10_000, false));
  }
}
