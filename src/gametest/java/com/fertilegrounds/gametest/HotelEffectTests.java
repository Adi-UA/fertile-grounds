package com.fertilegrounds.gametest;

import com.fertilegrounds.block.ModBlocks;
import com.fertilegrounds.hotel.FairyHotelLookup;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;

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

  /**
   * With random ticks at 100, wheat 3 blocks from an occupied hotel reaches full age while
   * identical wheat 10 blocks away is still growing.
   */
  @GameTest(
      structure = "fertilegrounds:empty_medium",
      environment = "fertilegrounds:hotel_growth",
      maxTicks = 1200)
  public void hotelGrowsNearbyCropsFaster(final GameTestHelper helper) {
    final BlockPos hotel = new BlockPos(4, 2, 8);
    final BlockPos near = hotel.east(3);
    final BlockPos far = hotel.east(10);
    placeHotel(helper, hotel, ModBlocks.FAIRY_HOTEL);
    TestSupport.plantWheat(helper, near);
    TestSupport.plantWheat(helper, far);
    helper.succeedWhen(
        () -> {
          helper.assertBlockProperty(near, CropBlock.AGE, CropBlock.MAX_AGE);
          helper.assertTrue(
              helper.getBlockState(far).getValue(CropBlock.AGE) < CropBlock.MAX_AGE,
              "wheat 10 blocks away is still growing");
        });
  }

  /** Two hotels side by side: exactly one leads, so the boost doesn't double. */
  @GameTest(structure = "fertilegrounds:empty_medium")
  public void sideBySideHotelsShareOneLeader(final GameTestHelper helper) {
    placeHotel(helper, HOTEL, ModBlocks.FAIRY_HOTEL);
    placeHotel(helper, HOTEL.east(), ModBlocks.FAIRY_HOTEL);
    final ServerLevel level = helper.getLevel();
    helper.succeedWhen(
        () -> {
          final boolean first = FairyHotelLookup.isClusterLeader(level, helper.absolutePos(HOTEL));
          final boolean second =
              FairyHotelLookup.isClusterLeader(level, helper.absolutePos(HOTEL.east()));
          helper.assertTrue(first != second, "exactly one of the two hotels leads");
        });
  }

  /** At 100% honey chance, a hive 2 blocks away fills to 5 and then stays there. */
  @GameTest(
      structure = "fertilegrounds:empty_medium",
      environment = "fertilegrounds:hotel_honey",
      maxTicks = 1200)
  public void hotelFillsANearbyHiveToFive(final GameTestHelper helper) {
    final BlockPos hive = HOTEL.east(2);
    placeHotel(helper, HOTEL, ModBlocks.FAIRY_HOTEL);
    helper.setBlock(hive, Blocks.BEEHIVE);
    final Runnable full =
        () ->
            helper.assertBlockProperty(
                hive, BeehiveBlock.HONEY_LEVEL, BeehiveBlock.MAX_HONEY_LEVELS);
    helper.startSequence().thenWaitUntil(full).thenIdle(200).thenExecute(full).thenSucceed();
  }

  static void placeHotel(final GameTestHelper helper, final BlockPos pos, final Block hotel) {
    helper.setBlock(pos.below(), Blocks.STONE);
    helper.setBlock(pos, hotel);
  }
}
