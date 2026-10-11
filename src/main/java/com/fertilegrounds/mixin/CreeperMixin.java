package com.fertilegrounds.mixin;

import com.fertilegrounds.hotel.AvoidFairyHotelGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Makes creepers avoid occupied Fairy Hotels like they avoid cats. Fabric API has no hook for
 * adding a mob's goals, so this appends one when vanilla registers the creeper's.
 */
@Mixin(Creeper.class)
abstract class CreeperMixin extends Monster {

  private CreeperMixin(final EntityType<? extends Monster> type, final Level level) {
    super(type, level);
  }

  /** Same priority and speeds as vanilla's cat-avoid goal. */
  @Inject(method = "registerGoals", at = @At("TAIL"))
  private void fertilegrounds$avoidFairyHotels(final CallbackInfo ci) {
    this.goalSelector.addGoal(3, new AvoidFairyHotelGoal(this, 1.0, 1.2));
  }
}
