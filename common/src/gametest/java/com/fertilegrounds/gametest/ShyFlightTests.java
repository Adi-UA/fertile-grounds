package com.fertilegrounds.gametest;

import com.fertilegrounds.entity.BeanFairy;
import com.fertilegrounds.entity.ModEntities;
import com.fertilegrounds.fairy.ShyFlight;
import com.google.common.util.concurrent.AtomicDouble;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;

/**
 * A lingering fairy darts away from a player who walks up within 4 blocks, but not from one who
 * sneaks up.
 */
public class ShyFlightTests {

  private static final BlockPos CROP = new BlockPos(8, 2, 8);

  /** Ticks after growing before the player steps in, so the fairy is lingering. */
  private static final int LINGER_START_TICKS = 20;

  /**
   * Ticks the player stands there: two 20-tick darts. A dart ends with the fairy drifting back, so
   * the test tracks the farthest it got rather than where it is at the end.
   */
  private static final int WATCH_TICKS = 40;

  @FertileTest(
      structure = "fertilegrounds:empty_medium",
      environment = "fertilegrounds:no_random_ticks",
      skyAccess = true,
      maxTicks = 600)
  public void fairyDartsFromAWalkingPlayer(final GameTestHelper helper) {
    approach(helper, false);
  }

  @FertileTest(
      structure = "fertilegrounds:empty_medium",
      environment = "fertilegrounds:no_random_ticks",
      skyAccess = true,
      maxTicks = 600)
  public void fairyStaysForASneakingPlayer(final GameTestHelper helper) {
    approach(helper, true);
  }

  /**
   * Once the fairy has grown the crop, a player steps in 2 blocks from it; the fairy gets at least
   * 4 blocks away from them only if they weren't sneaking.
   */
  private static void approach(final GameTestHelper helper, final boolean sneaking) {
    TestSupport.plantWheat(helper, CROP);
    // Kept, not looked up: a search around the test can find the next test's fairy.
    final BeanFairy fairy =
        helper.spawn(
            ModEntities.BEAN_FAIRY, CROP.getX() + 0.5F, CROP.getY() + 4, CROP.getZ() + 0.5F);
    final TestPlayer player = TestPlayer.join(helper).at(helper, 8, 2, 24);
    player.setShiftKeyDown(sneaking);
    final AtomicLong grownAt = new AtomicLong(-1);
    final AtomicDouble farthest = new AtomicDouble();
    helper.onEachTick(
        () -> {
          if (grownAt.get() < 0 && FairyVisitTests.isGrown(helper, CROP)) {
            grownAt.set(helper.getTick());
          }
          if (grownAt.get() >= 0 && helper.getTick() == grownAt.get() + LINGER_START_TICKS) {
            player.setPos(fairy.getX() + 2, fairy.getY(), fairy.getZ());
          }
          if (grownAt.get() >= 0 && helper.getTick() > grownAt.get() + LINGER_START_TICKS) {
            farthest.set(Math.max(farthest.get(), fairy.distanceTo(player)));
          }
        });
    helper.succeedWhen(
        () -> {
          helper.assertTrue(
              grownAt.get() >= 0
                  && helper.getTick() >= grownAt.get() + LINGER_START_TICKS + WATCH_TICKS,
              Component.literal(
                  "the player has stood by the lingering fairy for two darts' worth of ticks"));
          helper.assertValueEqual(
              farthest.get() >= ShyFlight.SCARE_DISTANCE,
              !sneaking,
              Component.literal("fairy got at least 4 blocks away"));
          player.leave();
        });
  }
}
