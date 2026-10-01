package com.fertilegrounds.client;

import com.fertilegrounds.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class FertileGroundsClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    EntityModelLayerRegistry.registerModelLayer(
        BeanFairyModel.LAYER, BeanFairyModel::createBodyLayer);
    EntityRendererRegistry.register(ModEntities.BEAN_FAIRY, BeanFairyRenderer::new);
  }
}
