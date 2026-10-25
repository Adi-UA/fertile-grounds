package com.fertilegrounds.gametest;

import com.fertilegrounds.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;

/** Farmland trampling and drying, Enriched Sand's tag, and stems on mod farmland. */
public class SoilRegressionTests {

  private static final BlockPos SOIL = new BlockPos(3, 1, 3);

  /**
   * Trampled farmland reverts to its own dirt tier, not vanilla dirt. A cow dropped 10 blocks
   * always passes vanilla's trample roll (random below fall distance minus 0.5).
   */
  @FertileTest(environment = "fertilegrounds:no_random_ticks", skyAccess = true, maxTicks = 200)
  public void enrichedFarmlandTramplesToEnrichedDirt(final GameTestHelper helper) {
    trample(helper, ModBlocks.ENRICHED_FARMLAND, ModBlocks.ENRICHED_DIRT);
  }

  @FertileTest(environment = "fertilegrounds:no_random_ticks", skyAccess = true, maxTicks = 200)
  public void superEnrichedFarmlandTramplesToSuperEnrichedDirt(final GameTestHelper helper) {
    trample(helper, ModBlocks.SUPER_ENRICHED_FARMLAND, ModBlocks.SUPER_ENRICHED_DIRT);
  }

  /** Dry farmland with no water, rain, or crop reverts to its own dirt tier. */
  @FertileTest(environment = "fertilegrounds:fast_random_ticks", maxTicks = 400)
  public void enrichedFarmlandDriesToEnrichedDirt(final GameTestHelper helper) {
    dryOut(helper, ModBlocks.ENRICHED_FARMLAND, ModBlocks.ENRICHED_DIRT);
  }

  @FertileTest(environment = "fertilegrounds:fast_random_ticks", maxTicks = 400)
  public void superEnrichedFarmlandDriesToSuperEnrichedDirt(final GameTestHelper helper) {
    dryOut(helper, ModBlocks.SUPER_ENRICHED_FARMLAND, ModBlocks.SUPER_ENRICHED_DIRT);
  }

  /** Sugar cane needs Enriched Sand in #minecraft:sand. */
  @FertileTest
  public void enrichedSandCountsAsSand(final GameTestHelper helper) {
    helper.setBlock(SOIL, ModBlocks.ENRICHED_SAND);
    helper.assertBlockTag(BlockTags.SAND, SOIL);
    helper.succeed();
  }

  /** Melon and pumpkin stems stand on both farmland tiers. */
  @FertileTest
  public void stemsStandOnModFarmland(final GameTestHelper helper) {
    for (final Block farmland :
        new Block[] {ModBlocks.ENRICHED_FARMLAND, ModBlocks.SUPER_ENRICHED_FARMLAND}) {
      helper.setBlock(SOIL, farmland);
      for (final Block stem :
          new Block[] {
            Blocks.MELON_STEM,
            Blocks.PUMPKIN_STEM,
            Blocks.ATTACHED_MELON_STEM,
            Blocks.ATTACHED_PUMPKIN_STEM
          }) {
        helper.assertTrue(
            stem.defaultBlockState()
                .canSurvive(helper.getLevel(), helper.absolutePos(SOIL.above())),
            stem + " stands on " + farmland);
      }
    }
    helper.succeed();
  }

  private static void trample(final GameTestHelper helper, final Block farmland, final Block dirt) {
    helper.setBlock(SOIL, farmland);
    helper.spawn(EntityType.COW, SOIL.getX() + 0.5F, SOIL.getY() + 11, SOIL.getZ() + 0.5F);
    helper.succeedWhen(() -> helper.assertBlockPresent(dirt, SOIL));
  }

  private static void dryOut(final GameTestHelper helper, final Block farmland, final Block dirt) {
    helper.setBlock(SOIL, farmland.defaultBlockState().setValue(FarmBlock.MOISTURE, 0));
    helper.succeedWhen(() -> helper.assertBlockPresent(dirt, SOIL));
  }
}
