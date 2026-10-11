package com.fertilegrounds.neoforge.client;

import com.fertilegrounds.FertileGrounds;
import com.fertilegrounds.client.BeanFairyModel;
import com.fertilegrounds.client.BeanFairyRenderer;
import com.fertilegrounds.entity.ModEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/** Starts the mod's client half on NeoForge: the Bean Fairy's model and renderer. */
@Mod(value = FertileGrounds.MOD_ID, dist = Dist.CLIENT)
public final class FertileGroundsNeoForgeClient {

  public FertileGroundsNeoForgeClient(final IEventBus modBus) {
    modBus.addListener(
        (final EntityRenderersEvent.RegisterLayerDefinitions event) ->
            event.registerLayerDefinition(BeanFairyModel.LAYER, BeanFairyModel::createBodyLayer));
    modBus.addListener(
        (final EntityRenderersEvent.RegisterRenderers event) ->
            event.registerEntityRenderer(ModEntities.BEAN_FAIRY, BeanFairyRenderer::new));
  }
}
