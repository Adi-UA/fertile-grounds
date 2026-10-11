package com.fertilegrounds.util;

import com.fertilegrounds.hotel.FairySpeedEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;

/** The mod's status effects, registered like vanilla's {@code MobEffects}. */
public final class ModMobEffects {

  public static final Holder<MobEffect> FAIRY_SPEED =
      Registry.registerForHolder(
          BuiltInRegistries.MOB_EFFECT, ModIdsUtil.id("fairy_speed"), new FairySpeedEffect());

  private ModMobEffects() {}

  /** Loads this class so the static fields above register during mod startup. */
  public static void register() {}
}
