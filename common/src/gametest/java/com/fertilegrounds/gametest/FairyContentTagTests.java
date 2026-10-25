package com.fertilegrounds.gametest;

import com.fertilegrounds.entity.ModEntities;
import com.fertilegrounds.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SweetBerryBushBlock;

/**
 * The fairy_crops and fairy_flowers tags. The test data adds sweet berry bushes to fairy_crops
 * (they aren't in #minecraft:crops), the way a pack adds a modded crop.
 */
public class FairyContentTagTests {

  private static final BlockPos CROP = new BlockPos(8, 2, 8);

  @FertileTest(
      structure = "fertilegrounds:empty_medium",
      environment = "fertilegrounds:quick_visit",
      skyAccess = true,
      maxTicks = 600)
  public void fairyGrowsACropAddedToTheTag(final GameTestHelper helper) {
    helper.setBlock(CROP.below(), Blocks.GRASS_BLOCK);
    helper.setBlock(CROP, Blocks.SWEET_BERRY_BUSH);
    helper.spawn(ModEntities.BEAN_FAIRY, CROP.getX() + 0.5F, CROP.getY() + 4, CROP.getZ() + 0.5F);
    helper.succeedWhen(
        () ->
            helper.assertBlockState(
                CROP,
                state -> state.getValue(SweetBerryBushBlock.AGE) == SweetBerryBushBlock.MAX_AGE,
                state -> Component.literal("berry bush fully grown")));
  }

  @FertileTest
  public void smallFlowersAreFairyFlowers(final GameTestHelper helper) {
    final BlockPos pos = new BlockPos(2, 1, 2);
    helper.setBlock(pos, Blocks.POPPY);
    helper.assertBlockTag(ModTags.FAIRY_FLOWERS, pos);
    helper.assertTrue(
        new ItemStack(Items.POPPY).is(ModTags.FAIRY_FLOWER_ITEMS),
        Component.literal("poppy item tagged"));
    helper.succeed();
  }
}
