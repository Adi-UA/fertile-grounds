package com.fertilegrounds.item;

import com.fertilegrounds.entity.ModEntities;
import com.fertilegrounds.util.ModIdsUtil;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.DispenserBlock;

/** Registers the items this mod adds that aren't blocks (block items live in ModBlocks). */
public final class ModItems {

  public static final Item FAIRY_DUST =
      register("fairy_dust", FairyDustItem::new, new Item.Properties());

  public static final Item FERTILIZER =
      register("fertilizer", FertilizerItem::new, new Item.Properties());

  public static final Item BEAN_FAIRY_SPAWN_EGG =
      register(
          "bean_fairy_spawn_egg",
          SpawnEggItem::new,
          new Item.Properties().spawnEgg(ModEntities.BEAN_FAIRY));

  public static final Item JARRED_FAIRY =
      register("jarred_fairy", JarredFairyItem::new, new Item.Properties().stacksTo(1));

  private ModItems() {}

  /**
   * Loads this class so the static fields above register their items during mod startup, then
   * teaches dispensers to use Fertilizer.
   */
  public static void register() {
    DispenserBlock.registerBehavior(FERTILIZER, new FertilizerDispenseBehavior());
  }

  /** Like vanilla's {@code Items.registerItem}: the id has to be set before the item is built. */
  private static Item register(
      final String path,
      final Function<Item.Properties, Item> factory,
      final Item.Properties properties) {
    final ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, ModIdsUtil.id(path));
    return Registry.register(BuiltInRegistries.ITEM, key, factory.apply(properties.setId(key)));
  }
}
