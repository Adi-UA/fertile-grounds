package com.fertilegrounds.fabric.mixin;

import com.fertilegrounds.hotel.FairyHotelLookup;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Stops trampling near occupied Fairy Hotels. It wraps only the call in {@code fallOn}, so farmland
 * still dries out. NeoForge and Forge use their {@code FarmlandTrampleEvent} instead, and the mod's
 * own tiers check in their own {@code fallOn}.
 */
@Mixin(FarmBlock.class)
abstract class FarmlandBlockMixin {

  @WrapOperation(
      method = "fallOn",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/world/level/block/FarmBlock;turnToDirt(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"))
  private void fertilegrounds$protectNearHotels(
      final Entity entity,
      final BlockState state,
      final Level level,
      final BlockPos pos,
      final Operation<Void> original) {
    if (!(level instanceof ServerLevel serverLevel
        && FairyHotelLookup.protectsFromTrampling(serverLevel, pos))) {
      original.call(entity, state, level, pos);
    }
  }
}
