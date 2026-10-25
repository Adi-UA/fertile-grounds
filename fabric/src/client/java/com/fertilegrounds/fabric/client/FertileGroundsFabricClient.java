package com.fertilegrounds.fabric.client;

import com.fertilegrounds.block.ModBlocks;
import com.fertilegrounds.client.BeanFairyModel;
import com.fertilegrounds.client.BeanFairyRenderer;
import com.fertilegrounds.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.entity.EntityRenderers;

/**
 * Starts the mod's client half on Fabric: the Bean Fairy's model and renderer, and the hotels'
 * cutout layer (NeoForge and Forge read it from the models' {@code render_type}).
 */
public class FertileGroundsFabricClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    EntityModelLayerRegistry.registerModelLayer(
        BeanFairyModel.LAYER, BeanFairyModel::createBodyLayer);
    EntityRenderers.register(ModEntities.BEAN_FAIRY, BeanFairyRenderer::new);
    // This Minecraft version draws blocks solid unless told otherwise, so see-through pixels turn
    // black.
    BlockRenderLayerMap.putBlocks(
        ChunkSectionLayer.CUTOUT,
        ModBlocks.EMPTY_FAIRY_HOTEL,
        ModBlocks.FAIRY_HOTEL,
        ModBlocks.CHIME_FAIRY_HOTEL);
  }
}
