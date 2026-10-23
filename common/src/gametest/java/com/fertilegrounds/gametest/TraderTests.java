package com.fertilegrounds.gametest;

import com.fertilegrounds.item.ModItems;
import com.fertilegrounds.item.WanderingTraderOffers;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;

/**
 * The Wandering Trader's rare offers. The trader picks one of them at random when it spawns, so
 * these tests read the rare list and build each offer the way the trader does.
 */
public class TraderTests {

  /** Vanilla's rare list in 1.21.1 has 6 offers; the mod adds to it and removes none. */
  private static final int VANILLA_RARE_OFFERS = 6;

  @FertileTest
  public void rarePoolHasBothModTradesAndEveryVanillaOne(final GameTestHelper helper) {
    final WanderingTrader trader = helper.spawn(EntityType.WANDERING_TRADER, 2, 2, 2);
    final List<MerchantOffer> offers = rareOffers(helper, trader);
    helper.assertTrue(
        offers.stream().anyMatch(offer -> offer.getResult().is(ModItems.FAIRY_DUST)),
        "the rare list has the Fairy Dust offer");
    helper.assertTrue(
        offers.stream().anyMatch(offer -> offer.getResult().is(ModItems.FERTILIZER)),
        "the rare list has the Fertilizer offer");
    helper.assertValueEqual(
        offers.size(),
        VANILLA_RARE_OFFERS + WanderingTraderOffers.rare().size(),
        "offers in the rare list");
    trader.discard();
    helper.succeed();
  }

  @FertileTest
  public void fairyDustSellsTwoForFourEmeraldsFourTimes(final GameTestHelper helper) {
    assertOffer(helper, ModItems.FAIRY_DUST, 2, 4, 4);
  }

  @FertileTest
  public void fertilizerSellsOneForSixEmeraldsThreeTimes(final GameTestHelper helper) {
    assertOffer(helper, ModItems.FERTILIZER, 1, 6, 3);
  }

  private static void assertOffer(
      final GameTestHelper helper,
      final Item item,
      final int count,
      final int emeralds,
      final int uses) {
    final WanderingTrader trader = helper.spawn(EntityType.WANDERING_TRADER, 2, 2, 2);
    final MerchantOffer offer =
        rareOffers(helper, trader).stream()
            .filter(candidate -> candidate.getResult().is(item))
            .findFirst()
            .orElseThrow(() -> new GameTestAssertException("no rare offer sells " + item));
    helper.assertValueEqual(offer.getResult().getCount(), count, "items sold");
    helper.assertTrue(offer.getBaseCostA().is(Items.EMERALD), "costs emeralds");
    helper.assertValueEqual(offer.getBaseCostA().getCount(), emeralds, "emerald price");
    helper.assertValueEqual(offer.getMaxUses(), uses, "uses before it locks");
    trader.discard();
    helper.succeed();
  }

  /**
   * Every rare offer, built the way {@code WanderingTrader.updateTrades} builds the one it picks.
   */
  private static List<MerchantOffer> rareOffers(
      final GameTestHelper helper, final WanderingTrader trader) {
    return Arrays.stream(VillagerTrades.WANDERING_TRADER_TRADES.get(WanderingTraderOffers.RARE))
        .map(listing -> listing.getOffer(trader, helper.getLevel().getRandom()))
        .filter(Objects::nonNull)
        .toList();
  }
}
