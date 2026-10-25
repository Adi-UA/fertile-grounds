package com.fertilegrounds.gametest;

import com.fertilegrounds.block.ChimeHotelBlock;
import com.fertilegrounds.block.ModBlocks;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.ComparatorBlockEntity;

/** What a comparator reads from the mod's blocks. */
public class RedstoneTests {

  private static final BlockPos BLOCK = new BlockPos(3, 2, 3);

  @FertileTest(environment = "fertilegrounds:no_random_ticks")
  public void enrichedDirtReadsSeven(final GameTestHelper helper) {
    assertComparatorReads(helper, ModBlocks.ENRICHED_DIRT, 7);
  }

  @FertileTest(environment = "fertilegrounds:no_random_ticks")
  public void enrichedFarmlandReadsSeven(final GameTestHelper helper) {
    assertComparatorReads(helper, ModBlocks.ENRICHED_FARMLAND, 7);
  }

  @FertileTest(environment = "fertilegrounds:no_random_ticks")
  public void enrichedSandReadsSeven(final GameTestHelper helper) {
    assertComparatorReads(helper, ModBlocks.ENRICHED_SAND, 7);
  }

  @FertileTest(environment = "fertilegrounds:no_random_ticks")
  public void superEnrichedDirtReadsFifteen(final GameTestHelper helper) {
    assertComparatorReads(helper, ModBlocks.SUPER_ENRICHED_DIRT, 15);
  }

  @FertileTest(environment = "fertilegrounds:no_random_ticks")
  public void superEnrichedFarmlandReadsFifteen(final GameTestHelper helper) {
    assertComparatorReads(helper, ModBlocks.SUPER_ENRICHED_FARMLAND, 15);
  }

  /** Waits past a Chime Hotel's first check, so a quiet hotel is seen reading 0 after it. */
  @FertileTest(environment = "fertilegrounds:no_random_ticks", maxTicks = 200)
  public void hotelsReadZero(final GameTestHelper helper) {
    final BlockPos chime = BLOCK.east(2);
    placeWithComparator(helper, BLOCK, ModBlocks.FAIRY_HOTEL);
    placeWithComparator(helper, chime, ModBlocks.CHIME_FAIRY_HOTEL);
    helper.runAfterDelay(
        ChimeHotelBlock.CHECK_INTERVAL_TICKS + 20,
        () -> {
          assertSignal(helper, BLOCK, 0);
          assertSignal(helper, chime, 0);
          helper.succeed();
        });
  }

  /**
   * A growing field reads 0; when it ripens and the hotel chimes, the comparator reads 15 for 1
   * second, then 0 again. The field reaches past a small structure, so this uses the 33x33 one.
   */
  @FertileTest(
      structure = "fertilegrounds:empty_large",
      environment = "fertilegrounds:no_random_ticks",
      maxTicks = 600)
  public void chimeHotelPulsesForOneSecond(final GameTestHelper helper) {
    final BlockPos hotel = new BlockPos(16, 2, 16);
    placeWithComparator(helper, hotel, ModBlocks.CHIME_FAIRY_HOTEL);
    final BlockPos corner = new BlockPos(14, 2, 18);
    final AtomicLong pulseStart = new AtomicLong();
    helper
        .startSequence()
        .thenExecute(() -> plantField(helper, corner, 0))
        .thenIdle(ChimeHotelBlock.CHECK_INTERVAL_TICKS + 20)
        .thenExecute(
            () -> {
              assertSignal(helper, hotel, 0);
              plantField(helper, corner, CropBlock.MAX_AGE);
            })
        .thenWaitUntil(() -> assertSignal(helper, hotel, 15))
        .thenExecute(() -> pulseStart.set(helper.getTick()))
        .thenWaitUntil(() -> assertSignal(helper, hotel, 0))
        .thenExecute(
            () -> {
              final long length = helper.getTick() - pulseStart.get();
              helper.assertTrue(
                  Math.abs(length - ChimeHotelBlock.RINGING_TICKS) <= 2,
                  Component.literal("pulse lasted " + length + " ticks"));
            })
        .thenSucceed();
  }

  /**
   * Sets {@code block} on stone with a comparator east of it reading it. A comparator reads the
   * block in the direction it faces. The comparator goes down first: a block with a comparator
   * output updates the comparators next to it when placed, while a comparator set without a player
   * skips its own first reading.
   */
  private static void placeWithComparator(
      final GameTestHelper helper, final BlockPos pos, final Block block) {
    helper.setBlock(pos.below(), Blocks.STONE);
    helper.setBlock(pos.east().below(), Blocks.STONE);
    helper.setBlock(
        pos.east(),
        Blocks.COMPARATOR.defaultBlockState().setValue(ComparatorBlock.FACING, Direction.WEST));
    helper.setBlock(pos, block);
  }

  private static void assertComparatorReads(
      final GameTestHelper helper, final Block block, final int signal) {
    placeWithComparator(helper, BLOCK, block);
    helper.succeedWhen(() -> assertSignal(helper, BLOCK, signal));
  }

  private static void assertSignal(
      final GameTestHelper helper, final BlockPos read, final int signal) {
    final ComparatorBlockEntity comparator =
        helper.getBlockEntity(read.east(), ComparatorBlockEntity.class);
    helper.assertValueEqual(
        comparator.getOutputSignal(), signal, Component.literal("comparator signal"));
  }

  /** A 5x5 wheat field at {@code age}, on farmland. */
  private static void plantField(
      final GameTestHelper helper, final BlockPos corner, final int age) {
    for (final BlockPos pos : BlockPos.betweenClosed(corner, corner.offset(4, 0, 4))) {
      helper.setBlock(pos.below(), Blocks.FARMLAND);
      helper.setBlock(pos, Blocks.WHEAT.defaultBlockState().setValue(CropBlock.AGE, age));
    }
  }
}
