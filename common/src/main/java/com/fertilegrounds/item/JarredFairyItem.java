package com.fertilegrounds.item;

import com.fertilegrounds.hotel.JarredFairies;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * A Bean Fairy caught in a bottle. It does nothing on its own: it waits to move into a Fairy Hotel,
 * and slips away after {@code jarred_fairy_days} if it doesn't.
 */
public class JarredFairyItem extends Item {

  public JarredFairyItem(final Properties properties) {
    super(properties);
  }

  /** A freshly caught fairy, due to slip away {@code jarred_fairy_days} from now. */
  public static ItemStack caughtNow(final ServerLevel level) {
    final ItemStack jar = new ItemStack(ModItems.JARRED_FAIRY);
    jar.set(ModDataComponents.SLIPS_AWAY_AT, JarredFairies.slipsAwayAt(level));
    return jar;
  }

  /** Checks a carried fairy each tick, so it slips away on time in the player's inventory. */
  @Override
  public void inventoryTick(
      final ItemStack stack,
      final Level level,
      final Entity owner,
      final int slot,
      final boolean selected) {
    if (level.isClientSide() || !(owner instanceof ServerPlayer player)) {
      return;
    }
    // Find this exact stack, offhand and armor slots included, to swap it for a flower.
    final Inventory inventory = player.getInventory();
    for (int index = 0; index < inventory.getContainerSize(); index++) {
      if (inventory.getItem(index) == stack) {
        JarredFairies.checkCarried(player, stack, index);
        return;
      }
    }
  }
}
