package com.fertilegrounds.gametest;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gamerules.GameRules;

/** Proves the shared test setup works: environments, structures, and mock players. */
public class SetupTests {

  @FertileTest(environment = "fertilegrounds:night")
  public void nightEnvironmentIsDarkWithNoRandomTicks(final GameTestHelper helper) {
    helper.assertTrue(helper.getLevel().isDarkOutside(), "the night environment is dark");
    // The test server's clock runs while a test waits for its chunks to load, so the night
    // environment also stops it; otherwise a slow machine could reach dawn before the test starts.
    helper.assertValueEqual(helper.getLevel().getDayTime(), 18000L, "clock time");
    helper.assertValueEqual(
        helper.getLevel().getGameRules().get(GameRules.RANDOM_TICK_SPEED), 0, "random tick speed");
    helper.succeed();
  }

  @FertileTest(structure = "fertilegrounds:empty_large")
  public void largeStructureIsEmpty(final GameTestHelper helper) {
    helper.assertBlockPresent(Blocks.AIR, 32, 9, 32);
    helper.assertBlockPresent(Blocks.AIR, 16, 0, 16);
    helper.succeed();
  }

  @FertileTest(structure = "fertilegrounds:empty_medium")
  public void survivalPlayerUsesUpItems(final GameTestHelper helper) {
    final TestPlayer player = TestPlayer.join(helper);
    final ItemStack stack = new ItemStack(Items.BONE_MEAL, 2);
    player.getInventory().setItem(Inventory.SLOT_OFFHAND, stack);
    stack.consume(1, player);
    helper.assertValueEqual(stack.getCount(), 1, "bone meal left after using one in survival");
    player.leave();
    helper.succeed();
  }
}
