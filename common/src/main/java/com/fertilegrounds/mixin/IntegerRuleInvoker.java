package com.fertilegrounds.mixin;

import java.util.function.BiConsumer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Calls vanilla's private {@code IntegerValue.create}. Forge registers the mod's gamerules with it
 * and then gives each a range through {@link GameRuleTypeAccessor}, so the {@code /gamerule}
 * command rejects values outside it; Fabric API has its own ranged rules.
 */
@Mixin(GameRules.IntegerValue.class)
public interface IntegerRuleInvoker {

  @Invoker("create")
  static GameRules.Type<GameRules.IntegerValue> fertilegrounds$create(
      final int defaultValue, final BiConsumer<MinecraftServer, GameRules.IntegerValue> onChange) {
    throw new AssertionError("Mixin replaces this method");
  }
}
