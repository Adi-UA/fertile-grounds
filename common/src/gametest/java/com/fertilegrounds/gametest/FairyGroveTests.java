package com.fertilegrounds.gametest;

import com.fertilegrounds.block.ModBlocks;
import com.fertilegrounds.entity.BeanFairy;
import com.fertilegrounds.grove.GroveBuilder;
import com.fertilegrounds.grove.GroveJob;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.LeavesBlock;

/**
 * The Fairy Grove: shrine checks, the safety scan, and the finished garden. Building tests use a
 * 37-block-wide empty site with the shrine's soil in the middle, 9 blocks up, so the whole grove
 * (35 wide, 8 deep, 24 high) fits inside it.
 */
public class FairyGroveTests {

  /** The shrine's Super Enriched Dirt. */
  private static final BlockPos SOIL = new BlockPos(18, 9, 18);

  private static final BlockPos HOTEL = SOIL.above();

  /** The same shrine in a smaller test, for refusals that never reach the area scan. */
  private static final BlockPos SMALL_SOIL = new BlockPos(8, 2, 8);

  private static final int BUILD_TICKS = 900;

  @FertileTest(structure = "fertilegrounds:empty_medium", environment = "fertilegrounds:grove")
  public void hotelOnGrassIsRefused(final GameTestHelper helper) {
    buildShrine(helper, SMALL_SOIL, 4);
    helper.setBlock(SMALL_SOIL, Blocks.GRASS_BLOCK);
    assertRefused(helper, SMALL_SOIL.above(), "message.fertilegrounds.grove.no_soil");
  }

  @FertileTest(structure = "fertilegrounds:empty_medium", environment = "fertilegrounds:grove")
  public void missingHotelIsRefused(final GameTestHelper helper) {
    buildShrine(helper, SMALL_SOIL, 4);
    helper.setBlock(SMALL_SOIL.above().east(3), Blocks.AIR);
    assertRefused(helper, SMALL_SOIL.above(), "message.fertilegrounds.grove.missing_hotel");
  }

  @FertileTest(structure = "fertilegrounds:empty_medium", environment = "fertilegrounds:grove")
  public void emptyHotelIsRefused(final GameTestHelper helper) {
    buildShrine(helper, SMALL_SOIL, 4);
    helper.setBlock(SMALL_SOIL.above().east(3), ModBlocks.EMPTY_FAIRY_HOTEL);
    assertRefused(helper, SMALL_SOIL.above(), "message.fertilegrounds.grove.missing_hotel");
  }

  @FertileTest(structure = "fertilegrounds:empty_medium", environment = "fertilegrounds:grove")
  public void tooFewFlowersAreRefused(final GameTestHelper helper) {
    buildShrine(helper, SMALL_SOIL, 3);
    assertRefused(helper, SMALL_SOIL.above(), "message.fertilegrounds.grove.too_few_flowers");
  }

  @FertileTest(structure = "fertilegrounds:empty_medium", environment = "fertilegrounds:grove_off")
  public void turnedOffIsRefused(final GameTestHelper helper) {
    buildShrine(helper, SMALL_SOIL, 4);
    assertRefused(helper, SMALL_SOIL.above(), "message.fertilegrounds.grove.off");
  }

  @FertileTest(structure = "fertilegrounds:grove_site", environment = "fertilegrounds:grove")
  public void chestInTheAreaIsRefused(final GameTestHelper helper) {
    buildShrine(helper, SOIL, 4);
    helper.setBlock(SOIL.offset(10, 1, 0), Blocks.CHEST);
    assertRefused(helper, HOTEL, "message.fertilegrounds.grove.blocked");
  }

