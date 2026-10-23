package com.fertilegrounds.gametest;

import com.fertilegrounds.block.ModBlocks;
import com.fertilegrounds.item.ModItems;
import com.fertilegrounds.util.ModIdsUtil;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

/**
 * The mod's advancements load and are earned through real actions; the visit and gift ones are in
 * {@code VisitFeedbackTests} and {@code FairyGiftTests}. The test data's advancements on each
 * custom trigger load too. An unknown trigger or a broken file makes the advancement loader skip
 * that file with only a log line.
 */
public class AdvancementTests {

  @FertileTest
  public void everyAdvancementLoads(final GameTestHelper helper) {
    final ServerAdvancementManager advancements = helper.getLevel().getServer().getAdvancements();
    Stream.concat(
            Stream.of(
                    "root",
                    "first_fairy_visit",
                    "use_fertilizer",
                    "first_fairy_gift",
                    "harvest_moon_visit",
                    "first_fairy_catch",
                    "first_fairy_hotel")
                .map(ModIdsUtil::id),
            Stream.of("fairy_visit", "fairy_gift", "harvest_moon_visit")
                .map(
                    path -> ResourceLocation.fromNamespaceAndPath("fertilegrounds-gametest", path)))
        .forEach(id -> helper.assertTrue(advancements.get(id) != null, "advancement " + id));
    helper.succeed();
  }

  /** Picking up any soil block earns the tab's root advancement. */
  @FertileTest
  public void rootEarnedByGettingSoil(final GameTestHelper helper) {
    final TestPlayer player = TestPlayer.join(helper);
    player.getInventory().add(new ItemStack(ModBlocks.ENRICHED_DIRT));
    helper.succeedWhen(
        () -> {
          helper.assertTrue(player.hasAdvancement("root"), "root advancement");
          player.leave();
        });
  }

  @FertileTest(environment = "fertilegrounds:no_random_ticks")
  public void fertilizerAdvancementEarnedByUse(final GameTestHelper helper) {
    final BlockPos soil = new BlockPos(3, 1, 3);
    helper.setBlock(soil, ModBlocks.ENRICHED_FARMLAND);
    final TestPlayer player = TestPlayer.join(helper);
    player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.FERTILIZER));
    player.useHeldItemOn(helper, soil);
    helper.assertTrue(player.hasAdvancement("use_fertilizer"), "Fertilizer advancement");
    player.leave();
    helper.succeed();
  }
}
