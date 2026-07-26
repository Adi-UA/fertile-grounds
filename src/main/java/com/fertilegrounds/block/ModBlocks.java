package com.fertilegrounds.block;

import com.fertilegrounds.FertileGrounds;
import com.fertilegrounds.mixin.HoeItemAccessor;
import com.fertilegrounds.util.ModIdsUtil;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * Registers all blocks this mod adds. Registration order matters here: each
 * "farmland" tier's dried-out target and each "dirt" tier's till target
 * reference an already-declared sibling field, which only works because
 * static fields initialize top-to-bottom in declaration order.
 */
public final class ModBlocks {

    /** Tier 1 (Enriched Dirt/Sand): a modest boost from cheap ingredients. */
    private static final float ENRICHED_BOOST_CHANCE = 0.15F;
    /** Tier 3 (Super Enriched Dirt): ~3.3x tier 1, gated behind Glowstone Dust. */
    private static final float SUPER_ENRICHED_BOOST_CHANCE = 0.5F;

    // ---- Tier 1: Enriched — cheap ingredients, modest boost ----

    public static final Block ENRICHED_DIRT = registerBlockWithItem("enriched_dirt", new Block(ModBlockProperties.dirtProperties()));

    public static final Block ENRICHED_FARMLAND = registerBlockOnly(
            "enriched_farmland",
            new BoostedFarmlandBlock(ModBlockProperties.farmlandProperties(), ENRICHED_BOOST_CHANCE, ENRICHED_DIRT)
    );

    // ---- Tier 2: Enriched Sand — boosts existing sand-grown plants, no tilling ----

    public static final Block ENRICHED_SAND = registerBlockWithItem(
            "enriched_sand",
            new GrowthBoostedSandBlock(ModBlockProperties.sandProperties(), ENRICHED_BOOST_CHANCE)
    );

    // ---- Tier 3: Super Enriched — Glowstone Dust gated, ~3.3x boost ----

    public static final Block SUPER_ENRICHED_DIRT = registerBlockWithItem("super_enriched_dirt", new Block(ModBlockProperties.dirtProperties()));

    public static final Block SUPER_ENRICHED_FARMLAND = registerBlockOnly(
            "super_enriched_farmland",
            new BoostedFarmlandBlock(ModBlockProperties.farmlandProperties(), SUPER_ENRICHED_BOOST_CHANCE, SUPER_ENRICHED_DIRT)
    );

    private ModBlocks() {
    }

    /**
     * Wires up hoe-tilling for the two dirt tiers. Must run explicitly from
     * {@link FertileGrounds#onInitialize()} rather than a static initializer
     * here, since it depends on Mixins having already been applied to
     * {@code HoeItem} by the time it runs — true for mod init, not guaranteed
     * for class-loading order otherwise.
     */
    public static void register() {
        registerTillable(ENRICHED_DIRT, ENRICHED_FARMLAND);
        registerTillable(SUPER_ENRICHED_DIRT, SUPER_ENRICHED_FARMLAND);
        FertileGrounds.LOGGER.info("Fertile Grounds: registered 5 blocks (2 tillable pairs + enriched sand)");
    }

    private static void registerTillable(final Block untilled, final Block tilled) {
        HoeItemAccessor.fertilegrounds$getTillables().put(
                untilled,
                Pair.of(HoeItem::onlyIfAirAbove, HoeItem.changeIntoState(tilled.defaultBlockState()))
        );
    }

    private static Block registerBlockOnly(final String path, final Block block) {
        return Registry.register(BuiltInRegistries.BLOCK, ModIdsUtil.id(path), block);
    }

    private static Block registerBlockWithItem(final String path, final Block block) {
        final Block registered = registerBlockOnly(path, block);
        Registry.register(BuiltInRegistries.ITEM, ModIdsUtil.id(path), new BlockItem(registered, new Item.Properties()));
        return registered;
    }
}
