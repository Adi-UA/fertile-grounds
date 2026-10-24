package com.fertilegrounds.gametest;

import com.fertilegrounds.block.ModBlocks;
import com.fertilegrounds.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Fertilizer used by a player and fired from a dispenser; both call {@code FertilizerItem.upgrade}.
 * Each starts with 2, so a used one leaves 1.
 */
public class FertilizerTests {

  private static final BlockPos DISPENSER = new BlockPos(1, 2, 3);

  private static final BlockPos TARGET = DISPENSER.east();

  @FertileTest(environment = "fertilegrounds:no_random_ticks")
  public void upgradesFarmlandKeepingMoistureAndCrop(final GameTestHelper helper) {
    final BlockState wheat = Blocks.WHEAT.defaultBlockState().setValue(CropBlock.AGE, 3);
    fire(helper, moist(ModBlocks.ENRICHED_FARMLAND));
    helper.setBlock(TARGET.above(), wheat);
    helper.succeedWhen(
        () -> {
          helper.assertBlockState(TARGET, moist(ModBlocks.SUPER_ENRICHED_FARMLAND));
          helper.assertBlockState(TARGET.above(), wheat);
          assertFertilizerLeft(helper, 1);
        });
  }

  @FertileTest
  public void upgradesSand(final GameTestHelper helper) {
    helper.setBlock(TARGET.below(), Blocks.STONE); // holds up the sand, which would otherwise fall
    fire(helper, Blocks.SAND.defaultBlockState());
    helper.succeedWhen(
        () -> {
          helper.assertBlockPresent(ModBlocks.ENRICHED_SAND, TARGET);
          assertFertilizerLeft(helper, 1);
        });
  }

  @FertileTest
  public void keepsFertilizerOnStone(final GameTestHelper helper) {
    assertKept(helper, Blocks.STONE.defaultBlockState());
  }

  @FertileTest(environment = "fertilegrounds:no_random_ticks")
  public void keepsFertilizerOnTheTopTier(final GameTestHelper helper) {
    assertKept(helper, moist(ModBlocks.SUPER_ENRICHED_FARMLAND));
  }

  /** By hand, the player uses the same upgrade and loses one Fertilizer in survival. */
  @FertileTest(environment = "fertilegrounds:no_random_ticks")
  public void playerUpgradesFarmlandByHand(final GameTestHelper helper) {
    final BlockState wheat = Blocks.WHEAT.defaultBlockState().setValue(CropBlock.AGE, 3);
    helper.setBlock(TARGET, moist(ModBlocks.ENRICHED_FARMLAND));
    helper.setBlock(TARGET.above(), wheat);
    final TestPlayer player = TestPlayer.join(helper);
    player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.FERTILIZER, 2));
    player.useHeldItemOn(helper, TARGET);

    helper.assertBlockState(TARGET, moist(ModBlocks.SUPER_ENRICHED_FARMLAND));
    helper.assertBlockState(TARGET.above(), wheat);
    helper.assertValueEqual(player.getMainHandItem().getCount(), 1, "Fertilizer left");
    player.leave();
    helper.succeed();
  }

  private static void assertKept(final GameTestHelper helper, final BlockState target) {
    fire(helper, target);
    // The dispenser fires on the next tick, so wait a few before checking nothing changed.
    helper.runAfterDelay(
        5,
        () -> {
          helper.assertBlockState(TARGET, target);
          assertFertilizerLeft(helper, 2);
          helper.succeed();
        });
  }

  /** Places the target and an east-facing dispenser with 2 Fertilizer, then powers it. */
  private static void fire(final GameTestHelper helper, final BlockState target) {
    helper.setBlock(TARGET, target);
    helper.setBlock(
        DISPENSER,
        Blocks.DISPENSER.defaultBlockState().setValue(DispenserBlock.FACING, Direction.EAST));
    helper
        .getBlockEntity(DISPENSER, DispenserBlockEntity.class)
        .setItem(0, new ItemStack(ModItems.FERTILIZER, 2));
    helper.setBlock(DISPENSER.west(), Blocks.REDSTONE_BLOCK);
  }

  private static void assertFertilizerLeft(final GameTestHelper helper, final int count) {
    final ItemStack left = helper.getBlockEntity(DISPENSER, DispenserBlockEntity.class).getItem(0);
    helper.assertTrue(left.is(ModItems.FERTILIZER), "dispenser still holds Fertilizer");
    helper.assertValueEqual(left.getCount(), count, "Fertilizer left");
  }

  private static BlockState moist(final Block farmland) {
    return farmland.defaultBlockState().setValue(FarmBlock.MOISTURE, FarmBlock.MAX_MOISTURE);
  }
}
