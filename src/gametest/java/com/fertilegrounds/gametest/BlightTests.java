package com.fertilegrounds.gametest;

import com.fertilegrounds.blight.Blight;
import com.fertilegrounds.block.ModBlocks;
import com.fertilegrounds.entity.ModEntities;
import com.fertilegrounds.fairy.FairyVisit;
import com.fertilegrounds.fairy.FairyVisits;
import com.fertilegrounds.item.ModItems;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Blighted Dirt: spreading, withering, the witch curse, crafting, and every cure. Spreading tests
 * call the block's random tick directly with natural random ticks off, so blight never wanders into
 * the test world's own ground.
 */
public class BlightTests {

  private static final BlockPos BLIGHT = new BlockPos(4, 2, 4);

  private static final int SPREAD_TRIES = 300;

  private static final String WITCH_MESSAGE = "message.fertilegrounds.witch_blight";

  @GameTest
  public void breakingDropsItself(final GameTestHelper helper) {
    helper.setBlock(BLIGHT, ModBlocks.BLIGHTED_DIRT);
    helper.getLevel().destroyBlock(helper.absolutePos(BLIGHT), true);
    helper.assertItemEntityPresent(ModBlocks.BLIGHTED_DIRT.asItem(), BLIGHT, 2);
    helper.succeed();
  }

