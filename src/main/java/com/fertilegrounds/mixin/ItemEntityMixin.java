package com.fertilegrounds.mixin;

import com.fertilegrounds.hotel.JarredFairies;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Lets a dropped Jarred Fairy slip away too; vanilla has no tick hook for a single item. */
@Mixin(ItemEntity.class)
abstract class ItemEntityMixin {

  @Inject(method = "tick", at = @At("HEAD"))
  private void fertilegrounds$checkJarredFairy(final CallbackInfo ci) {
    final ItemEntity item = (ItemEntity) (Object) this;
    // Once a second is plenty for a timer counted in days.
    if (item.level() instanceof ServerLevel level && item.tickCount % 20 == 0) {
      JarredFairies.checkDropped(item, level);
    }
  }
}
