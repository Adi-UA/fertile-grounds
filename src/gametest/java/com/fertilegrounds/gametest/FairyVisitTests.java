package com.fertilegrounds.gametest;

import com.fertilegrounds.entity.ModEntities;
import java.util.concurrent.atomic.AtomicBoolean;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;

/**
 * A fairy's visit: growing the patch in rings, staying out of the ground, reaching roofed crops,
 * and leaving when there's nothing to do. The fairies come from {@code summon}-style spawns, which
 * find the nearest crop the way a spawn egg does. Ported from the smoke test's fairy_visit suite.
 */
public class FairyVisitTests {

  private static final BlockPos CENTER = new BlockPos(8, 2, 8);
  private static final BlockPos CORNER = CENTER.offset(2, 0, 2);
  private static final BlockPos OUTSIDE = CENTER.offset(3, 0, 0);

  /**
   * A 5x5 open-field patch: the fairy grows the center before the edge, never sinks below one block
   * above the crop (regression for the dive fixed in 366bbe4), leaves the carrot just outside its
   * reach alone, and flies away.
   */
  @GameTest(
      structure = "fertilegrounds:empty_medium",
      environment = "fertilegrounds:quick_visit",
      skyAccess = true,
      padding = 8,
      maxTicks = 600)
  public void openFieldPatchGrowsFromTheCenterOut(final GameTestHelper helper) {
    for (final BlockPos pos :
        BlockPos.betweenClosed(CENTER.offset(-2, -1, -2), CENTER.offset(3, -1, 2))) {
      helper.setBlock(pos, Blocks.FARMLAND);
    }
    for (final BlockPos pos :
        BlockPos.betweenClosed(CENTER.offset(-2, 0, -2), CENTER.offset(2, 0, 2))) {
      helper.setBlock(pos, Blocks.WHEAT);
    }
    helper.setBlock(OUTSIDE, Blocks.CARROTS);
    helper.spawn(
        ModEntities.BEAN_FAIRY, CENTER.getX() + 0.5F, CENTER.getY() + 4, CENTER.getZ() + 0.5F);

    final double floor = helper.absolutePos(CENTER).getY() + 1;
    final AtomicBoolean centerFirst = new AtomicBoolean();
    helper.onEachTick(
        () -> {
          TestSupport.fairies(helper)
              .forEach(
                  fairy ->
                      helper.assertTrue(
                          fairy.getY() >= floor,
                          "fairy sank to y="
                              + fairy.getY()
                              + ", below the open-sky floor "
                              + floor));
          if (isGrown(helper, CENTER) && !isGrown(helper, CORNER)) {
            centerFirst.set(true);
          }
        });
    helper.succeedWhen(
        () -> {
          helper.assertTrue(
              isGrown(helper, CENTER) && isGrown(helper, CORNER), "patch fully grown");
          helper.assertTrue(TestSupport.fairies(helper).isEmpty(), "fairy left after growing");
          helper.assertTrue(centerFirst.get(), "the center grew before the edge");
          helper.assertBlockState(
              OUTSIDE,
              state -> state.getValue(CropBlock.AGE) == 0,
              s -> Component.literal("carrot outside the patch stayed unripe"));
        });
  }

  /** Regression for 88d5657: a fairy reaches a crop under a glass roof by flying through it. */
  @GameTest(
      structure = "fertilegrounds:empty_medium",
      environment = "fertilegrounds:quick_visit",
      skyAccess = true,
      padding = 8,
      maxTicks = 600)
  public void roofedCropIsReached(final GameTestHelper helper) {
    TestSupport.plantWheat(helper, CENTER);
    for (final BlockPos pos :
        BlockPos.betweenClosed(CENTER.offset(-1, 2, -1), CENTER.offset(1, 2, 1))) {
      helper.setBlock(pos, Blocks.GLASS);
    }
    helper.spawn(
        ModEntities.BEAN_FAIRY, CENTER.getX() + 0.5F, CENTER.getY() + 5, CENTER.getZ() + 0.5F);
    helper.succeedWhen(
        () -> {
          helper.assertTrue(isGrown(helper, CENTER), "roofed wheat grown");
          helper.assertTrue(TestSupport.fairies(helper).isEmpty(), "fairy left after growing");
        });
  }

  /** Regression for 88d5657: a fairy with no crop nearby flies away instead of staying stuck. */
  @GameTest(
      structure = "fertilegrounds:empty_medium",
      environment = "fertilegrounds:quick_visit",
      padding = 8,
      maxTicks = 300)
  public void fairyWithNoCropLeaves(final GameTestHelper helper) {
    helper.setBlock(CENTER.below(), Blocks.STONE);
    helper.spawn(ModEntities.BEAN_FAIRY, CENTER.getX() + 0.5F, CENTER.getY(), CENTER.getZ() + 0.5F);
    helper.succeedWhen(
        () -> helper.assertTrue(TestSupport.fairies(helper).isEmpty(), "fairy left"));
  }

  static boolean isGrown(final GameTestHelper helper, final BlockPos pos) {
    return helper.getBlockState(pos).getBlock() instanceof CropBlock crop
        && crop.isMaxAge(helper.getBlockState(pos));
  }
}