  @GameTest(environment = "fertilegrounds:no_random_ticks")
  public void hoeDoesNotTillIt(final GameTestHelper helper) {
    helper.setBlock(BLIGHT, ModBlocks.BLIGHTED_DIRT);
    final TestPlayer player = TestPlayer.join(helper);
    player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.IRON_HOE));
    player.useHeldItemOn(helper, BLIGHT);
    helper.assertBlockPresent(ModBlocks.BLIGHTED_DIRT, BLIGHT);
    player.leave();
    helper.succeed();
  }

  /**
   * Farmland, grass, and sand on the surface fall; stone, a dirt path, and dirt buried under stone
   * hold.
   */
  @GameTest(environment = "fertilegrounds:blight_always")
  public void spreadsIntoSurfaceSoilOnly(final GameTestHelper helper) {
    setUpNeighbors(helper);
    tickBlight(helper);
    for (final Direction side : List.of(Direction.EAST, Direction.WEST, Direction.NORTH)) {
      helper.assertBlockPresent(ModBlocks.BLIGHTED_DIRT, BLIGHT.relative(side));
    }
    helper.assertBlockPresent(Blocks.STONE, BLIGHT.south());
    helper.assertBlockPresent(Blocks.DIRT_PATH, BLIGHT.below());
    helper.assertBlockPresent(Blocks.DIRT, BLIGHT.above());
    helper.succeed();
  }

  @GameTest(environment = "fertilegrounds:blight_off")
  public void zeroChanceNeverSpreads(final GameTestHelper helper) {
    setUpNeighbors(helper);
    tickBlight(helper);
    helper.assertBlockPresent(Blocks.FARMLAND, BLIGHT.east());
    helper.assertBlockPresent(Blocks.GRASS_BLOCK, BLIGHT.west());
    helper.assertBlockPresent(Blocks.SAND, BLIGHT.north());
    helper.succeed();
  }

  @GameTest(environment = "fertilegrounds:no_random_ticks")
  public void ripeWheatWithersWithoutDrops(final GameTestHelper helper) {
    helper.setBlock(BLIGHT, Blocks.FARMLAND);
    helper.setBlock(
        BLIGHT.above(),
        Blocks.WHEAT.defaultBlockState().setValue(CropBlock.AGE, CropBlock.MAX_AGE));
    Blight.take(helper.getLevel(), helper.absolutePos(BLIGHT));
    helper.assertBlockPresent(ModBlocks.BLIGHTED_DIRT, BLIGHT);
    helper.assertBlockPresent(Blocks.DEAD_BUSH, BLIGHT.above());
    helper.runAfterDelay(
        5,
        () -> {
          helper.assertItemEntityNotPresent(Items.WHEAT, BLIGHT, 3);
          helper.assertItemEntityNotPresent(Items.WHEAT_SEEDS, BLIGHT, 3);
          helper.assertBlockPresent(Blocks.DEAD_BUSH, BLIGHT.above());
          helper.succeed();
        });
  }

  @GameTest(environment = "fertilegrounds:blight_always")
  public void playerKilledWitchBlightsTheGrassAndSaysSo(final GameTestHelper helper) {
    floor(helper, Blocks.GRASS_BLOCK);
    final TestPlayer player = TestPlayer.join(helper).at(helper, 1, 2, 1);
    killWitch(helper, helper.getLevel().damageSources().playerAttack(player));
    helper.assertBlockPresent(ModBlocks.BLIGHTED_DIRT, BLIGHT.below());
    helper.assertValueEqual(player.chatCount(WITCH_MESSAGE), 1L, "witch blight messages");
    player.leave();
    helper.succeed();
  }

  @GameTest(environment = "fertilegrounds:blight_always")
  public void witchOnStoneChangesNothing(final GameTestHelper helper) {
    floor(helper, Blocks.STONE);
    final TestPlayer player = TestPlayer.join(helper).at(helper, 1, 2, 1);
    killWitch(helper, helper.getLevel().damageSources().playerAttack(player));
    helper.assertBlockPresent(Blocks.STONE, BLIGHT.below());
    helper.assertValueEqual(player.chatCount(WITCH_MESSAGE), 0L, "witch blight messages");
    player.leave();
    helper.succeed();
  }

  @GameTest(environment = "fertilegrounds:blight_always")
  public void witchKilledByLavaNeverBlights(final GameTestHelper helper) {
    floor(helper, Blocks.GRASS_BLOCK);
    killWitch(helper, helper.getLevel().damageSources().lava());
    helper.assertBlockPresent(Blocks.GRASS_BLOCK, BLIGHT.below());
    helper.succeed();
  }

  @GameTest
  public void craftsFromDirtWartRodAndEye(final GameTestHelper helper) {
    final CraftingInput grid =
        CraftingInput.of(
            2,
            2,
            List.of(
                new ItemStack(Items.DIRT),
                new ItemStack(Items.NETHER_WART),
                new ItemStack(Items.BLAZE_ROD),
                new ItemStack(Items.FERMENTED_SPIDER_EYE)));
    final ItemStack crafted =
        helper
            .getLevel()
            .getServer()
            .getRecipeManager()
            .getRecipeFor(RecipeType.CRAFTING, grid, helper.getLevel())
            .map(recipe -> recipe.value().assemble(grid))
            .orElse(ItemStack.EMPTY);
    helper.assertTrue(crafted.is(ModBlocks.BLIGHTED_DIRT.asItem()), "crafted Blighted Dirt");
    helper.succeed();
  }

  @GameTest(environment = "fertilegrounds:no_random_ticks")
  public void fertilizerByHandCures(final GameTestHelper helper) {
    helper.setBlock(BLIGHT, ModBlocks.BLIGHTED_DIRT);
    final TestPlayer player = TestPlayer.join(helper);
    player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.FERTILIZER, 2));
    player.useHeldItemOn(helper, BLIGHT);
    helper.assertBlockPresent(Blocks.DIRT, BLIGHT);
    helper.assertValueEqual(player.getMainHandItem().getCount(), 1, "Fertilizer left");
    player.leave();
    helper.succeed();
  }

  @GameTest(environment = "fertilegrounds:no_random_ticks")
  public void fertilizerFromADispenserCures(final GameTestHelper helper) {
    final BlockPos dispenser = BLIGHT.west();
    helper.setBlock(BLIGHT, ModBlocks.BLIGHTED_DIRT);
    helper.setBlock(
        dispenser,
        Blocks.DISPENSER.defaultBlockState().setValue(DispenserBlock.FACING, Direction.EAST));
    helper
        .getBlockEntity(dispenser, DispenserBlockEntity.class)
        .setItem(0, new ItemStack(ModItems.FERTILIZER, 2));
    helper.setBlock(dispenser.west(), Blocks.REDSTONE_BLOCK);
    helper.succeedWhen(
        () -> {
          helper.assertBlockPresent(Blocks.DIRT, BLIGHT);
          helper.assertValueEqual(
              helper.getBlockEntity(dispenser, DispenserBlockEntity.class).getItem(0).getCount(),
              1,
              "Fertilizer left");
        });
  }

  /** A visiting fairy cures the blight under its 5x5 patch and leaves the rest. */
  @GameTest(
      structure = "fertilegrounds:empty_medium",
      environment = "fertilegrounds:quick_visit",
      skyAccess = true,
      padding = 8,
      maxTicks = 600)
  public void fairyVisitCuresItsPatch(final GameTestHelper helper) {
    final BlockPos crop = new BlockPos(8, 2, 8);
    final BlockPos inPatch = crop.offset(2, -1, 0);
    final BlockPos outside = crop.offset(3, -1, 0);
    TestSupport.plantWheat(helper, crop);
    helper.setBlock(inPatch, ModBlocks.BLIGHTED_DIRT);
    helper.setBlock(outside, ModBlocks.BLIGHTED_DIRT);
    helper.spawn(ModEntities.BEAN_FAIRY, crop.getX() + 0.5F, crop.getY() + 4, crop.getZ() + 0.5F);
    helper.succeedWhen(
        () -> {
          helper.assertBlockPresent(Blocks.DIRT, inPatch);
          helper.assertBlockPresent(ModBlocks.BLIGHTED_DIRT, outside);
          helper.assertTrue(TestSupport.fairies(helper).isEmpty(), "fairy left after growing");
        });
  }

  /** A visit that grows at dawn, with no fairy flying, cures its patch too. */
  @GameTest(
      structure = "fertilegrounds:empty_medium",
      environment = "fertilegrounds:overnight_visits",
      maxTicks = 200)
  public void overnightVisitCuresItsPatch(final GameTestHelper helper) {
    final BlockPos crop = new BlockPos(8, 2, 8);
    final BlockPos inPatch = crop.offset(-2, -1, 2);
    TestSupport.plantWheat(helper, crop);
    helper.setBlock(inPatch, ModBlocks.BLIGHTED_DIRT);
    helper
        .startSequence()
        .thenExecute(() -> TestSupport.command(helper, "time set 13000"))
        .thenExecuteAfter(
            30,
            () -> {
              final long never = helper.getLevel().getGameTime() + 1_000_000;
              FairyVisits.add(
                  helper.getLevel(),
                  FairyVisit.of(helper.absolutePos(crop), never, FairyVisit.Source.UNOWNED, null));
              TestSupport.command(helper, "time set 24000");
            })
        .thenExecuteAfter(30, () -> helper.assertBlockPresent(Blocks.DIRT, inPatch))
        .thenSucceed();
  }

  /**
   * Around the blight: farmland east, grass west, sand north (on stone so it doesn't fall), stone
   * south, a dirt path below, and dirt above that is buried under stone.
   */
  private static void setUpNeighbors(final GameTestHelper helper) {
    helper.setBlock(BLIGHT, ModBlocks.BLIGHTED_DIRT);
    helper.setBlock(BLIGHT.east(), Blocks.FARMLAND);
    helper.setBlock(BLIGHT.west(), Blocks.GRASS_BLOCK);
    helper.setBlock(BLIGHT.north().below(), Blocks.STONE);
    helper.setBlock(BLIGHT.north(), Blocks.SAND);
    helper.setBlock(BLIGHT.south(), Blocks.STONE);
    helper.setBlock(BLIGHT.below(), Blocks.DIRT_PATH);
    helper.setBlock(BLIGHT.above(), Blocks.DIRT);
    helper.setBlock(BLIGHT.above(2), Blocks.STONE);
  }

  private static void tickBlight(final GameTestHelper helper) {
    final ServerLevel level = helper.getLevel();
    final BlockPos pos = helper.absolutePos(BLIGHT);
    for (int i = 0; i < SPREAD_TRIES; i++) {
      final BlockState state = level.getBlockState(pos);
      state.randomTick(level, pos, level.getRandom());
    }
  }

  /** A 3x3 floor under the spot where the witch stands. */
  private static void floor(final GameTestHelper helper, final Block block) {
    for (final BlockPos pos :
        BlockPos.betweenClosed(BLIGHT.offset(-1, -1, -1), BLIGHT.offset(1, -1, 1))) {
      helper.setBlock(pos, block);
    }
  }

  private static void killWitch(final GameTestHelper helper, final DamageSource source) {
    final Witch witch =
        helper.spawn(EntityTypes.WITCH, BLIGHT.getX() + 0.5F, BLIGHT.getY(), BLIGHT.getZ() + 0.5F);
    witch.hurtServer(helper.getLevel(), source, 1000);
  }
}