  /**
   * Over a hill, a dip, and a tree, the fairies build the whole layout and leave the shrine's plot
   * alone.
   */
  @FertileTest(
      structure = "fertilegrounds:grove_site",
      environment = "fertilegrounds:grove",
      maxTicks = BUILD_TICKS)
  public void wildLandBecomesAGrove(final GameTestHelper helper) {
    buildShrine(helper, SOIL, 4);
    final BlockPos hill = SOIL.offset(-10, 0, 8);
    for (final BlockPos pos :
        BlockPos.betweenClosed(hill.offset(-1, -8, -1), hill.offset(1, 6, 1))) {
      helper.setBlock(pos, Blocks.STONE);
    }
    // Grass in the scanned area, which the grove must accept as land.
    helper.setBlock(hill.above(6), Blocks.GRASS_BLOCK);
    helper.setBlock(SOIL.offset(12, 0, 12), Blocks.GRASS_BLOCK);
    helper.setBlock(SOIL.offset(12, 1, -12), Blocks.OAK_LOG);
    helper.setBlock(SOIL.offset(12, 2, -12), Blocks.OAK_LEAVES);
    final TestPlayer player = holdingDiamonds(helper);
    player.useHeldItemOn(helper, HOTEL);
    helper.assertValueEqual(
        player.getMainHandItem().getCount(), 1, Component.literal("diamonds left"));
    helper.assertTrue(
        player.lastOverlayIs("message.fertilegrounds.grove.started"),
        Component.literal("player told it started"));
    player.leave();

    helper.succeedWhen(
        () -> {
          helper.assertTrue(
              GroveBuilder.jobs(helper.getLevel()).isEmpty(), Component.literal("grove finished"));
          // Plots: wet Enriched Farmland around water.
          helper.assertBlockState(
              SOIL.offset(10, 0, 9),
              state ->
                  state.is(ModBlocks.ENRICHED_FARMLAND)
                      && state.getValue(FarmBlock.MOISTURE) == FarmBlock.MAX_MOISTURE,
              state ->
                  net.minecraft.network.chat.Component.literal("moist farmland, got " + state));
          helper.assertBlockPresent(Blocks.WATER, SOIL.offset(-10, 0, -10));
          helper.assertBlockPresent(Blocks.AIR, SOIL.offset(10, 1, 9));
          // Crossings: hives inside, hedges outside, each with a lantern.
          helper.assertBlockPresent(Blocks.BEEHIVE, SOIL.offset(5, 1, -5));
          helper.assertBlockPresent(Blocks.LANTERN, SOIL.offset(5, 2, -5));
          helper.assertBlockProperty(SOIL.offset(15, 1, 5), LeavesBlock.PERSISTENT, true);
          helper.assertBlockPresent(Blocks.LANTERN, SOIL.offset(15, 2, 5));
          helper.assertBlockPresent(Blocks.GRASS_BLOCK, SOIL.offset(0, 0, 16));
          // The hill and tree are gone, and the dip is filled.
          helper.assertBlockPresent(ModBlocks.ENRICHED_FARMLAND, hill);
          helper.assertBlockPresent(Blocks.AIR, hill.above(6));
          helper.assertBlockPresent(Blocks.AIR, SOIL.offset(12, 2, -12));
          helper.assertBlockPresent(Blocks.DIRT, SOIL.offset(10, -8, 10));
          // The shrine is untouched.
          helper.assertBlockPresent(ModBlocks.CHIME_FAIRY_HOTEL, HOTEL);
          helper.assertBlockPresent(ModBlocks.SUPER_ENRICHED_DIRT, SOIL);
          helper.assertBlockPresent(ModBlocks.FAIRY_HOTEL, HOTEL.north(3));
          // Lanterns light the plots, so no mob spawns there.
          helper.assertTrue(
              helper
                      .getLevel()
                      .getBrightness(LightLayer.BLOCK, helper.absolutePos(SOIL.offset(10, 1, 9)))
                  > 0,
              Component.literal("plot is lit"));
          helper.assertTrue(
              GroveBuilder.fairies(helper.getLevel(), helper.absolutePos(SOIL)).isEmpty(),
              Component.literal("builder fairies gone"));
        });
  }

  /** A chest placed after the check, in a column not yet built, is left alone. */
  @FertileTest(
      structure = "fertilegrounds:grove_site",
      environment = "fertilegrounds:grove",
      maxTicks = BUILD_TICKS)
  public void chestPlacedMidBuildIsLeftAlone(final GameTestHelper helper) {
    buildShrine(helper, SOIL, 4);
    final TestPlayer player = holdingDiamonds(helper);
    player.useHeldItemOn(helper, HOTEL);
    player.leave();
    final BlockPos chest = SOIL.offset(17, 3, 0);
    helper.setBlock(chest, Blocks.CHEST);
    helper.succeedWhen(
        () -> {
          helper.assertTrue(
              GroveBuilder.jobs(helper.getLevel()).isEmpty(), Component.literal("grove finished"));
          helper.assertBlockPresent(Blocks.CHEST, chest);
          helper.assertBlockPresent(Blocks.AIR, chest.below(3));
          helper.assertBlockPresent(Blocks.GRASS_BLOCK, chest.offset(0, -3, 1));
        });
  }

