package com.fertilegrounds.gametest;

import com.fertilegrounds.block.ChimeHotelBlock;
import com.fertilegrounds.block.FairyHotelBlock;
import com.fertilegrounds.block.ModBlocks;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * When a Chime Hotel rings. Its 8-block field reaches past a small structure, so these tests use
 * the 33x33 one with the hotel in the middle, clear of crops left by earlier tests.
 */
public class ChimeHotelTests {

  private static final BlockPos HOTEL = new BlockPos(16, 2, 16);

  /** A 5x5 field 2 to 6 blocks south of the hotel. */
  private static final BlockPos FIELD_CORNER = new BlockPos(14, 2, 18);

  private static final int FIELD_SIZE = 5;

  /**
   * A ripe field turns {@code READY} on once and keeps it on across later checks; breaking the
   * crops turns it off, and a regrown field turns it on again.
   */
  @FertileTest(
      structure = "fertilegrounds:empty_large",
      environment = "fertilegrounds:no_random_ticks",
      maxTicks = 800)
  public void ripeFieldReadiesOnceAndReArmsAfterHarvest(final GameTestHelper helper) {
    HotelEffectTests.placeHotel(helper, HOTEL, ModBlocks.CHIME_FAIRY_HOTEL);
    plantField(helper, FIELD_SIZE * FIELD_SIZE);
    final AtomicInteger readied = countReadyTurningOn(helper);
    helper
        .startSequence()
        .thenWaitUntil(() -> assertReady(helper, true))
        .thenIdle(2 * ChimeHotelBlock.CHECK_INTERVAL_TICKS + 20)
        .thenExecute(
            () -> {
              helper.assertValueEqual(readied.get(), 1, Component.literal("times READY turned on"));
              forEachFieldSpot(
                  helper, FIELD_SIZE * FIELD_SIZE, pos -> helper.setBlock(pos, Blocks.AIR));
            })
        .thenWaitUntil(() -> assertReady(helper, false))
        .thenExecute(() -> plantField(helper, FIELD_SIZE * FIELD_SIZE))
        .thenWaitUntil(() -> assertReady(helper, true))
        .thenExecute(
            () ->
                helper.assertValueEqual(
                    readied.get(), 2, Component.literal("times READY turned on")))
        .thenSucceed();
  }

  /** Three ripe crops aren't a field, so the hotel stays quiet through two checks. */
  @FertileTest(
      structure = "fertilegrounds:empty_large",
      environment = "fertilegrounds:no_random_ticks",
      maxTicks = 300)
  public void threeRipeCropsStayQuiet(final GameTestHelper helper) {
    HotelEffectTests.placeHotel(helper, HOTEL, ModBlocks.CHIME_FAIRY_HOTEL);
    plantField(helper, 3);
    helper.runAfterDelay(
        2 * ChimeHotelBlock.CHECK_INTERVAL_TICKS + 20,
        () -> {
          assertReady(helper, false);
          helper.succeed();
        });
  }

  /** 19 of 25 ripe (76%) is below the 80% default; ripening a 20th (80%) makes the field ready. */
  @FertileTest(
      structure = "fertilegrounds:empty_large",
      environment = "fertilegrounds:no_random_ticks",
      maxTicks = 500)
  public void readyAtEightyPercent(final GameTestHelper helper) {
    HotelEffectTests.placeHotel(helper, HOTEL, ModBlocks.CHIME_FAIRY_HOTEL);
    forEachFieldSpot(helper, FIELD_SIZE * FIELD_SIZE, pos -> TestSupport.plantWheat(helper, pos));
    plantField(helper, 19);
    helper
        .startSequence()
        .thenIdle(ChimeHotelBlock.CHECK_INTERVAL_TICKS + 20)
        .thenExecute(
            () -> {
              assertReady(helper, false);
              plantField(helper, 20);
            })
        .thenWaitUntil(() -> assertReady(helper, true))
        .thenSucceed();
  }

  /**
   * Petting a Chime Hotel lasts until dawn, not just until its next 5-second check. The clock
   * starts 1,000 ticks before dawn, in its own environment since other tests set the clock too.
   */
  @FertileTest(environment = "fertilegrounds:chime_petting", maxTicks = 1300)
  public void pettingLastsUntilDawn(final GameTestHelper helper) {
    final BlockPos hotel = new BlockPos(3, 2, 3);
    TestSupport.command(helper, "time set 23000");
    HotelEffectTests.placeHotel(helper, hotel, ModBlocks.CHIME_FAIRY_HOTEL);
    final TestPlayer player = TestPlayer.join(helper);
    player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BRUSH));
    helper
        .startSequence()
        .thenExecute(() -> player.useHeldItemOn(helper, hotel))
        .thenIdle(5 * ChimeHotelBlock.CHECK_INTERVAL_TICKS)
        .thenExecute(() -> helper.assertBlockProperty(hotel, FairyHotelBlock.PETTED, true))
        .thenWaitUntil(() -> helper.assertBlockProperty(hotel, FairyHotelBlock.PETTED, false))
        .thenExecute(
            () -> {
              final long timeOfDay = helper.getLevel().getDayTime() % 24000;
              helper.assertTrue(
                  timeOfDay < 23000,
                  Component.literal(
                      "petting lasted until dawn (ended at time of day " + timeOfDay + ")"));
              player.leave();
            })
        .thenSucceed();
  }

  /** Counts each check where the hotel's {@code READY} state goes from off to on. */
  private static AtomicInteger countReadyTurningOn(final GameTestHelper helper) {
    final AtomicInteger count = new AtomicInteger();
    final AtomicBoolean was = new AtomicBoolean(false);
    helper.onEachTick(
        () -> {
          final boolean ready = isReady(helper);
          if (ready && !was.get()) {
            count.incrementAndGet();
          }
          was.set(ready);
        });
    return count;
  }

  private static boolean isReady(final GameTestHelper helper) {
    final BlockState state = helper.getBlockState(HOTEL);
    return state.is(ModBlocks.CHIME_FAIRY_HOTEL) && state.getValue(ChimeHotelBlock.READY);
  }

  private static void assertReady(final GameTestHelper helper, final boolean ready) {
    helper.assertBlockProperty(HOTEL, ChimeHotelBlock.READY, ready);
  }

  /** Plants fully grown wheat on the first {@code count} spots of the field. */
  private static void plantField(final GameTestHelper helper, final int count) {
    forEachFieldSpot(
        helper,
        count,
        pos -> {
          helper.setBlock(pos.below(), Blocks.FARMLAND);
          helper.setBlock(pos, Blocks.WHEAT.defaultBlockState().setValue(CropBlock.AGE, 7));
        });
  }

  private static void forEachFieldSpot(
      final GameTestHelper helper, final int count, final Consumer<BlockPos> action) {
    for (int i = 0; i < count; i++) {
      action.accept(FIELD_CORNER.offset(i % FIELD_SIZE, 0, i / FIELD_SIZE));
    }
  }
}
