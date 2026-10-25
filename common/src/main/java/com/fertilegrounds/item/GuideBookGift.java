package com.fertilegrounds.item;

import com.fertilegrounds.util.ModGameRules;
import com.fertilegrounds.util.ModSavedData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Gives each player the guide once, on their first join to a world, tracked by a flag saved on the
 * player ({@link ModSavedData#RECEIVED_GUIDE}).
 */
public final class GuideBookGift {

  private GuideBookGift() {}

  /** Gives the guide unless this player already got one or the gamerule is off. */
  public static void giveOnFirstJoin(final ServerPlayer player) {
    if (ModSavedData.RECEIVED_GUIDE.isSet(player)
        || !player.level().getGameRules().get(ModGameRules.GIVE_GUIDE_BOOK)) {
      return;
    }
    ModSavedData.RECEIVED_GUIDE.set(player);
    final ItemStack book = GuideBook.create();
    // A full inventory drops the book at the player's feet, the way /give does.
    if (!player.getInventory().add(book)) {
      final ItemEntity drop = player.createItemStackToDrop(book, false, false);
      if (drop != null) {
        drop.setNoPickUpDelay();
        drop.setTarget(player.getUUID());
        player.level().addFreshEntity(drop);
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
