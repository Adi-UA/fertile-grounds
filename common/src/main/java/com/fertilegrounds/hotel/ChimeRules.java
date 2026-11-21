package com.fertilegrounds.hotel;

/**
 * When a Chime Hotel rings, kept free of world access so JUnit can test it. A hotel is "ready"
 * while enough of its nearby crops are fully grown; it chimes only on the check where it becomes
 * ready, so a ripe field rings once and the hotel re-arms after the harvest.
 */
public final class ChimeRules {

  /** A field needs at least this many crops, so stray plants never ring. */
  public static final int MIN_CROPS = 4;

  private ChimeRules() {}

  /**
   * One check of a Chime Hotel.
   *
   * @param ready whether the hotel was ready after its last check
   * @param grown how many nearby crops are fully grown
   * @param total how many nearby crops there are
   * @param thresholdPercent the share of grown crops that counts as ready, from 1 to 100
   * @return the hotel's new ready state and whether it chimes now
   */
  public static Check next(
      final boolean ready, final int grown, final int total, final int thresholdPercent) {
    final boolean nowReady = total >= MIN_CROPS && grown * 100 >= thresholdPercent * total;
    return new Check(nowReady, nowReady && !ready);
  }

  /** The result of one check: the new ready state, and whether to ring. */
  public record Check(boolean ready, boolean chime) {}
}
