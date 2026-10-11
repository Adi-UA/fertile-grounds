package com.fertilegrounds.blight;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fertilegrounds.block.SoilTier;
import org.junit.jupiter.api.Test;

class BlightRulesTest {

  @Test
  void plainSoilTakesTheFullChance() {
    assertEquals(20, BlightRules.spreadChance(20, null));
  }

  @Test
  void enrichedSoilResistsHalf() {
    assertEquals(10, BlightRules.spreadChance(20, SoilTier.ENRICHED));
  }

  @Test
  void superEnrichedSoilResistsThreeQuarters() {
    assertEquals(5, BlightRules.spreadChance(20, SoilTier.SUPER_ENRICHED));
  }

  @Test
  void zeroStopsSpreadingEverywhere() {
    assertEquals(0, BlightRules.spreadChance(0, null));
    assertEquals(0, BlightRules.spreadChance(0, SoilTier.SUPER_ENRICHED));
  }
}
