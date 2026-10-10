package com.fertilegrounds.gametest;

import com.fertilegrounds.block.ModBlocks;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/** Placing, lighting, and breaking Fairy Hotels. */
public class FairyHotelTests {

  private static final BlockPos HOTEL = new BlockPos(3, 2, 3);

  @GameTest
  public void emptyHotelGivesLightFive(final GameTestHelper helper) {
    assertLight(helper, ModBlocks.EMPTY_FAIRY_HOTEL, 5);
  }

  @GameTest
  public void occupiedHotelGivesLightTwelve(final GameTestHelper helper) {
    assertLight(helper, ModBlocks.FAIRY_HOTEL, 12);
  }

  /** A player breaking an occupied hotel gets it back with the fairy still inside. */
  @GameTest
  public void brokenOccupiedHotelDropsItself(final GameTestHelper helper) {
    placeHotel(helper, ModBlocks.FAIRY_HOTEL);
    final TestPlayer player = TestPlayer.join(helper);
    player.gameMode.destroyBlock(helper.absolutePos(HOTEL));
    helper.assertItemEntityPresent(ModBlocks.FAIRY_HOTEL.asItem(), HOTEL, 1);
    player.leave();
    helper.succeed();
  }

  /** Any mix of fairy flowers around a lantern, with a firefly bush on top, makes a hotel. */
  @GameTest
  public void mixedFlowersCraftAHotel(final GameTestHelper helper) {
    final ItemStack poppy = new ItemStack(Items.POPPY);
    final ItemStack dandelion = new ItemStack(Items.DANDELION);
    final CraftingInput grid =
        CraftingInput.of(
            3,
            3,
            List.of(
                poppy,
                new ItemStack(Items.FIREFLY_BUSH),
                dandelion,
                dandelion,
                new ItemStack(Items.LANTERN),
                poppy,
                poppy,
                dandelion,
                poppy));
    final ItemStack result =
        helper
            .getLevel()
            .getServer()
            .getRecipeManager()
            .getRecipeFor(RecipeType.CRAFTING, grid, helper.getLevel())
            .map(recipe -> recipe.value().assemble(grid))
            .orElse(ItemStack.EMPTY);
    helper.assertTrue(
        result.is(ModBlocks.EMPTY_FAIRY_HOTEL.asItem()), "crafted an Empty Fairy Hotel");
    helper.succeed();
  }

  private static void assertLight(final GameTestHelper helper, final Block hotel, final int level) {
    placeHotel(helper, hotel);
    // The light engine updates on later ticks, so wait for it.
    helper.succeedWhen(
        () ->
            helper.assertValueEqual(
                helper.getLevel().getBrightness(LightLayer.BLOCK, helper.absolutePos(HOTEL)),
                level,
                "block light at the hotel"));
  }

  /** Sets a hotel standing on stone, the way a player places one on the ground. */
  private static void placeHotel(final GameTestHelper helper, final Block hotel) {
    helper.setBlock(HOTEL.below(), Blocks.STONE);
    helper.setBlock(HOTEL, hotel);
  }
}
