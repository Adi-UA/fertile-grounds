package com.fertilegrounds.hotel;

import com.fertilegrounds.item.ModDataComponents;
import com.fertilegrounds.item.ModItems;
import com.fertilegrounds.util.ModGameRules;
import com.fertilegrounds.util.ModTags;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Turns Jarred Fairies whose time is up into flowers, wherever they are: carried, in a container a
 * player opens, or lying on the ground. The rules live in {@link JarredFairyRules}.
 */
public final class JarredFairies {

  private JarredFairies() {}

  /**
   * Replaces a carried Jarred Fairy whose time is up with a flower, and tells the player.
   *
   * @param slot the stack's index in the player's inventory
   */
  public static void checkCarried(
      final ServerPlayer player, final ItemStack stack, final int slot) {
    final ServerLevel level = player.serverLevel();
    if (!hasSlippedAway(stack, level)) {
      return;
    }
    final ItemStack flower = leftFlower(level);
    player.getInventory().setItem(slot, flower);
    player.displayClientMessage(
        Component.translatable("message.fertilegrounds.fairy_slipped_away", flower.getHoverName()),
        true);
    level.playSound(
        null, player.blockPosition(), SoundEvents.ALLAY_THROW, SoundSource.PLAYERS, 0.4F, 1.0F);
    level.sendParticles(
        ParticleTypes.CHERRY_LEAVES,
        player.getX(),
        player.getY() + 1,
        player.getZ(),
        8,
        0.5,
        0.5,
        0.5,
        0.0);
  }

  /**
   * Replaces every Jarred Fairy whose time is up in a container the player just opened, before its
   * contents reach the player, and tells them once however many left.
   */
  public static void checkOpenedMenu(final ServerPlayer player, final AbstractContainerMenu menu) {
    final ServerLevel level = player.serverLevel();
    final Container ownInventory = player.getInventory();
    ItemStack firstFlower = ItemStack.EMPTY;
    for (final Slot slot : menu.slots) {
      // The player's own slots convert as they carry them, with the "carried" message.
      if (slot.container == ownInventory || !hasSlippedAway(slot.getItem(), level)) {
        continue;
      }
      final ItemStack flower = leftFlower(level);
      slot.set(flower);
      if (firstFlower.isEmpty()) {
        firstFlower = flower;
      }
    }
    if (!firstFlower.isEmpty()) {
      player.displayClientMessage(
          Component.translatable(
              "message.fertilegrounds.fairy_slipped_away_while_gone", firstFlower.getHoverName()),
          true);
    }
  }

  /** Replaces a dropped Jarred Fairy whose time is up with a flower; nobody is told. */
  public static void checkDropped(final ItemEntity item, final ServerLevel level) {
    if (hasSlippedAway(item.getItem(), level)) {
      item.setItem(leftFlower(level));
    }
  }

  /**
   * Whether {@code stack} is a Jarred Fairy whose time is up. One with no time yet, from the
   * creative tab or {@code /give}, starts its days now.
   */
  private static boolean hasSlippedAway(final ItemStack stack, final ServerLevel level) {
    if (!stack.is(ModItems.JARRED_FAIRY)) {
      return false;
    }
    final long now = level.getDayTime();
    final Long slipsAwayAt = stack.get(ModDataComponents.SLIPS_AWAY_AT);
    if (slipsAwayAt == null) {
      stack.set(ModDataComponents.SLIPS_AWAY_AT, slipsAwayAt(level));
      return false;
    }
    return JarredFairyRules.hasSlippedAway(slipsAwayAt, now);
  }

  /** When a fairy caught now slips away, on the overworld clock. */
  public static long slipsAwayAt(final ServerLevel level) {
    return JarredFairyRules.expiresAt(
        level.getDayTime(), level.getGameRules().getInt(ModGameRules.JARRED_FAIRY_DAYS));
  }

  private static ItemStack leftFlower(final ServerLevel level) {
    final List<Item> flowers = new ArrayList<>();
    for (final Holder<Item> flower :
        BuiltInRegistries.ITEM.getTagOrEmpty(ModTags.FAIRY_FLOWER_ITEMS)) {
      flowers.add(flower.value());
    }
    return new ItemStack(
        JarredFairyRules.leftFlower(flowers, Items.POPPY, level.getRandom()::nextInt));
  }
}
