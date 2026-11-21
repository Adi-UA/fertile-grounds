package com.fertilegrounds.gametest;

import com.fertilegrounds.fairy.HarvestMoon;
import net.minecraft.gametest.framework.GameTestHelper;

/**
 * Night 0 has a full moon and night 1 doesn't. The scheduler checks once a second, and only a
 * nightfall after a day counts, so each jump waits 30 ticks and a day sits between the nights.
 */
public class HarvestMoonTests {

  @FertileTest(environment = "fertilegrounds:clock_jumps", maxTicks = 400)
  public void onlyFullMoonNightsAreHarvestMoons(final GameTestHelper helper) {
    helper
        .startSequence()
        .thenExecute(() -> TestSupport.command(helper, "time set 1000"))
        .thenExecuteAfter(30, () -> TestSupport.command(helper, "time set 13000"))
        .thenExecuteAfter(
            30,
            () ->
                helper.assertTrue(
                    HarvestMoon.wasLastNight(helper.getLevel()), "night 0 is a Harvest Moon"))
        .thenExecute(() -> TestSupport.command(helper, "time set 24000"))
        .thenExecuteAfter(30, () -> TestSupport.command(helper, "time set 37000"))
        .thenExecuteAfter(
            30,
            () -> helper.assertFalse(HarvestMoon.wasLastNight(helper.getLevel()), "night 1 is not"))
        .thenSucceed();
  }
}
