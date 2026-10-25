package com.fertilegrounds.item;

import com.fertilegrounds.block.ModBlocks;
import com.fertilegrounds.entity.ModEntities;
import com.fertilegrounds.util.ModIdsUtil;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;

/** Registers the items this mod adds, including the items for its placeable blocks. */
public final class ModItems {

  public static final Item ENRICHED_DIRT = registerBlock(ModBlocks.ENRICHED_DIRT);

  public static final Item ENRICHED_SAND = registerBlock(ModBlocks.ENRICHED_SAND);

  public static final Item SUPER_ENRICHED_DIRT = registerBlock(ModBlocks.SUPER_ENRICHED_DIRT);

  public static final Item EMPTY_FAIRY_HOTEL = registerBlock(ModBlocks.EMPTY_FAIRY_HOTEL);

  public static final Item FAIRY_HOTEL = registerBlock(ModBlocks.FAIRY_HOTEL);

  public static final Item CHIME_FAIRY_HOTEL = registerBlock(ModBlocks.CHIME_FAIRY_HOTEL);

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
   * Loads this class so the static fields above register their items, then teaches dispensers to
   * use Fertilizer.
   */
  public static void register() {
    DispenserBlock.registerBehavior(FERTILIZER, new FertilizerDispenseBehavior());
  }

  /**
   * Like vanilla's {@code Items.registerBlock}. The item takes the block's translation key, since
   * the lang file names only {@code block.fertilegrounds.*} keys for blocks.
   */
  private static Item registerBlock(final Block block) {
    final ResourceKey<Item> key =
        ResourceKey.create(Registries.ITEM, BuiltInRegistries.BLOCK.getKey(block));
    final BlockItem item =
        new BlockItem(block, new Item.Properties().setId(key).useBlockDescriptionPrefix());
    item.registerBlocks(Item.BY_BLOCK, item);
    return Registry.register(BuiltInRegistries.ITEM, key, item);
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
