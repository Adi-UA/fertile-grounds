package com.fertilegrounds.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.AttachedStemBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Like {@link CropBlockMixin}: a stem with its grown melon or pumpkin stays on any farmland. */
@Mixin(AttachedStemBlock.class)
abstract class AttachedStemBlockMixin {

  @Inject(method = "mayPlaceOn", at = @At("RETURN"), cancellable = true)
  private void fertilegrounds$anyFarmland(
      final BlockState state,
      final BlockGetter level,
      final BlockPos pos,
      final CallbackInfoReturnable<Boolean> cir) {
    if (!cir.getReturnValueZ() && state.getBlock() instanceof FarmBlock) {
      cir.setReturnValue(true);
    }
  }
}
