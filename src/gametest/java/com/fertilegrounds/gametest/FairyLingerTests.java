package com.fertilegrounds.gametest;

import com.fertilegrounds.entity.ModEntities;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;

/** With the default fairy_linger_seconds (60), a fairy stays after growing its patch. */
public class FairyLingerTests {

  private static final BlockPos CROP = new BlockPos(8, 2, 8);

  /** Half a minute after growing, the fairy is still drifting over the patch. */
  @GameTest(
      structure = "fertilegrounds:empty_medium",
      environment = "fertilegrounds:no_random_ticks",
      skyAccess = true,
      padding = 8,
      maxTicks = 1200)
  public void fairyLingersAfterGrowing(final GameTestHelper helper) {
    helper.setBlock(CROP.below(), Blocks.FARMLAND);
    helper.setBlock(CROP, Blocks.WHEAT);
    helper.spawn(ModEntities.BEAN_FAIRY, CROP.getX() + 0.5F, CROP.getY() + 4, CROP.getZ() + 0.5F);
    final long[] grownAt = {-1};
    helper.onEachTick(
        () -> {
          if (grownAt[0] < 0 && FairyVisitTests.isGrown(helper, CROP)) {
            grownAt[0] = helper.getTick();
          }
        });
    helper.succeedWhen(
        () -> {
          helper.assertTrue(grownAt[0] >= 0, "the fairy grew the wheat");
          helper.assertTrue(helper.getTick() >= grownAt[0] + 600, "30 seconds have passed since");
          helper.assertFalse(TestSupport.fairies(helper).isEmpty(), "the fairy is still here");
        });
  }
}
