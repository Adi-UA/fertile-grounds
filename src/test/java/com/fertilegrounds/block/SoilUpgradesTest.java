package com.fertilegrounds.block;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class SoilUpgradesTest {

  @ParameterizedTest
  @CsvSource({
    "minecraft:dirt, fertilegrounds:super_enriched_dirt",
    "fertilegrounds:enriched_dirt, fertilegrounds:super_enriched_dirt",
    "minecraft:farmland, fertilegrounds:super_enriched_farmland",
    "fertilegrounds:enriched_farmland, fertilegrounds:super_enriched_farmland",
    "minecraft:sand, fertilegrounds:enriched_sand",
  })
  void upgradesEachSoil(final String from, final String to) {
    assertEquals(Optional.of(to), SoilUpgrades.upgradeOf(from));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "fertilegrounds:super_enriched_dirt",
        "fertilegrounds:super_enriched_farmland",
        "fertilegrounds:enriched_sand",
      })
  void topTierSoilHasNoUpgrade(final String block) {
    assertTrue(SoilUpgrades.upgradeOf(block).isEmpty());
  }

  @Test
  void otherBlocksHaveNoUpgrade() {
    assertTrue(SoilUpgrades.upgradeOf("minecraft:stone").isEmpty());
    assertTrue(SoilUpgrades.upgradeOf("minecraft:grass_block").isEmpty());
  }
}
