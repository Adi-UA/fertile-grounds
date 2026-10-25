package com.fertilegrounds.block;

import com.fertilegrounds.FertileGrounds;
import com.fertilegrounds.util.ModGameRules;
import com.fertilegrounds.util.ModIdsUtil;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * Registers all blocks this mod adds; their items live in {@link com.fertilegrounds.item.ModItems},
 * the way vanilla splits {@code Blocks} and {@code Items}. Registration order matters here: each
 * "farmland" tier's dried-out target references an already-declared sibling field, which only works
 * because static fields initialize top-to-bottom in declaration order.
 */
public final class ModBlocks {

  // ---- Tier 1: Enriched — cheap ingredients, modest boost ----

  public static final Block ENRICHED_DIRT =
      register(
          "enriched_dirt",
          properties -> new EnrichedDirtBlock(properties, SoilTier.ENRICHED),
          ModBlockProperties.dirtProperties());

  public static final Block ENRICHED_FARMLAND =
      register(
          "enriched_farmland",
          properties ->
              new GrowthBoostedFarmlandBlock(
                  properties, ModGameRules.ENRICHED_BOOST_CHANCE, ENRICHED_DIRT, SoilTier.ENRICHED),
          ModBlockProperties.farmlandProperties());

  // ---- Tier 2: Enriched Sand — boosts existing sand-grown plants, no tilling ----

  public static final Block ENRICHED_SAND =
      register(
          "enriched_sand",
          properties ->
              new GrowthBoostedSandBlock(
                  properties, ModGameRules.ENRICHED_BOOST_CHANCE, SoilTier.ENRICHED),
          ModBlockProperties.sandProperties());

  // ---- Tier 3: Super Enriched — Glowstone Dust gated, ~3.3x boost ----

  public static final Block SUPER_ENRICHED_DIRT =
      register(
          "super_enriched_dirt",
          properties -> new EnrichedDirtBlock(properties, SoilTier.SUPER_ENRICHED),
          ModBlockProperties.dirtProperties());

  public static final Block SUPER_ENRICHED_FARMLAND =
      register(
          "super_enriched_farmland",
          properties ->
              new GrowthBoostedFarmlandBlock(
                  properties,
                  ModGameRules.SUPER_ENRICHED_BOOST_CHANCE,
                  SUPER_ENRICHED_DIRT,
                  SoilTier.SUPER_ENRICHED),
          ModBlockProperties.farmlandProperties());

  // ---- Fairy Hotels: lanterns a caught fairy can live in ----

  public static final Block EMPTY_FAIRY_HOTEL =
      register(
          "empty_fairy_hotel",
          properties -> new FairyHotelBlock(properties, false),
          ModBlockProperties.fairyHotelProperties(5));

  public static final Block FAIRY_HOTEL =
      register(
          "fairy_hotel",
          properties -> new FairyHotelBlock(properties, true),
          ModBlockProperties.fairyHotelProperties(12).randomTicks());

  public static final Block CHIME_FAIRY_HOTEL =
      register(
          "chime_fairy_hotel",
          ChimeHotelBlock::new,
          ModBlockProperties.fairyHotelProperties(12).randomTicks());

  /**
   * Each dirt tier and the farmland a hoe turns it into. Each loader wires these into hoe tilling
   * with its own API, and like vanilla dirt they only till when the block above is air.
   */
  public static final Map<Block, Block> TILLED =
      Map.of(ENRICHED_DIRT, ENRICHED_FARMLAND, SUPER_ENRICHED_DIRT, SUPER_ENRICHED_FARMLAND);

  private ModBlocks() {}

  /** Loads this class so the static fields above register their blocks. */
  public static void register() {
    FertileGrounds.LOGGER.info(
        "Fertile Grounds: registered 8 blocks (2 tillable pairs, enriched sand, 3 fairy hotels)");
  }

  /**
   * Registers a block under this mod's namespace. This Minecraft version requires a block's
   * registry key to be known by the block itself before construction ({@code Block}'s constructor
   * derives its default loot table and translation key from {@code properties}, the same way
   * vanilla's own {@code Blocks.java} calls {@code properties.setId(key)} before building each
   * block), so this takes a factory function instead of an already-built {@code Block}.
   */
  private static <T extends Block> T register(
      final String path,
      final Function<BlockBehaviour.Properties, T> factory,
      final BlockBehaviour.Properties properties) {
    final ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, ModIdsUtil.id(path));
    final T block = factory.apply(properties.setId(key));
    return Registry.register(BuiltInRegistries.BLOCK, key, block);
  }
}
