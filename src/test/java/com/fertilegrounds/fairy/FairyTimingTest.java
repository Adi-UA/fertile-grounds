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
}
