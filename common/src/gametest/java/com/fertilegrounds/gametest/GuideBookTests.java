package com.fertilegrounds.gametest;

import com.fertilegrounds.item.GuideBook;
import com.fertilegrounds.item.GuideBookGift;
import com.fertilegrounds.util.ModSavedData;
import java.util.List;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.crafting.RecipeType;

/** The Fertile Grounds Guide: what's in it, who gets it, and crafting a copy. */
public class GuideBookTests {

  @FertileTest
  public void everyPageIsATranslationKey(final GameTestHelper helper) {
    final CompoundTag content = GuideBook.create().getTag();
    helper.assertTrue(content.getBoolean(WrittenBookItem.TAG_RESOLVED), "the book is resolved");
    final ListTag pages = content.getList(WrittenBookItem.TAG_PAGES, Tag.TAG_STRING);
    TestSupport.assertValueEqual(helper, pages.size(), GuideBook.PAGE_COUNT, "pages");
    for (int page = 0; page < GuideBook.PAGE_COUNT; page++) {
      helper.assertTrue(
          Component.Serializer.fromJson(pages.getString(page)).getContents()
                  instanceof TranslatableContents key
              && key.getKey().equals("book.fertilegrounds.guide.page." + (page + 1)),
          "page " + (page + 1) + " is its translation key");
    }
    helper.assertTrue(
        GuideBook.create().getHoverName().getContents() instanceof TranslatableContents,
        "the name is translated");
    helper.succeed();
  }

  /** Joining gives one guide; a second join, or the same check again, gives no more. */
  @FertileTest
  public void firstJoinGivesTheGuideOnce(final GameTestHelper helper) {
    final TestPlayer player = TestPlayer.join(helper);
    TestSupport.assertValueEqual(helper, guidesHeld(player), 1, "guides after joining");
    GuideBookGift.giveOnFirstJoin(player);
    TestSupport.assertValueEqual(helper, guidesHeld(player), 1, "guides after joining again");
    player.leave();
    helper.succeed();
  }

  /** The guide flag is saved with the player, so rejoining gives no second guide. */
  @FertileTest
  public void rejoiningGivesNoSecondGuide(final GameTestHelper helper) {
    final TestPlayer rejoined = TestPlayer.join(helper).rejoin(helper);
    helper.assertTrue(ModSavedData.RECEIVED_GUIDE.isSet(rejoined), "marked after rejoining");
    TestSupport.assertValueEqual(helper, guidesHeld(rejoined), 1, "guides after rejoining");
    rejoined.leave();
    helper.succeed();
  }

  /**
   * Dying and leaving the End both replace the player with a new copy, which must keep the flag
   * (Fabric and NeoForge copy it only when it's marked to survive death).
   */
  @FertileTest
  public void guideFlagSurvivesDeathAndTheEnd(final GameTestHelper helper) {
    final PlayerList players = helper.getLevel().getServer().getPlayerList();
    final ServerPlayer afterDeath = players.respawn(TestPlayer.join(helper), false);
    helper.assertTrue(ModSavedData.RECEIVED_GUIDE.isSet(afterDeath), "marked after dying");
    final ServerPlayer afterEnd = players.respawn(afterDeath, true);
    helper.assertTrue(ModSavedData.RECEIVED_GUIDE.isSet(afterEnd), "marked after the End");
    players.remove(afterEnd);
    helper.succeed();
  }

  @FertileTest(environment = "fertilegrounds:no_guide_book")
  public void gameruleOffGivesNoGuide(final GameTestHelper helper) {
    final TestPlayer player = TestPlayer.join(helper);
    TestSupport.assertValueEqual(helper, guidesHeld(player), 0, "guides after joining");
    helper.assertFalse(ModSavedData.RECEIVED_GUIDE.isSet(player), "marked as received");
    player.leave();
    helper.succeed();
  }

  /** With a full inventory, the guide drops at the player's feet. */
  @FertileTest
  public void fullInventoryDropsTheGuide(final GameTestHelper helper) {
    final TestPlayer player = TestPlayer.join(helper).at(helper, 3, 2, 3);
    ModSavedData.RECEIVED_GUIDE.clear(player);
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
    TestSupport.assertValueEqual(helper, dropped.size(), 1, "guides dropped");
    dropped.forEach(ItemEntity::discard);
    player.leave();
    helper.succeed();
  }

  /** The guide keeps its pages and name through a save and load, as its NBT tag holds them. */
  @FertileTest
  public void guideKeepsItsPagesThroughASave(final GameTestHelper helper) {
    final ItemStack loaded = ItemStack.of(GuideBook.create().save(new CompoundTag()));
    helper.assertTrue(isGuide(loaded), "the same guide after a save");
    helper.succeed();
  }

  /** A Book and Bone Meal craft the same guide the join gift gives. */
  @FertileTest
  public void bookAndBoneMealCraftTheGuide(final GameTestHelper helper) {
    final CraftingContainer grid =
        TestSupport.craftingGrid(
            2, 1, List.of(new ItemStack(Items.BONE_MEAL), new ItemStack(Items.BOOK)));
    final ItemStack result =
        helper
            .getLevel()
            .getServer()
            .getRecipeManager()
            .getRecipeFor(RecipeType.CRAFTING, grid, helper.getLevel())
            .map(recipe -> recipe.assemble(grid, helper.getLevel().registryAccess()))
            .orElse(ItemStack.EMPTY);
    helper.assertTrue(isGuide(result), "crafted the guide");
    helper.succeed();
  }

  private static boolean isGuide(final ItemStack stack) {
    return ItemStack.isSameItemSameTags(stack, GuideBook.create());
  }

  private static int guidesHeld(final TestPlayer player) {
    int count = 0;
    for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
      final ItemStack stack = player.getInventory().getItem(slot);
      if (isGuide(stack)) {
        count += stack.getCount();
      }
    }
    return count;
  }
}
