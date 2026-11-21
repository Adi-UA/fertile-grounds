package com.fertilegrounds.mixin;

import com.mojang.brigadier.arguments.ArgumentType;
import java.util.function.Supplier;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Replaces a gamerule's command argument, so an integer rule takes only values in its range, the
 * way later Minecraft versions build ranged rules.
 */
@Mixin(GameRules.Type.class)
public interface GameRuleTypeAccessor {

  @Mutable
  @Accessor("argument")
  void fertilegrounds$setArgument(Supplier<ArgumentType<?>> argument);
}
