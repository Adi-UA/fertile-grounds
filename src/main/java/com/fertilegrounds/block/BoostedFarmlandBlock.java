package com.fertilegrounds.block;

import com.fertilegrounds.FertileGrounds;
import com.fertilegrounds.util.GrowthBooster;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

/**
 * A farmland variant that passively fertilizes whatever crop is planted on
 * top of it, on top of vanilla farmland's normal moisture/hydration behavior.
 *
 * <p>Two vanilla {@link FarmBlock} behaviors are deliberately not inherited
 * as-is, since both would destroy a crafted block over a passive world event
 * with no player mistake involved:
 * <ul>
 *   <li>Trampling (a player/mob jumping on it) normally has a chance to turn
 *   farmland back to dirt. That's free to undo for vanilla farmland (just
 *   re-till), but this block cost a crafting recipe, so trampling is a no-op
 *   here beyond the normal fall damage.
 *   <li>Drying out completely (no water nearby, nothing maintaining it) also
 *   reverts farmland to dirt in vanilla, but vanilla hardcodes the target as
 *   {@code Blocks.DIRT} specifically. Left unchanged, that would silently
 *   downgrade this block into plain vanilla dirt. Moisture tracking itself is
 *   kept (it still affects growth speed via vanilla's {@code CropBlock}), but
 *   the "ran dry" case reverts to this tier's own dirt block instead.
 * </ul>
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
        // Intentionally skips FarmBlock's trample-to-dirt roll; this replicates
        // only Block's default fall-damage behavior, not FarmBlock's override.
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
            revertToDriedOutBlock(state, world, pos);
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

    private void revertToDriedOutBlock(final BlockState state, final ServerLevel world, final BlockPos pos) {
        final BlockState newState = Block.pushEntitiesUp(state, this.driedOutBlock.defaultBlockState(), world, pos);
        world.setBlockAndUpdate(pos, newState);
        world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(newState));
        FertileGrounds.LOGGER.debug("{} dried out at {}, reverted to {}", state.getBlock(), pos, this.driedOutBlock);
    }
}
