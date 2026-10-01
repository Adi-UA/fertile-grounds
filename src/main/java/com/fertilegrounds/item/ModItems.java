package com.fertilegrounds.item;

import com.fertilegrounds.entity.ModEntities;
import com.fertilegrounds.util.ModIdsUtil;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

/** Registers the items this mod adds that aren't blocks (block items live in ModBlocks). */
public final class ModItems {

  public static final Item FAIRY_DUST =
      register("fairy_dust", FairyDustItem::new, new Item.Properties());

  /**
   * Spawn eggs take two tint colors here. White leaves the painted texture's own colors unchanged.
   */
  public static final Item BEAN_FAIRY_SPAWN_EGG =
      register(
          "bean_fairy_spawn_egg",
          properties -> new SpawnEggItem(ModEntities.BEAN_FAIRY, 0xFFFFFF, 0xFFFFFF, properties),
          new Item.Properties());

  private ModItems() {}

  /** Loads this class so the static fields above register their items during mod startup. */
  public static void register() {}

  private static Item register(
      final String path,
      final Function<Item.Properties, Item> factory,
      final Item.Properties properties) {
    return Registry.register(
        BuiltInRegistries.ITEM, ModIdsUtil.id(path), factory.apply(properties));
  }
}
