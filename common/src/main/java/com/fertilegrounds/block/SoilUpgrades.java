package com.fertilegrounds.block;

import java.util.Map;
import java.util.Optional;

/**
 * Which soil Fertilizer turns each block into, by block id. Kept free of Minecraft types so JUnit
 * can test every mapping; Fertilizer by hand and by dispenser both read it, so their rules can't
 * drift apart.
 */
public final class SoilUpgrades {

  private static final Map<String, String> UPGRADES =
      Map.of(
          "minecraft:dirt", "fertilegrounds:super_enriched_dirt",
          "fertilegrounds:enriched_dirt", "fertilegrounds:super_enriched_dirt",
          "minecraft:farmland", "fertilegrounds:super_enriched_farmland",
          "fertilegrounds:enriched_farmland", "fertilegrounds:super_enriched_farmland",
          "minecraft:sand", "fertilegrounds:enriched_sand");

  private SoilUpgrades() {}

  /** The id of the block {@code blockId} upgrades to, or empty if Fertilizer does nothing there. */
  public static Optional<String> upgradeOf(final String blockId) {
    return Optional.ofNullable(UPGRADES.get(blockId));
  }
}
