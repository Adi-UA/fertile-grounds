package com.fertilegrounds.gametest;

import com.fertilegrounds.item.ModItems;
import com.fertilegrounds.util.ModIdsUtil;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.item.trading.TradeSets;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

/**
 * The Wandering Trader's rare trades. Since 26.1 trades are data: each is a {@code villager_trade}
 * file, and the trader rolls 2 from the {@code wandering_trader/uncommon} tag. Rolling is random,
 * so these tests read the pool and build each offer the way the trader does.
 */
public class TraderTests {

  /**
   * Vanilla's rare pool in 26.2 has 15 trades (26.3 has 16); the mod adds to it and removes none.
   */
  private static final int VANILLA_RARE_TRADES = 15;

  @FertileTest
  public void rarePoolHasBothModTradesAndEveryVanillaOne(final GameTestHelper helper) {
    final TradeSet rare = rarePool(helper);
    helper.assertTrue(
        rare.getTrades().stream().anyMatch(trade -> trade.is(trade("emerald_fairy_dust"))),
        "the rare pool has the Fairy Dust trade");
    helper.assertTrue(
        rare.getTrades().stream().anyMatch(trade -> trade.is(trade("emerald_fertilizer"))),
        "the rare pool has the Fertilizer trade");
    helper.assertValueEqual(
        rare.getTrades().stream()
            .filter(
                trade ->
                    trade.unwrapKey().orElseThrow().identifier().getNamespace().equals("minecraft"))
            .count(),
        (long) VANILLA_RARE_TRADES,
        "vanilla trades in the rare pool");
    helper.succeed();
  }

  @FertileTest
  public void fairyDustSellsTwoForFourEmeraldsFourTimes(final GameTestHelper helper) {
    assertOffer(helper, "emerald_fairy_dust", ModItems.FAIRY_DUST, 2, 4, 4);
  }

  @FertileTest
  public void fertilizerSellsOneForSixEmeraldsThreeTimes(final GameTestHelper helper) {
    assertOffer(helper, "emerald_fertilizer", ModItems.FERTILIZER, 1, 6, 3);
  }

  private static void assertOffer(
      final GameTestHelper helper,
      final String path,
      final Item item,
      final int count,
      final int emeralds,
      final int uses) {
    final WanderingTrader trader = helper.spawn(EntityTypes.WANDERING_TRADER, 2, 2, 2);
    final Holder<VillagerTrade> trade =
        helper
            .getLevel()
            .registryAccess()
            .lookupOrThrow(Registries.VILLAGER_TRADE)
            .getOrThrow(trade(path));
    final MerchantOffer offer = trade.value().getOffer(tradeContext(helper, trader));
    helper.assertTrue(offer != null, "the trade made an offer");
    helper.assertTrue(offer.getResult().is(item), "sells " + item);
    helper.assertValueEqual(offer.getResult().getCount(), count, "items sold");
    helper.assertTrue(offer.getBaseCostA().is(Items.EMERALD), "costs emeralds");
    helper.assertValueEqual(offer.getBaseCostA().getCount(), emeralds, "emerald price");
    helper.assertValueEqual(offer.getMaxUses(), uses, "uses before it locks");
    trader.discard();
    helper.succeed();
  }

  /** The same loot context {@code AbstractVillager.addOffersFromTradeSet} builds for a trade. */
  private static LootContext tradeContext(
      final GameTestHelper helper, final WanderingTrader trader) {
    return new LootContext.Builder(
            new LootParams.Builder(helper.getLevel())
                .withParameter(LootContextParams.ORIGIN, trader.position())
                .withParameter(LootContextParams.THIS_ENTITY, trader)
                .withParameter(LootContextParams.ADDITIONAL_COST_COMPONENT_ALLOWED, Unit.INSTANCE)
                .create(LootContextParamSets.VILLAGER_TRADE))
        .create(Optional.<Identifier>empty());
  }

  private static TradeSet rarePool(final GameTestHelper helper) {
    return helper
        .getLevel()
        .registryAccess()
        .lookupOrThrow(Registries.TRADE_SET)
        .getOrThrow(TradeSets.WANDERING_TRADER_UNCOMMON)
        .value();
  }

  private static ResourceKey<VillagerTrade> trade(final String path) {
    return ResourceKey.create(Registries.VILLAGER_TRADE, ModIdsUtil.id("wandering_trader/" + path));
  }
}
