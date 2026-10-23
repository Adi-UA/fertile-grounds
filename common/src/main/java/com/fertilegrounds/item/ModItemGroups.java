package com.fertilegrounds.item;

import com.fertilegrounds.block.ModBlocks;
import com.fertilegrounds.util.ModIdsUtil;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

/**
 * The mod's own creative-inventory tab: its id, name, icon, and items. Each loader builds and
 * registers the tab with its own builder, since vanilla's builder needs a fixed row and column and
 * its item list type is only opened up to mods by the loaders.
 */
public final class ModItemGroups {

  public static final ResourceKey<CreativeModeTab> MAIN =
      ResourceKey.create(Registries.CREATIVE_MODE_TAB, ModIdsUtil.id("main"));

  public static final Component TITLE = Component.translatable("itemGroup.fertilegrounds.main");

  private ModItemGroups() {}

  public static ItemStack icon() {
    return new ItemStack(ModBlocks.SUPER_ENRICHED_DIRT);
  }

  /** The tab's items, in order: the mod's craftable items. */
  public static List<ItemLike> contents() {
    return List.of(
        ModItems.ENRICHED_DIRT,
        ModItems.ENRICHED_SAND,
        ModItems.SUPER_ENRICHED_DIRT,
        ModItems.FERTILIZER,
        ModItems.FAIRY_DUST,
        ModItems.BEAN_FAIRY_SPAWN_EGG,
        ModItems.EMPTY_FAIRY_HOTEL,
        ModItems.FAIRY_HOTEL,
        ModItems.CHIME_FAIRY_HOTEL,
        ModItems.JARRED_FAIRY);
  }
}
