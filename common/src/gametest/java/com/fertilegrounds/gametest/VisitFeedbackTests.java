package com.fertilegrounds.gametest;

import com.fertilegrounds.fairy.FairyVisit;
import com.fertilegrounds.fairy.FairyVisits;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.CropBlock;

/**
 * What players hear and earn around a visit. Visits are unowned, so no gift roll gets in the way.
 */
public class VisitFeedbackTests {

  private static final BlockPos CROP = new BlockPos(8, 2, 8);

  /**
   * A scheduled visit sends a real fairy: a player within 32 blocks gets the arrival message, one
   * 50 blocks away doesn't, and once the fairy grows the crop the near player earns the first-visit
   * advancement, plus the Harvest Moon one, since the night environment is night 0's full moon.
   */
  @FertileTest(
      structure = "fertilegrounds:empty_medium",
      environment = "fertilegrounds:night",
      skyAccess = true,
      padding = 8,
      maxTicks = 600)
  public void fairyArrivalTellsOnlyNearbyPlayers(final GameTestHelper helper) {
    TestSupport.plantWheat(helper, CROP);
    // 16 blocks out: within 32 to hear it, and past the 4 blocks that make a fairy shy.
    final TestPlayer near = TestPlayer.join(helper).at(helper, 8, 2, 24);
    final TestPlayer far = TestPlayer.join(helper).at(helper, 58, 2, 8);
    FairyVisits.add(
        helper.getLevel(),
        FairyVisit.of(
            helper.absolutePos(CROP),
            helper.getLevel().getGameTime(),
            FairyVisit.Source.UNOWNED,
            null));
    helper.succeedWhen(
        () -> {
          helper.assertBlockProperty(CROP, CropBlock.AGE, CropBlock.MAX_AGE);
          helper.assertTrue(
              near.lastOverlayIs("message.fertilegrounds.fairy_arriving"),
              "near player told a fairy is arriving");
          helper.assertFalse(
              far.lastOverlayIs("message.fertilegrounds.fairy_arriving"),
              "far player told a fairy is arriving");
          helper.assertTrue(near.hasAdvancement("first_fairy_visit"), "first visit advancement");
          helper.assertTrue(
              near.hasAdvancement("harvest_moon_visit"), "Harvest Moon visit advancement");
          near.leave();
          far.leave();
        });
  }

  /**
   * Visits still pending at dawn grow then, and a player near two of them hears "came while you
   * slept" once. The arrival tick is far off, so no fairy flies during the night.
   */
  @FertileTest(
      structure = "fertilegrounds:empty_medium",
      environment = "fertilegrounds:overnight_visits",
      maxTicks = 200)
  public void dawnMessageComesOnceForTwoVisits(final GameTestHelper helper) {
    final BlockPos first = new BlockPos(4, 2, 4);
    final BlockPos second = new BlockPos(12, 2, 12);
    TestSupport.plantWheat(helper, first);
    TestSupport.plantWheat(helper, second);
    final TestPlayer player = TestPlayer.join(helper).at(helper, 8, 2, 8);
    helper
        .startSequence()
        .thenExecute(() -> TestSupport.command(helper, "time set 13000"))
        .thenExecuteAfter(
            30,
            () -> {
              final long never = helper.getLevel().getGameTime() + 1_000_000;
              for (final BlockPos crop : new BlockPos[] {first, second}) {
                FairyVisits.add(
                    helper.getLevel(),
                    FairyVisit.of(
                        helper.absolutePos(crop), never, FairyVisit.Source.UNOWNED, null));
              }
              TestSupport.command(helper, "time set 24000");
            })
        .thenExecuteAfter(
            30,
            () -> {
              helper.assertBlockProperty(first, CropBlock.AGE, CropBlock.MAX_AGE);
              helper.assertBlockProperty(second, CropBlock.AGE, CropBlock.MAX_AGE);
              helper.assertValueEqual(
                  player.chatCount("message.fertilegrounds.visited_overnight"),
                  1L,
                  "dawn messages");
              player.leave();
            })
        .thenSucceed();
  }
}
