package com.fertilegrounds.item;

import java.util.List;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;

/**
 * The Wandering Trader's rare offers the mod adds. The trader picks one of its rare offers each
 * time it spawns. Each loader adds these with its own hook: Fabric's {@code TradeOfferHelper},
 * NeoForge's and Forge's {@code WandererTradesEvent}.
 */
public final class WanderingTraderOffers {

  /** The level of vanilla's rare offers in {@code VillagerTrades.WANDERING_TRADER_TRADES}. */
  public static final int RARE = 2;

  /** Vanilla's price change per point of reputation, the same for every trader offer. */
  private static final float PRICE_MULTIPLIER = 0.05F;

  private WanderingTraderOffers() {}

  /** A new list each call, since the items must be registered first. */
  public static List<VillagerTrades.ItemListing> rare() {
    return List.of(sell(ModItems.FAIRY_DUST, 2, 4, 4), sell(ModItems.FERTILIZER, 1, 6, 3));
  }

  /** Like vanilla's {@code ItemsForEmeralds}, which this Minecraft version keeps private. */
  private static VillagerTrades.ItemListing sell(
      final Item item, final int count, final int emeralds, final int maxUses) {
    return (trader, random) ->
        new MerchantOffer(
            new ItemCost(Items.EMERALD, emeralds),
            new ItemStack(item, count),
            maxUses,
            1,
            PRICE_MULTIPLIER);
  }
}
