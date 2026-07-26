package com.fertilegrounds.block;

import com.fertilegrounds.util.GrowthBoostUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A sand variant that passively fertilizes whatever grows directly on top of
 * it (cactus, sugar cane, melon/pumpkin stems), without introducing any new
 * plantable surface — unlike the farmland tiers, ordinary crops still can't
 * be planted here; this only speeds up things that already grow on sand.
 * Falls under gravity exactly like vanilla sand ({@link FallingBlock}).
 */
public class GrowthBoostedSandBlock extends FallingBlock {

    private final float growthBoostChance;

    /**
     * @param properties block properties, e.g. from {@code ModBlockProperties.sandProperties()}
     * @param growthBoostChance per-random-tick probability (0.0-1.0) of fertilizing the plant above
     */
    public GrowthBoostedSandBlock(final Properties properties, final float growthBoostChance) {
        super(properties);
        this.growthBoostChance = growthBoostChance;
    }

    @Override
    public void randomTick(final BlockState state, final ServerLevel world, final BlockPos pos, final RandomSource random) {
        super.randomTick(state, world, pos, random);
        GrowthBoostUtil.tryBoostAbove(world, pos, random, this.growthBoostChance);
    }
}
