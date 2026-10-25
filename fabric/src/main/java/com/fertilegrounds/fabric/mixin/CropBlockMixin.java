package com.fertilegrounds.fabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Vanilla crops grow fastest on moist {@code Blocks.FARMLAND} only. The first {@code is(Block)} in
 * {@code getGrowthSpeed} is that farmland check, and this also accepts any {@link FarmBlock}.
 * NeoForge and Forge replace that check with their own soil hooks, so only Fabric needs this.
 */
@Mixin(CropBlock.class)
abstract class CropBlockMixin {

  @WrapOperation(
      method = "getGrowthSpeed",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z",
              ordinal = 0))
  private static boolean fertilegrounds$anyFarmlandGrows(
      final BlockState state, final Block block, final Operation<Boolean> original) {
    return original.call(state, block) || state.getBlock() instanceof FarmBlock;
  }
}
