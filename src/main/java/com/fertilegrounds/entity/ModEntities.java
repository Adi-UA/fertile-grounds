package com.fertilegrounds.entity;

import com.fertilegrounds.util.ModIdsUtil;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

/** Registers the mobs this mod adds. */
public final class ModEntities {

  private static final ResourceKey<EntityType<?>> BEAN_FAIRY_KEY =
      ResourceKey.create(Registries.ENTITY_TYPE, ModIdsUtil.id("bean_fairy"));

  /** Small hitbox to match the 10-pixel-wide model; no loot table since it drops nothing. */
  public static final EntityType<BeanFairy> BEAN_FAIRY =
      Registry.register(
          BuiltInRegistries.ENTITY_TYPE,
          BEAN_FAIRY_KEY,
          EntityType.Builder.of(BeanFairy::new, MobCategory.MISC)
              .sized(0.6F, 0.4F)
              .eyeHeight(0.2F)
              .noLootTable()
              .clientTrackingRange(8)
              .build(BEAN_FAIRY_KEY));

  private ModEntities() {}

  /** Every mob needs its attributes (health, speed) registered or the game crashes on spawn. */
  public static void register() {
    FabricDefaultAttributeRegistry.register(BEAN_FAIRY, BeanFairy.createAttributes());
  }
}
