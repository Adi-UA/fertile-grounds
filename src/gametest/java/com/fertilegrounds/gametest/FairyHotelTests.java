package com.fertilegrounds.gametest;

import com.fertilegrounds.block.ModBlocks;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
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
