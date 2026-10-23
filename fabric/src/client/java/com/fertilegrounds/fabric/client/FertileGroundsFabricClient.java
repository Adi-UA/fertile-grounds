package com.fertilegrounds.fabric.client;

import com.fertilegrounds.client.BeanFairyModel;
import com.fertilegrounds.client.BeanFairyRenderer;
import com.fertilegrounds.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

/** Starts the mod's client half on Fabric: the Bean Fairy's model and renderer. */
public class FertileGroundsFabricClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    EntityModelLayerRegistry.registerModelLayer(
        BeanFairyModel.LAYER, BeanFairyModel::createBodyLayer);
    EntityRendererRegistry.register(ModEntities.BEAN_FAIRY, BeanFairyRenderer::new);
  }
}
