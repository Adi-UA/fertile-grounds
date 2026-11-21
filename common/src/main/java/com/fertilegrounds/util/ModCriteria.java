package com.fertilegrounds.util;

import com.fertilegrounds.mixin.CriteriaTriggersInvoker;
import net.minecraft.advancements.critereon.PlayerTrigger;

/**
 * The mod's advancement triggers. Each fires when something happens to a player, so each is a
 * vanilla {@link PlayerTrigger}, as vanilla uses for sleeping in a bed.
 */
public final class ModCriteria {

  /** A fairy visit grew crops within 32 blocks of the player. */
  public static final PlayerTrigger FAIRY_VISIT = register("fairy_visit");

  /** The player received a fairy gift. */
  public static final PlayerTrigger FAIRY_GIFT = register("fairy_gift");

  /** A fairy visit grew crops near the player on a Harvest Moon night. */
  public static final PlayerTrigger HARVEST_MOON_VISIT = register("harvest_moon_visit");

  /** The player caught a Bean Fairy in a Glass Bottle. */
  public static final PlayerTrigger FAIRY_CAUGHT = register("fairy_caught");

  /** The player moved a Jarred Fairy into an Empty Fairy Hotel. */
  public static final PlayerTrigger FAIRY_MOVED_IN = register("fairy_moved_in");

  private ModCriteria() {}

  /**
   * Loads this class so the static fields above register their triggers. This Minecraft version has
   * no trigger registry, so each loader calls this from its entry point.
   */
  public static void register() {}

  private static PlayerTrigger register(final String path) {
    return CriteriaTriggersInvoker.fertilegrounds$register(new PlayerTrigger(ModIdsUtil.id(path)));
  }
}
