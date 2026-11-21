package com.fertilegrounds.client.compat.jade;

import com.fertilegrounds.block.FairyHotelBlock;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

/**
 * Registers the mod's Jade tooltip lines. This Jade version sends server data only for block
 * entities, so only lines the client already knows show. Jade calls {@code registerClient} only on
 * the client. Fabric loads this through the {@code jade} entrypoint and Forge's Jade finds it by
 * its {@link WailaPlugin} annotation, so no Jade class loads when Jade isn't installed.
 */
@WailaPlugin
public class FertileGroundsJadeClientPlugin implements IWailaPlugin {

  @Override
  public void registerClient(final IWailaClientRegistration registration) {
    registration.registerBlockComponent(FairyHotelTooltip.INSTANCE, FairyHotelBlock.class);
  }
}
