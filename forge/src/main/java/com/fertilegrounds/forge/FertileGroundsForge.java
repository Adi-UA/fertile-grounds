package com.fertilegrounds.forge;

import com.fertilegrounds.FertileGrounds;
import com.fertilegrounds.block.ModBlocks;
import com.fertilegrounds.entity.BeanFairy;
import com.fertilegrounds.entity.ModEntities;
import com.fertilegrounds.hotel.FairyHotelLookup;
import com.fertilegrounds.item.ModItemGroups;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.Direction;
import net.minecraft.core.component.BlockTransformer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.component.BlockTransformers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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
    PlayerInteractEvent.RightClickBlock.BUS.addListener(FertileGroundsForge::onRightClickBlock);
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

  /** Tills our dirt when a hoe (any item using vanilla's hoe transformer) is used on it. */
  private static boolean onRightClickBlock(final PlayerInteractEvent.RightClickBlock event) {
    final var transformer = event.getItemStack().get(DataComponents.BLOCK_TRANSFORMER);
    if (transformer == null
        || !transformer.is(BlockTransformers.HOE)
        || !ModBlocks.TILLED.containsKey(
            event.getLevel().getBlockState(event.getPos()).getBlock())) {
      return false;
    }
    final InteractionResult result =
        Tilling.TRANSFORMER.transformBlock(
            new UseOnContext(event.getEntity(), event.getHand(), event.getHitVec()));
    if (!result.consumesAction()) {
      return false;
    }
    event.setCancellationResult(result);
    return true;
  }

  /**
   * Hoe tilling for both dirt tiers, with vanilla's hoe rule. Forge has no hook into the hoe's
   * block transformer, so {@link #onRightClickBlock} runs this one first. Its own class delays
   * loading the blocks it names until after registration.
   */
  private static final class Tilling {
    static final BlockTransformer TRANSFORMER =
        new BlockTransformer(
            ModBlocks.TILLED.entrySet().stream()
                .map(
                    entry ->
                        BlockTransformer.BlockTransformData.builder(
                                BlockPredicate.allOf(
                                    BlockPredicate.matchesBlocks(entry.getKey()),
                                    BlockPredicate.matchesTag(Direction.UP, BlockTags.AIR)),
                                entry.getValue())
                            .sound(SoundEvents.HOE_TILL)
                            .disallowedFaces(List.of(Direction.DOWN))
                            .build())
                .toList());
  }
}
