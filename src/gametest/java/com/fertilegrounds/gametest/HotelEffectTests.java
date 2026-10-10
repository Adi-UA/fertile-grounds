package com.fertilegrounds.gametest;

import com.fertilegrounds.block.ModBlocks;
import com.fertilegrounds.hotel.FairyHotelLookup;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/** What an occupied Fairy Hotel does to the farm around it. */
public class HotelEffectTests {

  private static final BlockPos HOTEL = new BlockPos(8, 2, 8);

  /** The lookup finds an occupied hotel 6 blocks away within 8, not one 10 away. */
  @GameTest(structure = "fertilegrounds:empty_medium")
  public void lookupFindsOnlyNearbyOccupiedHotels(final GameTestHelper helper) {
    placeHotel(helper, HOTEL, ModBlocks.FAIRY_HOTEL);
    final ServerLevel level = helper.getLevel();
    final BlockPos hotel = helper.absolutePos(HOTEL);
    // Points of interest register on a later tick than the block is placed.
    helper.succeedWhen(
        () -> {
          helper.assertTrue(
              FairyHotelLookup.nearestOccupiedHotel(level, hotel.east(6), 8).isPresent(),
              "found from 6 blocks");
          helper.assertFalse(
              FairyHotelLookup.nearestOccupiedHotel(level, hotel.east(10), 8).isPresent(),
              "found from 10 blocks");
        });
  }

  @GameTest(structure = "fertilegrounds:empty_medium", maxTicks = 40)
  public void lookupSkipsEmptyHotels(final GameTestHelper helper) {
    placeHotel(helper, HOTEL, ModBlocks.EMPTY_FAIRY_HOTEL);
    helper.runAfterDelay(
        20,
        () -> {
          helper.assertFalse(
              FairyHotelLookup.nearestOccupiedHotel(helper.getLevel(), helper.absolutePos(HOTEL), 8)
                  .isPresent(),
              "an empty hotel was found");
          helper.succeed();
        });
  }

  static void placeHotel(final GameTestHelper helper, final BlockPos pos, final Block hotel) {
    helper.setBlock(pos.below(), Blocks.STONE);
    helper.setBlock(pos, hotel);
  }
}
