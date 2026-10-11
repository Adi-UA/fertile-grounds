package com.fertilegrounds.forge;

import com.fertilegrounds.FertileGrounds;
import com.fertilegrounds.block.ModBlocks;
import com.fertilegrounds.entity.BeanFairy;
import com.fertilegrounds.entity.ModEntities;
import com.fertilegrounds.hotel.FairyHotelLookup;
import com.fertilegrounds.item.ModItemGroups;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

/**
 * Starts the mod on Forge. Forge only accepts a registry's entries during that registry's {@link
 * RegisterEvent}, so each shared registration runs there, then game events are connected to the
 * shared handlers.
 */
@Mod(FertileGrounds.MOD_ID)
public final class FertileGroundsForge {

  public FertileGroundsForge(final FMLJavaModLoadingContext context) {
    FertileGrounds.LOGGER.info("Fertile Grounds initializing");
    RegisterEvent.getBus(context.getModBusGroup()).addListener(FertileGroundsForge::register);
    EntityAttributeCreationEvent.BUS.addListener(
        event -> event.put(ModEntities.BEAN_FAIRY, BeanFairy.createAttributes().build()));
    TickEvent.LevelTickEvent.Post.BUS.addListener(FertileGroundsForge::onLevelTick);
    ServerStoppedEvent.BUS.addListener(event -> FertileGrounds.onServerStopped());
    PlayerEvent.PlayerLoggedInEvent.BUS.addListener(FertileGroundsForge::onPlayerJoin);
    BlockEvent.BlockToolModificationEvent.BUS.addListener(FertileGroundsForge::onToolModify);
  }

  private static void register(final RegisterEvent event) {
    final Runnable registration = FertileGrounds.REGISTRATIONS.get(event.getRegistryKey());
    if (registration != null) {
      registration.run();
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
    // Forge maps each block state to its point of interest when the type is registered.
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

  private static void onLevelTick(final TickEvent.LevelTickEvent.Post event) {
    if (event.level() instanceof ServerLevel level) {
      FertileGrounds.onLevelTick(level);
    }
  }

  private static void onPlayerJoin(final PlayerEvent.PlayerLoggedInEvent event) {
    if (event.getEntity() instanceof ServerPlayer player) {
      FertileGrounds.onPlayerJoin(player);
    }
  }

  /** A hoe tills our dirt tiers; like vanilla dirt, only with air above. Never cancels. */
  private static boolean onToolModify(final BlockEvent.BlockToolModificationEvent event) {
    final Block tilled = ModBlocks.TILLED.get(event.getState().getBlock());
    if (tilled != null
        && event.getToolAction() == ToolActions.HOE_TILL
        && HoeItem.onlyIfAirAbove(event.getContext())) {
      event.setFinalState(tilled.defaultBlockState());
    }
    return false;
  }
}
