package com.fertilegrounds.fairy;

/**
 * The rules for the occasional gift a fairy leaves after a visit, kept free of world access so
 * JUnit can test them: how likely a gift is, whether the owner is off cooldown, and which tier of
 * loot table it rolls.
 */
public final class FairyGifts {

  /** Each distinct fairy flower adds 1 percentage point, up to this many. */
  public static final int MAX_FLOWER_POINTS = 10;

  /** Farms with at least this many fairy crops get uncommon gifts. */
  public static final int UNCOMMON_CROPS = 25;

  /** Farms with at least this many fairy crops get rare gifts. */
  public static final int RARE_CROPS = 100;

  /** Which loot table a gift rolls on: {@code fertilegrounds:gameplay/fairy_gift/<name>}. */
  public enum Tier {
    COMMON,
    UNCOMMON,
    RARE;

    /** One tier higher, for a Harvest Moon. Rare stays rare. */
    public Tier stepUp() {
      return values()[Math.min(ordinal() + 1, RARE.ordinal())];
    }
  }

  private FairyGifts() {}

  /**
   * The gift chance in percent: the gamerule for the visit's source, plus a point per distinct
   * flower type nearby. Unowned visits (dispensers, 1.2.0 saves) never give gifts.
   */
  public static int chance(
      final FairyVisit.Source source,
      final int naturalPercent,
      final int dustPercent,
      final int flowerTypes) {
    final int base =
        switch (source) {
          case NATURAL -> naturalPercent;
          case DUST -> dustPercent;
          case UNOWNED -> 0;
        };
    return base == 0 ? 0 : base + Math.min(flowerTypes, MAX_FLOWER_POINTS);
  }

  /**
   * @param lastGiftDay the in-game day the player last got a gift, or -1 if never
   */
  public static boolean isOffCooldown(final long lastGiftDay, final long today, final int days) {
    return lastGiftDay < 0 || today - lastGiftDay >= days;
  }

  /** The tier for a farm with {@code fairyCrops} crops nearby, one step up on a Harvest Moon. */
  public static Tier tier(final int fairyCrops, final boolean harvestMoon) {
    final Tier tier =
        fairyCrops >= RARE_CROPS
            ? Tier.RARE
            : fairyCrops >= UNCOMMON_CROPS ? Tier.UNCOMMON : Tier.COMMON;
    return harvestMoon ? tier.stepUp() : tier;
  }
}
