package com.fertilegrounds.fabric;

import com.fertilegrounds.FertileGrounds;
import com.fertilegrounds.block.ModBlocks;
import com.fertilegrounds.entity.BeanFairy;
import com.fertilegrounds.entity.ModEntities;
import com.fertilegrounds.hotel.FairyHotelLookup;
import com.fertilegrounds.item.ModItemGroups;
import com.fertilegrounds.item.WanderingTraderOffers;
import com.fertilegrounds.util.ModGameRules;
import com.fertilegrounds.util.ModSavedData;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.fabricmc.fabric.api.registry.TillableBlockRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.level.block.Block;

/** Starts the mod on Fabric: registers everything at startup, then hooks up Fabric API events. */
public class FertileGroundsFabric implements ModInitializer {

  @Override
  public void onInitialize() {
    FertileGrounds.LOGGER.info("Fertile Grounds initializing");
    FertileGrounds.REGISTRATIONS.values().forEach(Runnable::run);
    ModGameRules.register();
    ModSavedData.register();
    Registry.register(
        BuiltInRegistries.CREATIVE_MODE_TAB,
        ModItemGroups.MAIN,
        FabricItemGroup.builder()
            .title(ModItemGroups.TITLE)
            .icon(ModItemGroups::icon)
            .displayItems((parameters, output) -> ModItemGroups.contents().forEach(output::accept))
            .build());
    PointOfInterestHelper.register(
        FairyHotelLookup.OCCUPIED_HOTEL.location(),
        FairyHotelLookup.MAX_TICKETS,
        FairyHotelLookup.VALID_RANGE,
        FairyHotelLookup.occupiedHotels().toArray(Block[]::new));
    FabricDefaultAttributeRegistry.register(ModEntities.BEAN_FAIRY, BeanFairy.createAttributes());
    // Fabric API's public stand-in for HoeItem's package-private tilling map.
    ModBlocks.TILLED.forEach(
        (untilled, tilled) ->
            TillableBlockRegistry.register(
                untilled, HoeItem::onlyIfAirAbove, tilled.defaultBlockState()));
    TradeOfferHelper.registerWanderingTraderOffers(
        offers ->
            offers.addOffersToPool(
                TradeOfferHelper.WanderingTraderOffersBuilder.SELL_SPECIAL_ITEMS_POOL,
                WanderingTraderOffers.rare()));

    ServerTickEvents.END_WORLD_TICK.register(FertileGrounds::onLevelTick);
    ServerLifecycleEvents.SERVER_STOPPED.register(server -> FertileGrounds.onServerStopped());
    ServerPlayConnectionEvents.JOIN.register(
        (handler, sender, server) -> FertileGrounds.onPlayerJoin(handler.player));
  }
}
