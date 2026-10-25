package com.fertilegrounds.gametest;

import com.fertilegrounds.item.JarredFairyItem;
import com.fertilegrounds.item.ModDataComponents;
import com.fertilegrounds.item.ModItems;
import com.fertilegrounds.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

/**
 * A Jarred Fairy left 3 days becomes a flower, wherever it is. Each test moves the clock forward,
 * so they share their own environment; the moves only add up, so no test sees less than 3 days.
 */
public class JarredFairyExpiryTests {

  private static final String THREE_DAYS_LATER = "time add 72000";

  @FertileTest(environment = "fertilegrounds:jar_expiry")
  public void carriedFairySlipsAwayWithAMessage(final GameTestHelper helper) {
    final TestPlayer player = TestPlayer.join(helper).tickedBy(helper);
    player.getInventory().setItem(0, JarredFairyItem.caughtNow(helper.getLevel()));
    TestSupport.command(helper, THREE_DAYS_LATER);
    helper.succeedWhen(
        () -> {
          helper.assertTrue(
              player.getInventory().getItem(0).is(ModTags.FAIRY_FLOWER_ITEMS),
              Component.literal("a flower in the jar's slot"));
          helper.assertTrue(
              player.lastOverlayIs("message.fertilegrounds.fairy_slipped_away"),
              Component.literal("player told the fairy left"));
          player.leave();
        });
  }

  @FertileTest(environment = "fertilegrounds:jar_expiry")
  public void fairyInAChestSlipsAwayWhenOpened(final GameTestHelper helper) {
    final BlockPos chest = new BlockPos(3, 2, 3);
    helper.setBlock(chest, Blocks.CHEST);
    final ChestBlockEntity contents = helper.getBlockEntity(chest, ChestBlockEntity.class);
    contents.setItem(0, JarredFairyItem.caughtNow(helper.getLevel()));
    contents.setItem(1, JarredFairyItem.caughtNow(helper.getLevel()));
    TestSupport.command(helper, THREE_DAYS_LATER);
    final TestPlayer player = TestPlayer.join(helper);
    player.openMenu(contents);

    for (final int slot : new int[] {0, 1}) {
      helper.assertTrue(
          contents.getItem(slot).is(ModTags.FAIRY_FLOWER_ITEMS),
          Component.literal("a flower in slot " + slot));
    }
    helper.assertValueEqual(
        player.overlayCount("message.fertilegrounds.fairy_slipped_away_while_gone"),
        1L,
        Component.literal("messages for two fairies"));
    helper.assertTrue(
        player.lastOverlayIs("message.fertilegrounds.fairy_slipped_away_while_gone"),
        Component.literal("player told a fairy left while they were gone"));
    player.leave();
    helper.succeed();
  }

  @FertileTest(environment = "fertilegrounds:jar_expiry")
  public void droppedFairySlipsAway(final GameTestHelper helper) {
    final BlockPos spot = new BlockPos(3, 2, 3);
    helper.setBlock(spot.below(), Blocks.STONE);
    final ItemEntity dropped =
        helper.spawnItem(
            ModItems.JARRED_FAIRY, spot.getX() + 0.5F, spot.getY(), spot.getZ() + 0.5F);
    dropped.setItem(JarredFairyItem.caughtNow(helper.getLevel()));
    dropped.setUnlimitedLifetime();
    TestSupport.command(helper, THREE_DAYS_LATER);
    helper.succeedWhen(
        () ->
            helper.assertTrue(
                dropped.getItem().is(ModTags.FAIRY_FLOWER_ITEMS), Component.literal("a flower")));
  }

  /** A carried fairy with time left stays in its jar while the inventory ticks. */
  @FertileTest(environment = "fertilegrounds:jar_expiry", maxTicks = 60)
  public void fairyWithTimeLeftStays(final GameTestHelper helper) {
    final TestPlayer player = TestPlayer.join(helper).tickedBy(helper);
    final ItemStack jar = JarredFairyItem.caughtNow(helper.getLevel());
    // Push its time far out, since other tests here move the clock too.
    jar.set(ModDataComponents.SLIPS_AWAY_AT, Long.MAX_VALUE);
    player.getInventory().setItem(0, jar);
    helper.runAfterDelay(
        40,
        () -> {
          helper.assertTrue(
              player.getInventory().getItem(0).is(ModItems.JARRED_FAIRY),
              Component.literal("still a Jarred Fairy"));
          player.leave();
          helper.succeed();
        });
  }
}
