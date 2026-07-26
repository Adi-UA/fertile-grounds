package com.fertilegrounds.block;

import com.fertilegrounds.FertileGrounds;
import com.fertilegrounds.util.GrowthBooster;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

/**
 * A farmland variant that passively fertilizes whatever crop is planted on
 * top of it, on top of vanilla farmland's normal moisture/hydration behavior.
 *
 * <p>Vanilla {@link FarmBlock} has two ways to revert to dirt on its own
 * (trampling and drying out), and both hardcode the target as
 * {@code Blocks.DIRT} specifically. Left unchanged, either would silently
 * downgrade a crafted block into plain vanilla dirt instead of this tier's
 * own dirt block. Both are reimplemented here to revert to
 * {@link #driedOutBlock} instead — same vanilla conditions and odds, just a
 * corrected target.
 *
 * <p>Crops won't actually accept this block as valid ground without
 * {@code CropBlockMixin} widening vanilla's hardcoded farmland identity
 * checks to also match {@code instanceof FarmBlock}.
 */
public class BoostedFarmlandBlock extends FarmBlock {

    private final float growthBoostChance;
    private final Block driedOutBlock;

    public BoostedFarmlandBlock(final Properties properties, final float growthBoostChance, final Block driedOutBlock) {
        super(properties);
        this.growthBoostChance = growthBoostChance;
        this.driedOutBlock = driedOutBlock;
    }

    @Override
    public void randomTick(final BlockState state, final ServerLevel world, final BlockPos pos, final RandomSource random) {
        tickMoisture(state, world, pos);
        GrowthBooster.tryBoostAbove(world, pos, random, this.growthBoostChance);
    }

    @Override
    public void fallOn(final Level level, final BlockState state, final BlockPos pos, final Entity entity, final float fallDistance) {
        // Same trample roll and conditions as FarmBlock.fallOn: only living
        // entities heavy/wide enough, only when mobGriefing allows it for
        // non-players, harder falls are more likely to trigger it. The only
        // change is the revert target.
        if (!level.isClientSide
                && level.getRandom().nextFloat() < fallDistance - 0.5F
                && entity instanceof LivingEntity
                && (entity instanceof Player || level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING))
                && entity.getBbWidth() * entity.getBbWidth() * entity.getBbHeight() > 0.512F) {
            revertToDriedOutBlock(state, level, pos, entity);
        }

        entity.causeFallDamage(fallDistance, 1.0F, entity.damageSources().fall());
    }

    /**
     * Reimplementation of {@code FarmBlock}'s moisture tick: vanilla's version
     * is unusable as a base to extend because its water/maintenance checks are
     * private, and its dry-out case is hardcoded to revert to {@code Blocks.DIRT}.
     */
    private void tickMoisture(final BlockState state, final ServerLevel world, final BlockPos pos) {
        final int moisture = state.getValue(MOISTURE);
        final boolean hydrated = isNearWater(world, pos) || world.isRainingAt(pos.above());

        if (hydrated) {
            if (moisture < MAX_MOISTURE) {
                world.setBlock(pos, state.setValue(MOISTURE, MAX_MOISTURE), 2);
            }
            return;
        }

        if (moisture > 0) {
            world.setBlock(pos, state.setValue(MOISTURE, moisture - 1), 2);
            return;
        }

        if (!world.getBlockState(pos.above()).is(BlockTags.MAINTAINS_FARMLAND)) {
            revertToDriedOutBlock(state, world, pos, null);
        }
    }

    private static boolean isNearWater(final ServerLevel world, final BlockPos pos) {
        for (final BlockPos candidate : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 1, 4))) {
            if (world.getFluidState(candidate).is(FluidTags.WATER)) {
                return true;
            }
        }
        return false;
    }

    private void revertToDriedOutBlock(final BlockState state, final Level world, final BlockPos pos, @Nullable final Entity cause) {
        final BlockState newState = Block.pushEntitiesUp(state, this.driedOutBlock.defaultBlockState(), world, pos);
        world.setBlockAndUpdate(pos, newState);
        world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(cause, newState));
        FertileGrounds.LOGGER.debug("{} reverted to {} at {} (cause: {})", state.getBlock(), this.driedOutBlock, pos, cause);
    }
}
