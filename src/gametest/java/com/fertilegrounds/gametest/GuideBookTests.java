package com.fertilegrounds.gametest;

import com.fertilegrounds.item.GuideBook;
import com.fertilegrounds.item.GuideBookGift;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeType;

/** The Fertile Grounds Guide: what's in it, who gets it, and crafting a copy. */
public class GuideBookTests {

  @GameTest
  public void everyPageIsATranslationKey(final GameTestHelper helper) {
    final WrittenBookContent content = GuideBook.create().get(DataComponents.WRITTEN_BOOK_CONTENT);
    helper.assertTrue(content.resolved(), "the book is resolved");
    helper.assertValueEqual(content.pages().size(), GuideBook.PAGE_COUNT, "pages");
    for (int page = 0; page < GuideBook.PAGE_COUNT; page++) {
      helper.assertTrue(
          content.pages().get(page).raw().getContents() instanceof TranslatableContents key
              && key.getKey().equals("book.fertilegrounds.guide.page." + (page + 1)),
          "page " + (page + 1) + " is its translation key");
    }
    helper.assertTrue(
        GuideBook.create().getItemName().getContents() instanceof TranslatableContents,
        "the name is translated");
    helper.succeed();
  }

  /** Joining gives one guide; a second join, or the same check again, gives no more. */
  @GameTest
  public void firstJoinGivesTheGuideOnce(final GameTestHelper helper) {
    final TestPlayer player = TestPlayer.join(helper);
    helper.assertValueEqual(guidesHeld(player), 1, "guides after joining");
    GuideBookGift.giveOnFirstJoin(player);
    helper.assertValueEqual(guidesHeld(player), 1, "guides after joining again");
    player.leave();
    helper.succeed();
  }

  @GameTest(environment = "fertilegrounds:no_guide_book")
  public void gameruleOffGivesNoGuide(final GameTestHelper helper) {
    final TestPlayer player = TestPlayer.join(helper);
    helper.assertValueEqual(guidesHeld(player), 0, "guides after joining");
    helper.assertFalse(player.hasAttached(GuideBookGift.RECEIVED_GUIDE), "marked as received");
    player.leave();
    helper.succeed();
  }

  /** With a full inventory, the guide drops at the player's feet. */
  @GameTest
  public void fullInventoryDropsTheGuide(final GameTestHelper helper) {
    final TestPlayer player = TestPlayer.join(helper).at(helper, 3, 2, 3);
    player.removeAttached(GuideBookGift.RECEIVED_GUIDE);
    player.getInventory().clearContent();
    for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
      player.getInventory().setItem(slot, new ItemStack(Items.STONE));
    }
    GuideBookGift.giveOnFirstJoin(player);
    final List<ItemEntity> dropped =
        helper
            .getLevel()
            .getEntitiesOfClass(
                ItemEntity.class,
                player.getBoundingBox().inflate(3),
                item -> isGuide(item.getItem()));
    helper.assertValueEqual(dropped.size(), 1, "guides dropped");
    dropped.forEach(ItemEntity::discard);
    player.leave();
    helper.succeed();
  }

  /** A Book and Bone Meal craft the same guide the join gift gives. */
  @GameTest
  public void bookAndBoneMealCraftTheGuide(final GameTestHelper helper) {
    final CraftingInput grid =
        CraftingInput.of(2, 1, List.of(new ItemStack(Items.BONE_MEAL), new ItemStack(Items.BOOK)));
    final ItemStack result =
        helper
            .getLevel()
            .getServer()
            .getRecipeManager()
            .getRecipeFor(RecipeType.CRAFTING, grid, helper.getLevel())
            .map(recipe -> recipe.value().assemble(grid))
            .orElse(ItemStack.EMPTY);
    helper.assertTrue(isGuide(result), "crafted the guide");
    helper.succeed();
  }

  private static boolean isGuide(final ItemStack stack) {
    return ItemStack.isSameItemSameComponents(stack, GuideBook.create());
  }

  private static int guidesHeld(final TestPlayer player) {
    int count = 0;
    for (final ItemStack stack : player.getInventory()) {
      if (isGuide(stack)) {
        count += stack.getCount();
      }
    }
    return count;
  }
}
