package com.fertilegrounds.util;

import net.minecraft.advancements.triggers.PlayerTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

/**
 * The mod's custom advancement triggers. Each only needs "this happened to a player", so they reuse
 * vanilla's {@link PlayerTrigger}, the same class vanilla registers for its own simple triggers
 * (sleeping in a bed, starting to ride, and others).
 */
public final class ModCriteria {

  /** A fairy visit grew crops within 32 blocks of the player. */
  public static final PlayerTrigger FAIRY_VISIT = register("fairy_visit");

  /** The player received a fairy gift. */
  public static final PlayerTrigger FAIRY_GIFT = register("fairy_gift");

  /** A fairy visit grew crops near the player on a Harvest Moon night. */
  public static final PlayerTrigger HARVEST_MOON_VISIT = register("harvest_moon_visit");

  private ModCriteria() {}

  /** Loads this class so the static fields above register their triggers during mod startup. */
  public static void register() {}

  private static PlayerTrigger register(final String path) {
    return Registry.register(
        BuiltInRegistries.TRIGGER_TYPES, ModIdsUtil.id(path), new PlayerTrigger());
  }
}
