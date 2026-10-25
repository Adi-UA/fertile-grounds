package com.fertilegrounds.fabric;

import com.fertilegrounds.FertileGrounds;
import com.fertilegrounds.block.ModBlocks;
import com.fertilegrounds.entity.BeanFairy;
import com.fertilegrounds.entity.ModEntities;
import com.fertilegrounds.hotel.FairyHotelLookup;
import com.fertilegrounds.item.ModItemGroups;
import com.fertilegrounds.util.ModSavedData;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.item.v1.BlockTransformerHelper;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PoiHelper;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;

/** Starts the mod on Fabric: registers everything at startup, then hooks up Fabric API events. */
public class FertileGroundsFabric implements ModInitializer {

  @Override
  public void onInitialize() {
    FertileGrounds.LOGGER.info("Fertile Grounds initializing");
    FertileGrounds.REGISTRATIONS.values().forEach(Runnable::run);
    ModSavedData.register();
    Registry.register(
        BuiltInRegistries.CREATIVE_MODE_TAB,
        ModItemGroups.MAIN,
        FabricCreativeModeTab.builder()
            .title(ModItemGroups.TITLE)
            .icon(ModItemGroups::icon)
            .displayItems((parameters, output) -> ModItemGroups.contents().forEach(output::accept))
            .build());
    PoiHelper.register(
        FairyHotelLookup.OCCUPIED_HOTEL.identifier(),
        FairyHotelLookup.MAX_TICKETS,
        FairyHotelLookup.VALID_RANGE,
        FairyHotelLookup.occupiedHotels().toArray(Block[]::new));
    FabricDefaultAttributeRegistry.register(ModEntities.BEAN_FAIRY, BeanFairy.createAttributes());
    // Hoe tilling is a data-driven block transformer; this appends our rules to the hoe's.
    ModBlocks.TILLED.forEach(
        (untilled, tilled) ->
            BlockTransformerHelper.registerTilling(
                BlockPredicate.allOf(
                    BlockPredicate.matchesBlocks(untilled),
                    BlockPredicate.matchesTag(Direction.UP, BlockTags.AIR)),
                tilled));

    ServerTickEvents.END_LEVEL_TICK.register(FertileGrounds::onLevelTick);
    ServerLifecycleEvents.SERVER_STOPPED.register(server -> FertileGrounds.onServerStopped());
    ServerPlayConnectionEvents.JOIN.register(
        (handler, sender, server) -> FertileGrounds.onPlayerJoin(handler.player));
  }
}
