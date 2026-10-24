package com.fertilegrounds.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Crops in this Minecraft version only stand on the exact {@code Blocks.FARMLAND} block (26.x reads
 * the {@code supports_crops} tag instead). This also accepts any {@link FarmBlock}, so the mod's
 * farmland tiers hold crops; it only ever turns a no into a yes. NeoForge's and Forge's crops still
 * ask {@code mayPlaceOn} when no other mod decides.
 */
@Mixin(CropBlock.class)
abstract class CropBlockMixin {

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
