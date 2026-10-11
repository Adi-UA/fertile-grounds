package com.fertilegrounds.fabric.client;

import com.fertilegrounds.client.BeanFairyModel;
import com.fertilegrounds.client.BeanFairyRenderer;
import com.fertilegrounds.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.renderer.entity.EntityRenderers;

/** Starts the mod's client half on Fabric: the Bean Fairy's model and renderer. */
public class FertileGroundsFabricClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    ModelLayerRegistry.registerModelLayer(BeanFairyModel.LAYER, BeanFairyModel::createBodyLayer);
    EntityRenderers.register(ModEntities.BEAN_FAIRY, BeanFairyRenderer::new);
  }
}
