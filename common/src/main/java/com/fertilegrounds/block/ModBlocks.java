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
 * Registers the mod's blocks; their items live in {@link com.fertilegrounds.item.ModItems}. Each
 * farmland names its dirt field, so the dirt must come first in declaration order.
 */
public final class ModBlocks {

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

  public static final Block ENRICHED_SAND =
      register(
          "enriched_sand",
          properties ->
              new GrowthBoostedSandBlock(
                  properties, ModGameRules.ENRICHED_BOOST_CHANCE, SoilTier.ENRICHED),
          ModBlockProperties.sandProperties());

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
   * Registers a block under the mod's namespace. It takes a factory because a block's constructor
   * reads its registry key from {@code properties}, as in vanilla's {@code Blocks}.
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
