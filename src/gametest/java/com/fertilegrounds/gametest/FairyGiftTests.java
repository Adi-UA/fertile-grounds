package com.fertilegrounds.gametest;

import com.fertilegrounds.fairy.FairyVisit;
import com.fertilegrounds.fairy.FairyVisitEvents;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;

/**
 * Gifts after a visit grows, at 100% gift chance. Each test fires the "visit grew" event that both
 * a flying fairy and a dawn visit go through.
 */
public class FairyGiftTests {

  private static final BlockPos CROP = new BlockPos(3, 2, 3);

  /** {@code ItemEntity}'s private INFINITE_LIFETIME: the age of an item that never despawns. */
  private static final int UNLIMITED_LIFETIME_AGE = -32768;

  @GameTest(environment = "fertilegrounds:gifts")
  public void ownerGetsAGiftThatNeverDespawns(final GameTestHelper helper) {
    final TestPlayer owner = TestPlayer.join(helper).at(helper, 1, 2, 1);
    grow(helper, FairyVisit.Source.DUST, owner);

    final List<ItemEntity> gifts = gifts(helper);
    helper.assertFalse(gifts.isEmpty(), "a gift dropped");
    gifts.forEach(
        gift -> helper.assertValueEqual(gift.getAge(), UNLIMITED_LIFETIME_AGE, "gift item age"));
    helper.assertTrue(
        owner.lastOverlayIs("message.fertilegrounds.fairy_gift"), "owner told about the gift");
    helper.assertTrue(owner.hasAdvancement("first_fairy_gift"), "first gift advancement");
    owner.leave();
    helper.succeed();
  }

  @GameTest(environment = "fertilegrounds:gifts")
  public void unownedVisitLeavesNoGift(final GameTestHelper helper) {
    grow(helper, FairyVisit.Source.UNOWNED, null);
    helper.assertTrue(gifts(helper).isEmpty(), "no gift for an unowned visit");
    helper.succeed();
  }

  /** With the default 3-day cooldown, a gift on day 0 blocks day 1's and allows day 3's. */
  @GameTest(environment = "fertilegrounds:gift_cooldown", maxTicks = 100)
  public void cooldownSkipsTheNextNightsGift(final GameTestHelper helper) {
    final TestPlayer owner = TestPlayer.join(helper).at(helper, 1, 2, 1);
    helper
        .startSequence()
        .thenExecute(() -> assertGiftOnDay(helper, owner, 0, true))
        .thenExecute(() -> assertGiftOnDay(helper, owner, 1, false))
        .thenExecute(() -> assertGiftOnDay(helper, owner, 3, true))
        .thenExecute(owner::leave)
        .thenSucceed();
  }

  private static void assertGiftOnDay(
      final GameTestHelper helper, final TestPlayer owner, final int day, final boolean expected) {
    TestSupport.command(helper, "time set " + (day * 24000 + 13000));
    helper.killAllEntitiesOfClass(ItemEntity.class);
    grow(helper, FairyVisit.Source.DUST, owner);
    helper.assertValueEqual(!gifts(helper).isEmpty(), expected, "gift on day " + day);
  }

  private static void grow(
      final GameTestHelper helper, final FairyVisit.Source source, final TestPlayer owner) {
    FairyVisitEvents.onVisitGrown(
        helper.getLevel(),
        FairyVisit.of(helper.absolutePos(CROP), 0, source, owner == null ? null : owner.getUUID()),
        false);
  }

  private static List<ItemEntity> gifts(final GameTestHelper helper) {
    return helper.getEntities(EntityType.ITEM);
  }
}
