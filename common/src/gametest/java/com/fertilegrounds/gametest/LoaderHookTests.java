package com.fertilegrounds.gametest;

import com.fertilegrounds.block.ModBlocks;
import com.fertilegrounds.entity.BeanFairy;
import com.fertilegrounds.entity.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

/**
 * What each loader wires up with its own API instead of shared code: hoe tilling (a Fabric API
 * helper on Fabric, a data map on NeoForge) and the Bean Fairy's attributes.
 */
public class LoaderHookTests {

  private static final BlockPos ENRICHED = new BlockPos(1, 1, 1);

  private static final BlockPos SUPER_ENRICHED = new BlockPos(3, 1, 1);

  @FertileTest(environment = "fertilegrounds:no_random_ticks")
  public void hoeTillsBothDirtTiers(final GameTestHelper helper) {
    helper.setBlock(ENRICHED, ModBlocks.ENRICHED_DIRT);
    helper.setBlock(SUPER_ENRICHED, ModBlocks.SUPER_ENRICHED_DIRT);
    final TestPlayer player = TestPlayer.join(helper);
    player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.IRON_HOE));
    player.useHeldItemOn(helper, ENRICHED);
    player.useHeldItemOn(helper, SUPER_ENRICHED);
    player.leave();
    helper.assertBlockPresent(ModBlocks.ENRICHED_FARMLAND, ENRICHED);
    helper.assertBlockPresent(ModBlocks.SUPER_ENRICHED_FARMLAND, SUPER_ENRICHED);
    helper.succeed();
  }

  /** Like vanilla dirt, enriched dirt only tills when the block above it is air. */
  @FertileTest(environment = "fertilegrounds:no_random_ticks")
  public void hoeLeavesCoveredDirt(final GameTestHelper helper) {
    helper.setBlock(ENRICHED, ModBlocks.ENRICHED_DIRT);
    helper.setBlock(ENRICHED.above(), Blocks.STONE);
    final TestPlayer player = TestPlayer.join(helper);
    player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.IRON_HOE));
    player.useHeldItemOn(helper, ENRICHED);
    player.leave();
    helper.assertBlockPresent(ModBlocks.ENRICHED_DIRT, ENRICHED);
    helper.succeed();
  }

  @FertileTest
  public void beanFairyHasItsAttributes(final GameTestHelper helper) {
    final BeanFairy fairy = helper.spawn(ModEntities.BEAN_FAIRY, new BlockPos(1, 2, 1));
    helper.assertValueEqual(
        fairy.getAttributeBaseValue(Attributes.MAX_HEALTH),
        4.0,
        Component.literal("Bean Fairy max health"));
    helper.assertValueEqual(
        fairy.getAttributeBaseValue(Attributes.FLYING_SPEED),
        0.3,
        Component.literal("Bean Fairy flying speed"));
    helper.succeed();
  }
}
