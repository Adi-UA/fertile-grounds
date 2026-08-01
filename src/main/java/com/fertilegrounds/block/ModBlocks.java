package com.fertilegrounds.block;

import com.fertilegrounds.FertileGrounds;
import com.fertilegrounds.util.ModIdsUtil;
import java.util.function.Function;
import net.fabricmc.fabric.api.registry.TillableBlockRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * Registers all blocks this mod adds. Registration order matters here: each "farmland" tier's
 * dried-out target and each "dirt" tier's till target reference an already-declared sibling field,
 * which only works because static fields initialize top-to-bottom in declaration order.
 */
public final class ModBlocks {

  /** Tier 1 (Enriched Dirt/Sand): a modest boost from cheap ingredients. */
  private static final float ENRICHED_BOOST_CHANCE = 0.15F;

  /** Tier 3 (Super Enriched Dirt): ~3.3x tier 1, gated behind Glowstone Dust. */
  private static final float SUPER_ENRICHED_BOOST_CHANCE = 0.5F;

  // ---- Tier 1: Enriched — cheap ingredients, modest boost ----

  public static final Block ENRICHED_DIRT =
      registerBlockWithItem("enriched_dirt", Block::new, ModBlockProperties.dirtProperties());

  public static final Block ENRICHED_FARMLAND =
      registerBlockOnly(
          "enriched_farmland",
          properties ->
              new GrowthBoostedFarmlandBlock(properties, ENRICHED_BOOST_CHANCE, ENRICHED_DIRT),
          ModBlockProperties.farmlandProperties());

  // ---- Tier 2: Enriched Sand — boosts existing sand-grown plants, no tilling ----

  public static final Block ENRICHED_SAND =
      registerBlockWithItem(
          "enriched_sand",
          properties -> new GrowthBoostedSandBlock(properties, ENRICHED_BOOST_CHANCE),
          ModBlockProperties.sandProperties());

  // ---- Tier 3: Super Enriched — Glowstone Dust gated, ~3.3x boost ----

  public static final Block SUPER_ENRICHED_DIRT =
      registerBlockWithItem("super_enriched_dirt", Block::new, ModBlockProperties.dirtProperties());

  public static final Block SUPER_ENRICHED_FARMLAND =
      registerBlockOnly(
          "super_enriched_farmland",
          properties ->
              new GrowthBoostedFarmlandBlock(
                  properties, SUPER_ENRICHED_BOOST_CHANCE, SUPER_ENRICHED_DIRT),
          ModBlockProperties.farmlandProperties());

  private ModBlocks() {}

  /**
   * Wires up hoe-tilling for the two dirt tiers, via Fabric API's {@link TillableBlockRegistry}
   * rather than a mixin — this Minecraft version already exposes tilling registration as a public
   * API instead of {@code HoeItem}'s old package-private map.
   */
  public static void register() {
    registerTillable(ENRICHED_DIRT, ENRICHED_FARMLAND);
    registerTillable(SUPER_ENRICHED_DIRT, SUPER_ENRICHED_FARMLAND);
    FertileGrounds.LOGGER.info(
        "Fertile Grounds: registered 5 blocks (2 tillable pairs + enriched sand)");
  }

  private static void registerTillable(final Block untilled, final Block tilled) {
    TillableBlockRegistry.register(untilled, HoeItem::onlyIfAirAbove, tilled.defaultBlockState());
  }

  /**
   * Registers a block under this mod's namespace. This Minecraft version requires a block's
   * registry key to be known by the block itself before construction — {@code Block}'s constructor
   * derives its default loot table/translation key from {@code properties}, the same way vanilla's
   * own {@code Blocks.java} calls {@code properties.setId(key)} before building each block — so
   * this takes a factory function instead of an already-built {@code Block}.
   */
  private static <T extends Block> T registerBlockOnly(
      final String path,
      final Function<BlockBehaviour.Properties, T> factory,
      final BlockBehaviour.Properties properties) {
    final ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, ModIdsUtil.id(path));
    final T block = factory.apply(properties.setId(key));
    return Registry.register(BuiltInRegistries.BLOCK, key, block);
  }

  /**
   * Same as {@link #registerBlockOnly}, plus a {@link BlockItem} so it's obtainable/stackable.
   * {@code Item}'s constructor has the same id-before-construction requirement as {@code Block}'s,
   * and {@code Item.BY_BLOCK} (the map {@code Block.asItem()} reads from) has to be populated
   * explicitly too — vanilla's {@code Items.java} does both as part of its own registration helper.
   */
  private static <T extends Block> T registerBlockWithItem(
      final String path,
      final Function<BlockBehaviour.Properties, T> factory,
      final BlockBehaviour.Properties properties) {
    final T registered = registerBlockOnly(path, factory, properties);
    final ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, ModIdsUtil.id(path));
    final BlockItem item = new BlockItem(registered, new Item.Properties().setId(itemKey));
    item.registerBlocks(Item.BY_BLOCK, item);
    Registry.register(BuiltInRegistries.ITEM, itemKey, item);
    return registered;
  }
}
