package com.fertilegrounds.gametest;

import com.fertilegrounds.FertileGrounds;
import com.fertilegrounds.block.ModBlocks;
import com.fertilegrounds.item.ModItems;
import com.fertilegrounds.util.ModIdsUtil;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

/**
 * The mod's advancements, and the test data's ones on each custom trigger, load and are earned
 * through real actions, since the loader skips a broken file with only a log line.
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

  /**
   * Each recipe advancement puts its recipe in the player's recipe book, since vanilla skips an
   * unknown rewards key with no log line.
   */
  @FertileTest
  public void recipeAdvancementsUnlockTheirRecipes(final GameTestHelper helper) {
    final TestPlayer player = TestPlayer.join(helper);
    final List<AdvancementHolder> recipeAdvancements =
        helper.getLevel().getServer().getAdvancements().getAllAdvancements().stream()
            .filter(
                holder ->
                    holder.id().getNamespace().equals(FertileGrounds.MOD_ID)
                        && holder.id().getPath().startsWith("recipes/"))
            .toList();
    helper.assertTrue(!recipeAdvancements.isEmpty(), "the mod has recipe advancements");
    for (final AdvancementHolder holder : recipeAdvancements) {
      final String path = holder.id().getPath().substring("recipes/".length());
      final ResourceLocation recipe = ModIdsUtil.id(path);
      helper.assertTrue(!player.getRecipeBook().contains(recipe), "new players lack " + recipe);
      holder
          .value()
          .criteria()
          .keySet()
          .forEach(criterion -> player.getAdvancements().award(holder, criterion));
      helper.assertTrue(
          player.getRecipeBook().contains(recipe), holder.id() + " unlocks " + recipe);
    }
    player.leave();
    helper.succeed();
  }
}
