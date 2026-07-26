package com.fertilegrounds.mixin;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Vanilla's CropBlock hardcodes two checks against the exact Blocks.FARMLAND
 * singleton: whether a crop may be placed at all ({@code mayPlaceOn}), and how
 * fast it grows based on the farmland underneath ({@code getGrowthSpeed}).
 * Both are widened here to also accept any {@link FarmBlock} subclass, which
 * is what makes this mod's farmland tiers work as real farmland instead of
 * just visually resembling it.
 *
 * <p>{@code getGrowthSpeed} calls {@code BlockState.is(Block)} five times in
 * total: once for the farmland/moisture check we care about, and four more
 * for an unrelated same-crop-spacing check. {@code ordinal = 0} targets only
 * the first (farmland) call site.
 */
@Mixin(CropBlock.class)
public class CropBlockMixin {

    @Redirect(
            method = "mayPlaceOn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"
            )
    )
    private static boolean fertilegrounds$allowPlacementOnAnyFarmland(final BlockState state, final Block block) {
        return state.is(block) || state.getBlock() instanceof FarmBlock;
    }

    @Redirect(
            method = "getGrowthSpeed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z",
                    ordinal = 0
            )
    )
    private static boolean fertilegrounds$treatAnyFarmlandAsHydrationSource(final BlockState state, final Block block) {
        return state.is(block) || state.getBlock() instanceof FarmBlock;
    }
}
