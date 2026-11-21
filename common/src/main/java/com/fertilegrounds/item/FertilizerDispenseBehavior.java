package com.fertilegrounds.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;

/**
 * Lets a dispenser apply Fertilizer to the block it faces, the same way vanilla dispensers apply
 * Bone Meal. On a block Fertilizer can't upgrade, the dispenser keeps the item and plays its failed
 * click.
 */
public class FertilizerDispenseBehavior extends OptionalDispenseItemBehavior {

  @Override
  protected ItemStack execute(final BlockSource source, final ItemStack dispensed) {
    final BlockPos target =
        source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
    this.setSuccess(FertilizerItem.upgrade(source.getLevel(), target));
    if (this.isSuccess()) {
      dispensed.shrink(1);
    }
    return dispensed;
  }
}
