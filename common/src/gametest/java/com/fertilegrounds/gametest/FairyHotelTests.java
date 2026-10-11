package com.fertilegrounds.gametest;

import com.fertilegrounds.block.ModBlocks;
import java.util.List;
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

  /** The light engine runs behind the server tick when the server is busy, so allow 5 seconds. */
  @FertileTest(maxTicks = 100)
  public void emptyHotelGivesLightFive(final GameTestHelper helper) {
    assertLight(helper, ModBlocks.EMPTY_FAIRY_HOTEL, 5);
  }

  @FertileTest(maxTicks = 100)
  public void occupiedHotelGivesLightTwelve(final GameTestHelper helper) {
    assertLight(helper, ModBlocks.FAIRY_HOTEL, 12);
  }

  /** A player breaking an occupied hotel gets it back with the fairy still inside. */
  @FertileTest
  public void brokenOccupiedHotelDropsItself(final GameTestHelper helper) {
    placeHotel(helper, ModBlocks.FAIRY_HOTEL);
    final TestPlayer player = TestPlayer.join(helper);
    player.gameMode.destroyBlock(helper.absolutePos(HOTEL));
    helper.assertItemEntityPresent(ModBlocks.FAIRY_HOTEL.asItem(), HOTEL, 1);
    player.leave();
    helper.succeed();
  }

  @FertileTest
  public void brokenChimeHotelDropsItself(final GameTestHelper helper) {
    placeHotel(helper, ModBlocks.CHIME_FAIRY_HOTEL);
    final TestPlayer player = TestPlayer.join(helper);
    player.gameMode.destroyBlock(helper.absolutePos(HOTEL));
    helper.assertItemEntityPresent(ModBlocks.CHIME_FAIRY_HOTEL.asItem(), HOTEL, 1);
    player.leave();
    helper.succeed();
  }

  /** An occupied hotel, a note block, and a spyglass, in any order, make a Chime Hotel. */
  @FertileTest
  public void noteBlockAndSpyglassMakeAChimeHotel(final GameTestHelper helper) {
    final CraftingInput grid =
        CraftingInput.of(
            3,
            1,
            List.of(
                new ItemStack(Items.SPYGLASS),
                new ItemStack(ModBlocks.FAIRY_HOTEL),
                new ItemStack(Items.NOTE_BLOCK)));
    helper.assertTrue(
        craft(helper, grid).is(ModBlocks.CHIME_FAIRY_HOTEL.asItem()), "crafted a Chime Hotel");
    helper.succeed();
  }

  /** Any mix of fairy flowers around a lantern, with a firefly bush on top, makes a hotel. */
  @FertileTest
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
    helper.assertTrue(
        craft(helper, grid).is(ModBlocks.EMPTY_FAIRY_HOTEL.asItem()),
        "crafted an Empty Fairy Hotel");
    helper.succeed();
  }

  private static ItemStack craft(final GameTestHelper helper, final CraftingInput grid) {
    return helper
        .getLevel()
        .getServer()
        .getRecipeManager()
        .getRecipeFor(RecipeType.CRAFTING, grid, helper.getLevel())
        .map(recipe -> recipe.value().assemble(grid))
        .orElse(ItemStack.EMPTY);
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
