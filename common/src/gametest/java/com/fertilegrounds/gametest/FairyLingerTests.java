package com.fertilegrounds.gametest;

import com.fertilegrounds.entity.ModEntities;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;

/** With the default fairy_linger_seconds (60), a fairy stays after growing its patch. */
public class FairyLingerTests {

  private static final BlockPos CROP = new BlockPos(8, 2, 8);

  /** Half a minute after growing, the fairy is still drifting over the patch. */
  @FertileTest(
      structure = "fertilegrounds:empty_medium",
      environment = "fertilegrounds:no_random_ticks",
      skyAccess = true,
      maxTicks = 1200)
  public void fairyLingersAfterGrowing(final GameTestHelper helper) {
    TestSupport.plantWheat(helper, CROP);
    helper.spawn(ModEntities.BEAN_FAIRY, CROP.getX() + 0.5F, CROP.getY() + 4, CROP.getZ() + 0.5F);
    final AtomicLong grownAt = new AtomicLong(-1);
    helper.onEachTick(
        () -> {
          if (grownAt.get() < 0 && FairyVisitTests.isGrown(helper, CROP)) {
            grownAt.set(helper.getTick());
          }
        });
    helper.succeedWhen(
        () -> {
          helper.assertTrue(grownAt.get() >= 0, Component.literal("the fairy grew the wheat"));
          helper.assertTrue(
              helper.getTick() >= grownAt.get() + 600,
              Component.literal("30 seconds have passed since"));
          helper.assertFalse(
              TestSupport.fairies(helper).isEmpty(), Component.literal("the fairy is still here"));
        });
  }
}