  /**
   * Builder fairies spawn, never take on a visit or finish growing (so they can't be caught), and a
   * job saved halfway resumes with new fairies and finishes.
   */
  @FertileTest(
      structure = "fertilegrounds:grove_site",
      environment = "fertilegrounds:grove",
      maxTicks = BUILD_TICKS)
  public void savedJobResumesWithBuilderFairies(final GameTestHelper helper) {
    buildShrine(helper, SOIL, 4);
    final GroveJob saved = new GroveJob(helper.absolutePos(SOIL), GroveBuilder.columnCount() / 2);
    final Tag encoded =
        GroveJob.CODEC.listOf().encodeStart(NbtOps.INSTANCE, List.of(saved)).getOrThrow();
    final List<GroveJob> loaded =
        GroveJob.CODEC.listOf().parse(NbtOps.INSTANCE, encoded).getOrThrow();
    helper.assertValueEqual(loaded, List.of(saved), Component.literal("job after a save and load"));
    GroveBuilder.add(helper.getLevel(), loaded.getFirst());

    helper
        .startSequence()
        .thenExecuteAfter(
            20,
            () -> {
              final List<BeanFairy> fairies =
                  GroveBuilder.fairies(helper.getLevel(), helper.absolutePos(SOIL));
              helper.assertValueEqual(fairies.size(), 4, Component.literal("builder fairies"));
              for (final BeanFairy fairy : fairies) {
                helper.assertTrue(fairy.isGroveBuilder(), Component.literal("fairy is a builder"));
                helper.assertTrue(
                    fairy.getVisit() == null, Component.literal("builder has no visit"));
                helper.assertTrue(
                    !fairy.hasFinishedGrowing(), Component.literal("builder can't be caught"));
              }
            })
        .thenWaitUntil(
            () ->
                helper.assertTrue(
                    GroveBuilder.jobs(helper.getLevel()).isEmpty(),
                    Component.literal("grove finished")))
        .thenExecute(
            () -> {
              helper.assertBlockPresent(Blocks.GRASS_BLOCK, SOIL.offset(-17, 0, 0));
              helper.assertTrue(
                  TestSupport.fairies(helper).stream().noneMatch(BeanFairy::isGroveBuilder),
                  Component.literal("builder fairies gone"));
            })
        .thenSucceed();
  }

  /**
   * A Chime Hotel on Super Enriched Dirt, an occupied hotel 3 blocks out on each side (standing on
   * stone), and {@code flowers} poppies on grass near it.
   */
  private static void buildShrine(
      final GameTestHelper helper, final BlockPos soil, final int flowers) {
    helper.setBlock(soil, ModBlocks.SUPER_ENRICHED_DIRT);
    helper.setBlock(soil.above(), ModBlocks.CHIME_FAIRY_HOTEL);
    for (final Direction side : Direction.Plane.HORIZONTAL) {
      helper.setBlock(soil.relative(side, 3), Blocks.STONE);
      helper.setBlock(soil.above().relative(side, 3), ModBlocks.FAIRY_HOTEL);
    }
    final List<BlockPos> flowerSpots =
        List.of(
            soil.offset(1, 0, 1),
            soil.offset(-1, 0, 1),
            soil.offset(1, 0, -1),
            soil.offset(-1, 0, -1));
    for (int i = 0; i < flowers; i++) {
      helper.setBlock(flowerSpots.get(i), Blocks.GRASS_BLOCK);
      helper.setBlock(flowerSpots.get(i).above(), Blocks.POPPY);
    }
  }

  private static TestPlayer holdingDiamonds(final GameTestHelper helper) {
    final TestPlayer player = TestPlayer.join(helper);
    player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.DIAMOND, 2));
    return player;
  }

  /** The diamond stays, the player reads why, and nothing gets built. */
  private static void assertRefused(
      final GameTestHelper helper, final BlockPos hotel, final String messageKey) {
    final TestPlayer player = holdingDiamonds(helper);
    player.useHeldItemOn(helper, hotel);
    helper.assertValueEqual(
        player.getMainHandItem().getCount(), 2, Component.literal("diamonds left"));
    helper.assertTrue(
        player.lastOverlayIs(messageKey), Component.literal("player told " + messageKey));
    helper.assertTrue(
        GroveBuilder.jobs(helper.getLevel()).stream()
            .noneMatch(job -> job.soil().equals(helper.absolutePos(hotel.below()))),
        Component.literal("no grove started"));
    player.leave();
    helper.succeed();
  }
}
