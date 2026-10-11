package com.fertilegrounds.client.compat.jade;

import com.fertilegrounds.block.FairyHotelBlock;
import com.fertilegrounds.block.GrowthBoostedFarmlandBlock;
import com.fertilegrounds.block.GrowthBoostedSandBlock;
import net.minecraft.world.level.block.Block;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;

/**
 * Registers the client half of the mod's Jade tooltips: the lines a player sees. Jade calls {@code
 * registerClient} only on the client, and Fabric only loads this through the {@code jade}
 * entrypoint.
 */
public class FertileGroundsJadeClientPlugin implements IWailaPlugin {

  @Override
  public void registerClient(final IWailaClientRegistration registration) {
    registration.registerBlockComponent(
        SoilChanceTooltip.INSTANCE, GrowthBoostedFarmlandBlock.class);
    registration.registerBlockComponent(SoilChanceTooltip.INSTANCE, GrowthBoostedSandBlock.class);
    registration.registerBlockComponent(PendingVisitTooltip.INSTANCE, Block.class);
    registration.registerBlockComponent(FairyHotelTooltip.INSTANCE, FairyHotelBlock.class);
  }
}
