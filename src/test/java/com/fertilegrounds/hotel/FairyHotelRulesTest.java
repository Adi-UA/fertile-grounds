package com.fertilegrounds.hotel;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.LongStream;
import org.junit.jupiter.api.Test;

class FairyHotelRulesTest {

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
