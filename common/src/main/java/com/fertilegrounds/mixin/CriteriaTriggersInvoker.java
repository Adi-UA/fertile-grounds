package com.fertilegrounds.mixin;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Calls vanilla's private {@code CriteriaTriggers.register}, since this Minecraft version has no
 * trigger registry.
 */
@Mixin(CriteriaTriggers.class)
public interface CriteriaTriggersInvoker {

  @Invoker("register")
  static <T extends CriterionTrigger<?>> T fertilegrounds$register(final T trigger) {
    throw new AssertionError("Mixin replaces this method");
  }
}
