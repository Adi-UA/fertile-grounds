package com.fertilegrounds.util;

import com.fertilegrounds.item.GuideBookRecipeSerializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

/** The mod's recipe types, registered like vanilla's {@code RecipeSerializer}s. */
public final class ModRecipeSerializers {

  /** The Fertile Grounds Guide, crafted from a Book and Bone Meal. */
  public static final RecipeSerializer<ShapelessRecipe> GUIDE_BOOK =
      Registry.register(
          BuiltInRegistries.RECIPE_SERIALIZER,
          ModIdsUtil.id("guide_book"),
          new GuideBookRecipeSerializer());

  private ModRecipeSerializers() {}

  /** Loads this class so the static fields above register during mod startup. */
  public static void register() {}
}
