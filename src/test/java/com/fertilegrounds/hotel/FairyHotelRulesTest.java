package com.fertilegrounds.hotel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.LongStream;
import org.junit.jupiter.api.Test;

class FairyHotelRulesTest {

  @Test
  void dawnIsAFullDayAwayAtDawn() {
    assertEquals(24000, FairyHotelRules.ticksUntilDawn(48000));
  }

  @Test
  void dawnCountsFromTheClockOnAnyDay() {
    assertEquals(1000, FairyHotelRules.ticksUntilDawn(3 * 24000 + 23000));
  }

  @Test
  void aCheckRightAfterDawnSeesIt() {
    assertTrue(FairyHotelRules.isJustAfterDawn(2 * 24000 + 40, 100));
  }

  @Test
  void aCheckLaterInTheDayDoesNot() {
    assertFalse(FairyHotelRules.isJustAfterDawn(2 * 24000 + 100, 100));
    assertFalse(FairyHotelRules.isJustAfterDawn(23999, 100));
  }

  @Test
  void aHotelAloneMakesOneBoostAttempt() {
    assertEquals(1, FairyHotelRules.boostAttempts(false));
  }

  @Test
  void aNectarBeeNearbyAddsOneAttempt() {
    assertEquals(2, FairyHotelRules.boostAttempts(true));
  }

  @Test
  void aLoneHotelLeads() {
    assertTrue(FairyHotelRules.isClusterLeader(42, LongStream.of(42)));
  }

  @Test
  void ofAPairOnlyTheSmallerLeads() {
    assertTrue(FairyHotelRules.isClusterLeader(10, LongStream.of(10, 20)));
    assertFalse(FairyHotelRules.isClusterLeader(20, LongStream.of(10, 20)));
  }

  /**
   * Hotels A, B, C in a row, where A and C are too far apart to see each other: A leads, B sees A
   * and doesn't, and C sees only B, which is smaller, so C doesn't either.
   */
  @Test
  void ofThreeInARowOnlyTheEndWithTheSmallestPositionLeads() {
    assertTrue(FairyHotelRules.isClusterLeader(1, LongStream.of(1, 2)));
    assertFalse(FairyHotelRules.isClusterLeader(2, LongStream.of(1, 2, 3)));
    assertFalse(FairyHotelRules.isClusterLeader(3, LongStream.of(2, 3)));
  }
}
