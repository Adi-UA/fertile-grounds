package com.fertilegrounds.item;

import net.minecraft.world.item.Item;

/**
 * A Bean Fairy caught in a bottle. It does nothing on its own: it waits to move into a Fairy Hotel,
 * and slips away after {@code jarred_fairy_days} if it doesn't.
 */
public class JarredFairyItem extends Item {

  public JarredFairyItem(final Properties properties) {
    super(properties);
  }
}
