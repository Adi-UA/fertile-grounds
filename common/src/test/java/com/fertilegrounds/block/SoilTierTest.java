package com.fertilegrounds.block;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SoilTierTest {

  @Test
  void enrichedSoilSignalsSeven() {
    assertEquals(7, SoilTier.ENRICHED.comparatorSignal());
  }

  @Test
  void superEnrichedSoilSignalsFifteen() {
    assertEquals(15, SoilTier.SUPER_ENRICHED.comparatorSignal());
  }
}
