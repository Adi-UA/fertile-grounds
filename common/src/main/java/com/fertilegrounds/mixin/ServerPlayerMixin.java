package com.fertilegrounds.mixin;

import com.fertilegrounds.hotel.JarredFairies;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Checks a container for Jarred Fairies whose time is up as a player opens it. {@code initMenu}
 * runs once per opened menu, before its contents are sent to the player.
 */
@Mixin(ServerPlayer.class)
abstract class ServerPlayerMixin {

  @Inject(method = "initMenu", at = @At("HEAD"))
  private void fertilegrounds$checkJarredFairies(
      final AbstractContainerMenu menu, final CallbackInfo ci) {
    final ServerPlayer player = (ServerPlayer) (Object) this;
    // The player's own inventory menu is set up while the player is still being created.
    if (menu != player.inventoryMenu) {
      JarredFairies.checkOpenedMenu(player, menu);
    }
  }
}
