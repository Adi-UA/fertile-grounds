package com.fertilegrounds.blight;

import com.fertilegrounds.block.SoilTier;

/** How fast blight spreads. Kept free of Minecraft types so JUnit can test it. */
public final class BlightRules {

  private BlightRules() {}

  /**
   * The percent chance that one random tick of Blighted Dirt takes the soil it picked.
   *
   * @param basePercent the {@code blight_spread_chance} gamerule
   * @param tier the target soil's tier, or null for plain soil
   */
  public static double spreadChance(final int basePercent, final SoilTier tier) {
    final int resistance =
        switch (tier) {
          case null -> 1;
          case ENRICHED -> 2;
          case SUPER_ENRICHED -> 4;
        };
    return (double) basePercent / resistance;
  }
}
