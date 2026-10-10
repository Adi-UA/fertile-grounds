package com.fertilegrounds.item;

import com.fertilegrounds.util.ModGameRules;
import com.fertilegrounds.util.ModIdsUtil;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Gives each player the guide on their first join to a world, including players from before the
 * guide existed. A flag saved on the player (a Fabric data attachment, kept through death) makes
 * sure it happens once.
 */
public final class GuideBookGift {

  public static final AttachmentType<Boolean> RECEIVED_GUIDE =
      AttachmentRegistry.create(
          ModIdsUtil.id("received_guide"), builder -> builder.persistent(Codec.BOOL).copyOnDeath());

  private GuideBookGift() {}

  public static void register() {
    ServerPlayConnectionEvents.JOIN.register(
        (handler, sender, server) -> giveOnFirstJoin(handler.player));
  }

  /** Gives the guide unless this player already got one or the gamerule is off. */
  public static void giveOnFirstJoin(final ServerPlayer player) {
    if (player.hasAttached(RECEIVED_GUIDE)
        || !player.level().getGameRules().get(ModGameRules.GIVE_GUIDE_BOOK)) {
      return;
    }
    player.setAttached(RECEIVED_GUIDE, true);
    final ItemStack book = GuideBook.create();
    // A full inventory drops the book at the player's feet, the way /give does.
    if (!player.getInventory().add(book)) {
      final ItemEntity drop = player.drop(book, false);
      if (drop != null) {
        drop.setNoPickUpDelay();
        drop.setTarget(player.getUUID());
      }
    }
    player
        .level()
        .playSound(
            null,
            player.blockPosition(),
            SoundEvents.BOOK_PAGE_TURN,
            SoundSource.PLAYERS,
            1.0F,
            1.0F);
  }
}
