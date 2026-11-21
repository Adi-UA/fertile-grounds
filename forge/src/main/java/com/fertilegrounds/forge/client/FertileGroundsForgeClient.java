package com.fertilegrounds.forge.client;

import com.fertilegrounds.FertileGrounds;
import com.fertilegrounds.client.BeanFairyModel;
import com.fertilegrounds.client.BeanFairyRenderer;
import com.fertilegrounds.entity.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Starts the mod's client half on Forge: the Bean Fairy's model and renderer. */
@Mod.EventBusSubscriber(
    modid = FertileGrounds.MOD_ID,
    bus = Mod.EventBusSubscriber.Bus.MOD,
    value = Dist.CLIENT)
public final class FertileGroundsForgeClient {

  private FertileGroundsForgeClient() {}

  @SubscribeEvent
  public static void registerLayers(final EntityRenderersEvent.RegisterLayerDefinitions event) {
    event.registerLayerDefinition(BeanFairyModel.LAYER, BeanFairyModel::createBodyLayer);
  }

  @SubscribeEvent
  public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
    event.registerEntityRenderer(ModEntities.BEAN_FAIRY, BeanFairyRenderer::new);
  }
}
