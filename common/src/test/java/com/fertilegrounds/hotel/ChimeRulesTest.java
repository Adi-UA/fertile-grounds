package com.fertilegrounds.hotel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ChimeRulesTest {

  @Test
  void fortyOneOfFiftyGrownChimesOnce() {
    assertEquals(new ChimeRules.Check(true, true), ChimeRules.next(false, 41, 50, 80));
  }

  @Test
  void exactlyTheThresholdCounts() {
    assertEquals(new ChimeRules.Check(true, true), ChimeRules.next(false, 40, 50, 80));
  }

  @Test
  void belowTheThresholdStaysQuiet() {
    assertEquals(new ChimeRules.Check(false, false), ChimeRules.next(false, 39, 50, 80));
  }

  @Test
  void aFieldThatStaysGrownDoesNotChimeAgain() {
    assertEquals(new ChimeRules.Check(true, false), ChimeRules.next(true, 50, 50, 80));
  }

  @Test
  void harvestingReArmsAndRegrowingChimesAgain() {
    final ChimeRules.Check harvested = ChimeRules.next(true, 0, 50, 80);
    assertEquals(new ChimeRules.Check(false, false), harvested);
    assertEquals(new ChimeRules.Check(true, true), ChimeRules.next(harvested.ready(), 45, 50, 80));
  }

  @Test
  void threeGrownCropsAreTooFew() {
    assertEquals(new ChimeRules.Check(false, false), ChimeRules.next(false, 3, 3, 80));
  }

  @Test
  void fourGrownCropsAreEnough() {
    assertEquals(new ChimeRules.Check(true, true), ChimeRules.next(false, 4, 4, 80));
  }

  @Test
  void breakingEveryCropReArms() {
    assertEquals(new ChimeRules.Check(false, false), ChimeRules.next(true, 0, 0, 80));
  }
}
