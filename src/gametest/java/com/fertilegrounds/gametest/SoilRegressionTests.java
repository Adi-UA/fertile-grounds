package com.fertilegrounds.gametest;

import com.fertilegrounds.block.ModBlocks;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmlandBlock;

/** Past soil bugs, each guarded by the commit that fixed it. */
public class SoilRegressionTests {

  private static final BlockPos SOIL = new BlockPos(3, 1, 3);

  /**
   * Regression for 83fd1fb: trampled farmland reverts to its own dirt tier, not vanilla dirt. A cow
   * dropped 10 blocks always passes vanilla's trample roll (random below fall distance minus 0.5).
   */
  @GameTest(environment = "fertilegrounds:no_random_ticks", skyAccess = true, maxTicks = 200)
  public void enrichedFarmlandTramplesToEnrichedDirt(final GameTestHelper helper) {
    trample(helper, ModBlocks.ENRICHED_FARMLAND, ModBlocks.ENRICHED_DIRT);
  }

  @GameTest(environment = "fertilegrounds:no_random_ticks", skyAccess = true, maxTicks = 200)
  public void superEnrichedFarmlandTramplesToSuperEnrichedDirt(final GameTestHelper helper) {
    trample(helper, ModBlocks.SUPER_ENRICHED_FARMLAND, ModBlocks.SUPER_ENRICHED_DIRT);
  }

  /** Regression for 83fd1fb: dry farmland with no water, rain, or crop reverts to its own dirt. */
  @GameTest(environment = "fertilegrounds:fast_random_ticks", maxTicks = 400)
  public void enrichedFarmlandDriesToEnrichedDirt(final GameTestHelper helper) {
    dryOut(helper, ModBlocks.ENRICHED_FARMLAND, ModBlocks.ENRICHED_DIRT);
  }

  @GameTest(environment = "fertilegrounds:fast_random_ticks", maxTicks = 400)
  public void superEnrichedFarmlandDriesToSuperEnrichedDirt(final GameTestHelper helper) {
    dryOut(helper, ModBlocks.SUPER_ENRICHED_FARMLAND, ModBlocks.SUPER_ENRICHED_DIRT);
  }

  /** Regression for 1e5d483: sugar cane needs Enriched Sand in #minecraft:sand. */
  @GameTest
  public void enrichedSandCountsAsSand(final GameTestHelper helper) {
    helper.setBlock(SOIL, ModBlocks.ENRICHED_SAND);
    helper.assertBlockTag(BlockTags.SAND, SOIL);
    helper.succeed();
  }

  private static void trample(final GameTestHelper helper, final Block farmland, final Block dirt) {
    helper.setBlock(SOIL, farmland);
    helper.spawn(EntityTypes.COW, SOIL.getX() + 0.5F, SOIL.getY() + 11, SOIL.getZ() + 0.5F);
    helper.succeedWhen(() -> helper.assertBlockPresent(dirt, SOIL));
  }

  private static void dryOut(final GameTestHelper helper, final Block farmland, final Block dirt) {
    helper.setBlock(SOIL, farmland.defaultBlockState().setValue(FarmlandBlock.MOISTURE, 0));
    helper.succeedWhen(() -> helper.assertBlockPresent(dirt, SOIL));
  }
}
