package com.fertilegrounds.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * The mod's item data. This Minecraft version has no item components, so each value lives in the
 * stack's NBT tag under its component id, which saves with the item and syncs to clients.
 */
public final class ModDataComponents {

  /** The game time a Jarred Fairy slips away. */
  public static final String SLIPS_AWAY_AT = "fertilegrounds:slips_away_at";

  private ModDataComponents() {}

  /** The long saved under {@code key}, or null if the stack has none. */
  public static @Nullable Long getLong(final ItemStack stack, final String key) {
    final CompoundTag tag = stack.getTag();
    return tag != null && tag.contains(key, Tag.TAG_LONG) ? tag.getLong(key) : null;
  }

  /** Saves {@code value} under {@code key}. */
  public static void setLong(final ItemStack stack, final String key, final long value) {
    stack.getOrCreateTag().putLong(key, value);
  }
}
