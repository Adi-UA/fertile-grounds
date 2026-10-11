package com.fertilegrounds.neoforge;

import com.fertilegrounds.FertileGrounds;
import com.fertilegrounds.entity.BeanFairy;
import com.fertilegrounds.entity.ModEntities;
import com.fertilegrounds.hotel.FairyHotelLookup;
import com.fertilegrounds.item.ModItemGroups;
import com.fertilegrounds.util.ModSavedData;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;

/**
 * Starts the mod on NeoForge. NeoForge only accepts a registry's entries during that registry's
 * {@link RegisterEvent}, so each shared registration runs there, then game events are connected to
 * the shared handlers. Hoe tilling is data on NeoForge: {@code
 * data/neoforge/data_maps/block/transformables.json}.
 */
@Mod(FertileGrounds.MOD_ID)
public final class FertileGroundsNeoForge {

  public FertileGroundsNeoForge(final IEventBus modBus) {
    FertileGrounds.LOGGER.info("Fertile Grounds initializing");
    modBus.addListener(FertileGroundsNeoForge::register);
    modBus.addListener(FertileGroundsNeoForge::createAttributes);
    NeoForge.EVENT_BUS.addListener(FertileGroundsNeoForge::onLevelTick);
    NeoForge.EVENT_BUS.addListener(
        (final ServerStoppedEvent event) -> FertileGrounds.onServerStopped());
    NeoForge.EVENT_BUS.addListener(FertileGroundsNeoForge::onPlayerJoin);
  }

  private static void register(final RegisterEvent event) {
    final Runnable registration = FertileGrounds.REGISTRATIONS.get(event.getRegistryKey());
    if (registration != null) {
      registration.run();
    }
    if (event.getRegistryKey().equals(NeoForgeRegistries.Keys.ATTACHMENT_TYPES)) {
      ModSavedData.register();
    }
    event.register(
        Registries.CREATIVE_MODE_TAB,
        ModItemGroups.MAIN.identifier(),
        () ->
            CreativeModeTab.builder()
                .title(ModItemGroups.TITLE)
                .icon(ModItemGroups::icon)
                .displayItems(
                    (parameters, output) -> ModItemGroups.contents().forEach(output::accept))
                .build());
    // NeoForge maps each block state to its point of interest when the type is registered.
    event.register(
        Registries.POINT_OF_INTEREST_TYPE,
        FairyHotelLookup.OCCUPIED_HOTEL.identifier(),
        () ->
            new PoiType(hotelStates(), FairyHotelLookup.MAX_TICKETS, FairyHotelLookup.VALID_RANGE));
  }

  private static Set<BlockState> hotelStates() {
    return FairyHotelLookup.occupiedHotels().stream()
        .flatMap(block -> block.getStateDefinition().getPossibleStates().stream())
        .collect(Collectors.toUnmodifiableSet());
  }

  private static void createAttributes(final EntityAttributeCreationEvent event) {
    event.put(ModEntities.BEAN_FAIRY, BeanFairy.createAttributes().build());
  }

  private static void onLevelTick(final LevelTickEvent.Post event) {
    if (event.getLevel() instanceof ServerLevel level) {
      FertileGrounds.onLevelTick(level);
    }
  }

  private static void onPlayerJoin(final PlayerEvent.PlayerLoggedInEvent event) {
    if (event.getEntity() instanceof ServerPlayer player) {
      FertileGrounds.onPlayerJoin(player);
    }
  }
}
