package com.fertilegrounds.gametest;

import com.fertilegrounds.block.FairyHotelBlock;
import com.fertilegrounds.block.ModBlocks;
import com.fertilegrounds.hotel.AvoidFairyHotelGoal;
import com.fertilegrounds.hotel.FairyHotelLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.phys.Vec3;

/** What an occupied Fairy Hotel does to the farm around it. */
public class HotelEffectTests {

  private static final BlockPos HOTEL = new BlockPos(8, 2, 8);

  /** A hotel spot inside vanilla's default 8x8x8 test structure. */
  private static final BlockPos HOTEL_SMALL = new BlockPos(3, 2, 3);

  /** The lookup finds an occupied hotel 6 blocks away within 8, not one 10 away. */
  @FertileTest(structure = "fertilegrounds:empty_medium")
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

  @FertileTest(structure = "fertilegrounds:empty_medium", maxTicks = 40)
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
  @FertileTest(
      structure = "fertilegrounds:empty_medium",
      environment = "fertilegrounds:hotel_growth",
      maxTicks = 1200)
  public void hotelGrowsNearbyCropsFaster(final GameTestHelper helper) {
    assertGrowsNearbyCropsFaster(helper, ModBlocks.FAIRY_HOTEL);
  }

  /** A Chime Hotel keeps the occupied hotel's crop boost. */
  @FertileTest(
      structure = "fertilegrounds:empty_medium",
      environment = "fertilegrounds:hotel_growth",
      maxTicks = 1200)
  public void chimeHotelGrowsNearbyCropsFaster(final GameTestHelper helper) {
    assertGrowsNearbyCropsFaster(helper, ModBlocks.CHIME_FAIRY_HOTEL);
  }

  private static void assertGrowsNearbyCropsFaster(final GameTestHelper helper, final Block type) {
    final BlockPos hotel = new BlockPos(4, 2, 8);
    final BlockPos near = hotel.east(3);
    final BlockPos far = hotel.east(10);
    placeHotel(helper, hotel, type);
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
  @FertileTest(structure = "fertilegrounds:empty_medium")
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
  @FertileTest(
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

  /**
   * A Brush pets an occupied hotel once (state set, 1 durability), does nothing the second time,
   * and the hotel can be petted again after dawn. The clock starts 100 ticks before dawn, since the
   * dawn tick counts real ticks and a {@code /time set} after petting wouldn't move it.
   */
  @FertileTest(environment = "fertilegrounds:hotel_petting", maxTicks = 300)
  public void brushPetsOncePerDay(final GameTestHelper helper) {
    TestSupport.command(helper, "time set 23900");
    placeHotel(helper, HOTEL_SMALL, ModBlocks.FAIRY_HOTEL);
    final TestPlayer player = TestPlayer.join(helper);
    player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BRUSH));
    helper
        .startSequence()
        .thenExecute(
            () -> {
              player.useHeldItemOn(helper, HOTEL_SMALL);
              helper.assertBlockProperty(HOTEL_SMALL, FairyHotelBlock.PETTED, true);
              helper.assertValueEqual(player.getMainHandItem().getDamageValue(), 1, "brush wear");
              player.useHeldItemOn(helper, HOTEL_SMALL);
              helper.assertValueEqual(
                  player.getMainHandItem().getDamageValue(), 1, "brush wear after a second pet");
            })
        .thenWaitUntil(() -> helper.assertBlockProperty(HOTEL_SMALL, FairyHotelBlock.PETTED, false))
        .thenExecute(
            () -> {
              player.useHeldItemOn(helper, HOTEL_SMALL);
              helper.assertBlockProperty(HOTEL_SMALL, FairyHotelBlock.PETTED, true);
              player.leave();
            })
        .thenSucceed();
  }

  /**
   * A cow dropped 11 blocks always tramples (see {@code SoilRegressionTests}): farmland 3 blocks
   * from an occupied hotel stays farmland, and farmland 6 blocks away turns to dirt.
   */
  @FertileTest(
      structure = "fertilegrounds:empty_medium",
      environment = "fertilegrounds:no_random_ticks",
      skyAccess = true,
      maxTicks = 200)
  public void hotelStopsTramplingNearby(final GameTestHelper helper) {
    final BlockPos hotel = new BlockPos(4, 2, 8);
    final BlockPos near = new BlockPos(7, 1, 8);
    final BlockPos far = new BlockPos(10, 1, 8);
    placeHotel(helper, hotel, ModBlocks.FAIRY_HOTEL);
    // Let the hotel's point of interest register before anything lands.
    helper
        .startSequence()
        .thenIdle(5)
        .thenExecute(
            () -> {
              for (final BlockPos farmland : new BlockPos[] {near, far}) {
                helper.setBlock(farmland, Blocks.FARMLAND);
                helper.spawn(
                    EntityTypes.COW,
                    farmland.getX() + 0.5F,
                    farmland.getY() + 11,
                    farmland.getZ() + 0.5F);
              }
            })
        .thenWaitUntil(() -> helper.assertBlockPresent(Blocks.DIRT, far))
        .thenIdle(20)
        .thenExecute(() -> helper.assertBlockPresent(Blocks.FARMLAND, near))
        .thenSucceed();
  }

  /**
   * A creeper 5 blocks from an occupied hotel is out of its range 5 seconds later. Stone covers the
   * barrier floor, where vanilla never picks a walk target, and its own environment keeps the
   * creeper from chasing other tests' mock players.
   */
  @FertileTest(
      structure = "fertilegrounds:empty_large",
      environment = "fertilegrounds:no_players",
      padding = 8,
      maxTicks = 200)
  public void creeperBacksAwayFromAHotel(final GameTestHelper helper) {
    for (final BlockPos floor : BlockPos.betweenClosed(0, 0, 0, 32, 0, 32)) {
      helper.setBlock(floor, Blocks.STONE);
    }
    final BlockPos hotel = new BlockPos(16, 1, 16);
    placeHotel(helper, hotel, ModBlocks.FAIRY_HOTEL);
    final Creeper creeper =
        helper.spawn(EntityTypes.CREEPER, hotel.getX() + 5.5F, 1, hotel.getZ() + 0.5F);
    final Vec3 hotelCenter = Vec3.atCenterOf(helper.absolutePos(hotel));
    // The flee goal stops once the hotel leaves its 6-block search, so the creeper may stop at 6.5.
    helper.succeedWhen(
        () -> {
          helper.assertTrue(
              creeper.position().distanceTo(hotelCenter) > AvoidFairyHotelGoal.AVOID_DISTANCE,
              "the creeper left the hotel's 6-block range");
          creeper.discard();
        });
  }

  static void placeHotel(final GameTestHelper helper, final BlockPos pos, final Block hotel) {
    helper.setBlock(pos.below(), Blocks.STONE);
    helper.setBlock(pos, hotel);
  }
}
