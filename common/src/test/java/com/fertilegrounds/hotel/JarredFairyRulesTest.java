package com.fertilegrounds.hotel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class JarredFairyRulesTest {

  private static final long CAUGHT_AT = 5000;

  private static final long EXPIRES_AT = JarredFairyRules.expiresAt(CAUGHT_AT, 3);

  @Test
  void threeDaysIsThreeDaysOfGameTicks() {
    assertEquals(CAUGHT_AT + 72000, EXPIRES_AT);
  }

  @Test
  void stillJarredOneTickBeforeThreeDays() {
    assertFalse(JarredFairyRules.hasSlippedAway(EXPIRES_AT, EXPIRES_AT - 1));
  }

  @Test
  void slipsAwayAtThreeDays() {
    assertTrue(JarredFairyRules.hasSlippedAway(EXPIRES_AT, EXPIRES_AT));
  }

  @Test
  void slipsAwayAfterThreeDays() {
    assertTrue(JarredFairyRules.hasSlippedAway(EXPIRES_AT, EXPIRES_AT + 4 * 24000));
  }

  @Test
  void leavesTheFlowerTheRandomIndexPicks() {
    assertEquals(
        "dandelion",
        JarredFairyRules.leftFlower(List.of("poppy", "dandelion", "allium"), "poppy", bound -> 1));
  }

  @Test
  void anEmptyTagLeavesTheFallback() {
    assertEquals("poppy", JarredFairyRules.leftFlower(List.of(), "poppy", bound -> 0));
  }
}
