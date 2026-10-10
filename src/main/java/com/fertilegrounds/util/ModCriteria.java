package com.fertilegrounds.util;

import com.fertilegrounds.fairy.FairyVisitTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

/** The mod's custom advancement triggers, registered like vanilla's in {@code CriteriaTriggers}. */
public final class ModCriteria {

  public static final FairyVisitTrigger FAIRY_VISIT =
      Registry.register(
          BuiltInRegistries.TRIGGER_TYPES, ModIdsUtil.id("fairy_visit"), new FairyVisitTrigger());

  private ModCriteria() {}

  /** Loads this class so the static fields above register their triggers during mod startup. */
  public static void register() {}
}
