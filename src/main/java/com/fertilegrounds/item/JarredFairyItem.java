package com.fertilegrounds.item;

import com.fertilegrounds.hotel.JarredFairyRules;
import com.fertilegrounds.util.ModGameRules;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

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
    jar.set(
        ModDataComponents.SLIPS_AWAY_AT,
        JarredFairyRules.expiresAt(
            level.getGameTime(), level.getGameRules().get(ModGameRules.JARRED_FAIRY_DAYS)));
    return jar;
  }
}
