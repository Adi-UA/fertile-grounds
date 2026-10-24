package com.fertilegrounds.gametest;

import com.fertilegrounds.FertileGrounds;
import com.fertilegrounds.block.ModBlocks;
import com.fertilegrounds.item.ModItems;
import com.fertilegrounds.util.ModIdsUtil;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

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
                .map(path -> Identifier.fromNamespaceAndPath("fertilegrounds-gametest", path)))
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
   * Each recipe advancement puts its recipe in the player's recipe book. Vanilla skips an unknown
   * key in an advancement's rewards with no log line, so a misnamed key ships silently: the 26.1
   * and 26.2 jars of 2.0.0 said "recipe" instead of "recipes".
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
      final ResourceKey<Recipe<?>> recipe =
          ResourceKey.create(Registries.RECIPE, ModIdsUtil.id(path));
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
