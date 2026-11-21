package com.fertilegrounds.fairy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fertilegrounds.fairy.FairyGifts.Tier;
import com.fertilegrounds.fairy.FairyVisit.Source;
import org.junit.jupiter.api.Test;

class FairyGiftsTest {

  @Test
  void dispenserAndOldVisitsNeverGiveGifts() {
    assertEquals(0, FairyGifts.chance(Source.UNOWNED, 30, 10, 10));
  }

  @Test
  void naturalAndDustUseTheirOwnChance() {
    assertEquals(30, FairyGifts.chance(Source.NATURAL, 30, 10, 0));
    assertEquals(10, FairyGifts.chance(Source.DUST, 30, 10, 0));
  }

  @Test
  void dustVisitWithFourFlowersIsFourteenPercent() {
    assertEquals(14, FairyGifts.chance(Source.DUST, 30, 10, 4));
  }

  @Test
  void flowerPointsAreCappedAtTen() {
    assertEquals(40, FairyGifts.chance(Source.NATURAL, 30, 10, 14));
  }

  @Test
  void aZeroChanceGamerulePlusFlowersStaysZero() {
    assertEquals(0, FairyGifts.chance(Source.DUST, 30, 0, 5));
  }

  @Test
  void cooldownBlocksTheNextNightsGift() {
    assertFalse(FairyGifts.isOffCooldown(10, 11, 3));
    assertTrue(FairyGifts.isOffCooldown(10, 13, 3));
    assertTrue(FairyGifts.isOffCooldown(-1, 0, 3));
  }

  @Test
  void farmSizePicksTheTier() {
    assertEquals(Tier.COMMON, FairyGifts.tier(24, false));
    assertEquals(Tier.UNCOMMON, FairyGifts.tier(25, false));
    assertEquals(Tier.UNCOMMON, FairyGifts.tier(99, false));
    assertEquals(Tier.RARE, FairyGifts.tier(140, false));
  }

  @Test
  void harvestMoonStepsUpAndRareStaysRare() {
    assertEquals(Tier.UNCOMMON, FairyGifts.tier(10, true));
    assertEquals(Tier.RARE, FairyGifts.tier(50, true));
    assertEquals(Tier.RARE, FairyGifts.tier(140, true));
  }
}
