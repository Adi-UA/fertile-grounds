package com.fertilegrounds.compat.jade;

import com.fertilegrounds.block.ChimeHotelBlock;
import com.fertilegrounds.block.GrowthBoostedFarmlandBlock;
import com.fertilegrounds.block.GrowthBoostedSandBlock;
import net.minecraft.world.level.block.Block;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;

/**
 * Registers the server half of the mod's Jade tooltips: the data only the server knows, sent for
 * the block a player looks at. Fabric only loads this through the {@code jade} entrypoint, so no
 * Jade class loads when Jade isn't installed. The client half is {@code
 * FertileGroundsJadeClientPlugin}.
 */
public class FertileGroundsJadePlugin implements IWailaPlugin {

  @Override
  public void register(final IWailaCommonRegistration registration) {
    registration.registerBlockDataProvider(
        SoilChanceData.INSTANCE, GrowthBoostedFarmlandBlock.class);
    registration.registerBlockDataProvider(SoilChanceData.INSTANCE, GrowthBoostedSandBlock.class);
    // Any block can be a fairy crop, so this one asks for data only for blocks in the tag.
    registration.registerBlockDataProvider(PendingVisitData.INSTANCE, Block.class);
    registration.registerBlockDataProvider(ChimeFieldData.INSTANCE, ChimeHotelBlock.class);
  }
}
