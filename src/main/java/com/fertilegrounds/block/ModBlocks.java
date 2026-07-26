package com.fertilegrounds.block;

import com.fertilegrounds.FertileGrounds;
import com.fertilegrounds.mixin.HoeItemAccessor;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

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

    public static final Block ENRICHED_DIRT = registerBlockWithItem("enriched_dirt", new Block(dirtProperties()));

    public static final Block ENRICHED_FARMLAND = registerBlockOnly(
            "enriched_farmland",
            new BoostedFarmlandBlock(farmlandProperties(), ENRICHED_BOOST_CHANCE, ENRICHED_DIRT)
    );

    public static final Block SUPER_ENRICHED_DIRT = registerBlockWithItem("super_enriched_dirt", new Block(dirtProperties()));

    public static final Block SUPER_ENRICHED_FARMLAND = registerBlockOnly(
            "super_enriched_farmland",
            new BoostedFarmlandBlock(farmlandProperties(), SUPER_ENRICHED_BOOST_CHANCE, SUPER_ENRICHED_DIRT)
    );

    public static final Block ENRICHED_SAND = registerBlockWithItem(
            "enriched_sand",
            new GrowthBoostedSandBlock(sandProperties(), ENRICHED_BOOST_CHANCE)
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
        return Registry.register(BuiltInRegistries.BLOCK, FertileGrounds.id(path), block);
    }

    private static Block registerBlockWithItem(final String path, final Block block) {
        final Block registered = registerBlockOnly(path, block);
        Registry.register(BuiltInRegistries.ITEM, FertileGrounds.id(path), new BlockItem(registered, new Item.Properties()));
        return registered;
    }

    private static BlockBehaviour.Properties dirtProperties() {
        // Matches vanilla Blocks.DIRT's own properties.
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.DIRT)
                .strength(0.5F)
                .sound(SoundType.GRAVEL);
    }

    private static BlockBehaviour.Properties farmlandProperties() {
        // Matches vanilla Blocks.FARMLAND's own properties.
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.DIRT)
                .randomTicks()
                .strength(0.6F)
                .sound(SoundType.GRAVEL)
                .isViewBlocking(Blocks::always)
                .isSuffocating(Blocks::always);
    }

    private static BlockBehaviour.Properties sandProperties() {
        // Matches vanilla Blocks.SAND's own properties, plus randomTicks()
        // (vanilla sand has no passive tick behavior; ours needs one).
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.SAND)
                .randomTicks()
                .strength(0.5F)
                .sound(SoundType.SAND);
    }
}
