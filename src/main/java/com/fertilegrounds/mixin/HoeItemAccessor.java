package com.fertilegrounds.mixin;

import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * HoeItem has no public API for registering new tillable blocks in 1.20.1 — its
 * block-to-till-result map is a {@code protected static} field. This accessor exposes it so {@code
 * ModBlocks} can add "our dirt -> our farmland" entries, reusing HoeItem's own public helper
 * methods ({@code onlyIfAirAbove}, {@code changeIntoState}) for the actual predicate and till
 * behavior, rather than reimplementing tilling from scratch.
 */
@Mixin(HoeItem.class)
public interface HoeItemAccessor {

  @Accessor("TILLABLES")
  static Map<Block, Pair<Predicate<UseOnContext>, Consumer<UseOnContext>>>
      fertilegrounds$getTillables() {
    throw new AssertionError("Mixin not applied: HoeItemAccessor");
  }
}
