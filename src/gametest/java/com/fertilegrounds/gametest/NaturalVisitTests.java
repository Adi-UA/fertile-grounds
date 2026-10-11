package com.fertilegrounds.gametest;

import com.fertilegrounds.fairy.FairyVisit;
import com.fertilegrounds.fairy.FairyVisits;
import java.util.Optional;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;

/**
 * At {@code fairy_visit_chance 100}, nightfall schedules a natural visit to the crop near a player,
 * owned by that player. Regression for b4c5267: a lone crop used to be missed. The scheduler checks
 * once a second and only a nightfall after a day counts, so the clock jumps to day, then to night,
 * waiting 30 ticks each time.
 */
public class NaturalVisitTests {

  /**
   * The crop sits at the center of the 33x33 structure, so the scheduler's whole 16-block search
   * area is inside it and cleared at the start, with no crops left over from earlier tests.
   */
  private static final BlockPos CROP = new BlockPos(16, 2, 16);

  @GameTest(
      structure = "fertilegrounds:empty_large",
      environment = "fertilegrounds:natural_visits",
      maxTicks = 200)
  public void nightfallSchedulesAVisitNearThePlayer(final GameTestHelper helper) {
    TestSupport.plantWheat(helper, CROP);
    final TestPlayer player = TestPlayer.join(helper).at(helper, 14, 2, 14);
    final BlockPos crop = helper.absolutePos(CROP);
    helper
        .startSequence()
        .thenExecute(() -> TestSupport.command(helper, "time set 1000"))
        .thenExecuteAfter(30, () -> TestSupport.command(helper, "time set 13000"))
        .thenExecuteAfter(
            30,
            () -> {
              final FairyVisit visit =
                  FairyVisits.get(helper.getLevel()).stream()
                      .filter(v -> v.pos().equals(crop))
                      .findFirst()
                      .orElseThrow(
                          () ->
                              helper.assertionException(
                                  Component.literal("no natural visit scheduled")));
              helper.assertValueEqual(visit.source(), FairyVisit.Source.NATURAL, "visit source");
              helper.assertValueEqual(visit.owner(), Optional.of(player.getUUID()), "visit owner");
              FairyVisits.remove(helper.getLevel(), visit);
              player.leave();
            })
        .thenSucceed();
  }
}
