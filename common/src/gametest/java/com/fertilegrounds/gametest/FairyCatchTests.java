package com.fertilegrounds.gametest;

import com.fertilegrounds.block.ModBlocks;
import com.fertilegrounds.entity.BeanFairy;
import com.fertilegrounds.entity.ModEntities;
import com.fertilegrounds.item.JarredFairyItem;
import com.fertilegrounds.item.ModDataComponents;
import com.fertilegrounds.item.ModItems;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

/** Catching a fairy in a bottle and moving it into a Fairy Hotel. */
public class FairyCatchTests {

  private static final BlockPos CROP = new BlockPos(8, 2, 8);

  private static final BlockPos HOTEL = new BlockPos(3, 2, 3);

  /** A sneaking player, so the fairy doesn't dart away, bottles it while it lingers. */
  @FertileTest(
      structure = "fertilegrounds:empty_medium",
      environment = "fertilegrounds:no_random_ticks",
      skyAccess = true,
      padding = 8,
      maxTicks = 600)
  public void bottleCatchesALingeringFairy(final GameTestHelper helper) {
    TestSupport.plantWheat(helper, CROP);
    final BeanFairy fairy =
        helper.spawn(
            ModEntities.BEAN_FAIRY, CROP.getX() + 0.5F, CROP.getY() + 4, CROP.getZ() + 0.5F);
    final TestPlayer player = holdingBottles(helper);
    final AtomicLong grownAt = new AtomicLong(-1);
    helper.onEachTick(
        () -> {
          if (grownAt.get() < 0 && fairy.hasFinishedGrowing()) {
            grownAt.set(helper.getTick());
            player.setPos(fairy.getX() + 1, fairy.getY(), fairy.getZ());
            player.interactOn(fairy, InteractionHand.MAIN_HAND, fairy.position());
          }
        });
    helper.succeedWhen(
        () -> {
          helper.assertTrue(grownAt.get() >= 0, "the fairy finished growing");
          helper.assertFalse(fairy.isAlive(), "the fairy is in the bottle");
          helper.assertValueEqual(player.getMainHandItem().getCount(), 1, "bottles left");
          helper.assertValueEqual(
              player.getInventory().countItem(ModItems.JARRED_FAIRY), 1, "Jarred Fairies");
          helper.assertTrue(player.hasAdvancement("first_fairy_catch"), "first catch advancement");
          player.leave();
        });
  }

  @FertileTest(
      structure = "fertilegrounds:empty_medium",
      environment = "fertilegrounds:no_random_ticks",
      skyAccess = true,
      padding = 8)
  public void fairyStillFlyingInIsBusy(final GameTestHelper helper) {
    TestSupport.plantWheat(helper, CROP);
    final BeanFairy fairy =
        helper.spawn(
            ModEntities.BEAN_FAIRY, CROP.getX() + 0.5F, CROP.getY() + 4, CROP.getZ() + 0.5F);
    final TestPlayer player = holdingBottles(helper);
    player.interactOn(fairy, InteractionHand.MAIN_HAND, fairy.position());

    helper.assertTrue(fairy.isAlive(), "the fairy keeps working");
    helper.assertValueEqual(player.getMainHandItem().getCount(), 2, "bottles left");
    helper.assertTrue(
        player.lastOverlayIs("message.fertilegrounds.fairy_busy"), "player told it's busy");
    player.leave();
    fairy.discard();
    helper.succeed();
  }

  @FertileTest
  public void jarredFairyMovesIntoAnEmptyHotel(final GameTestHelper helper) {
    helper.setBlock(HOTEL.below(), Blocks.STONE);
    helper.setBlock(HOTEL, ModBlocks.EMPTY_FAIRY_HOTEL);
    final TestPlayer player = TestPlayer.join(helper);
    player.setItemInHand(InteractionHand.MAIN_HAND, JarredFairyItem.caughtNow(helper.getLevel()));
    player.useHeldItemOn(helper, HOTEL);

    helper.assertBlockPresent(ModBlocks.FAIRY_HOTEL, HOTEL);
    helper.assertTrue(player.getMainHandItem().is(Items.GLASS_BOTTLE), "bottle given back");
    helper.assertTrue(player.hasAdvancement("first_fairy_hotel"), "first hotel advancement");
    player.leave();
    helper.succeed();
  }

  /** A Jarred Fairy fresh from the bottle knows when it slips away. */
  @FertileTest
  public void caughtFairyRecordsWhenItSlipsAway(final GameTestHelper helper) {
    final ItemStack jar = JarredFairyItem.caughtNow(helper.getLevel());
    helper.assertValueEqual(
        jar.get(ModDataComponents.SLIPS_AWAY_AT),
        helper.getLevel().getOverworldClockTime() + 3 * 24000,
        "slips away at");
    helper.succeed();
  }

  private static TestPlayer holdingBottles(final GameTestHelper helper) {
    final TestPlayer player = TestPlayer.join(helper).at(helper, 8, 2, 24);
    player.setShiftKeyDown(true);
    player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GLASS_BOTTLE, 2));
    return player;
  }
}
