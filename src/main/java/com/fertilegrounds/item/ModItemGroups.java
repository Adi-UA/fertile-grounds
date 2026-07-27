package com.fertilegrounds.item;

import com.fertilegrounds.block.ModBlocks;
import com.fertilegrounds.util.ModIdsUtil;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

/** Registers the mod's own creative-inventory tab and the items shown in it. */
public final class ModItemGroups {

  private static final ResourceKey<CreativeModeTab> MAIN =
      ResourceKey.create(Registries.CREATIVE_MODE_TAB, ModIdsUtil.id("main"));

  private ModItemGroups() {}

  /** Registers the "Fertile Grounds" creative tab, populated with the mod's craftable items. */
  public static void register() {
    Registry.register(
        BuiltInRegistries.CREATIVE_MODE_TAB,
        MAIN,
        FabricItemGroup.builder()
            .title(Component.translatable("itemGroup.fertilegrounds.main"))
            .icon(() -> new ItemStack(ModBlocks.SUPER_ENRICHED_DIRT))
            .displayItems(
                (parameters, output) -> {
                  output.accept(ModBlocks.ENRICHED_DIRT);
                  output.accept(ModBlocks.ENRICHED_SAND);
                  output.accept(ModBlocks.SUPER_ENRICHED_DIRT);
                })
            .build());
  }
}
