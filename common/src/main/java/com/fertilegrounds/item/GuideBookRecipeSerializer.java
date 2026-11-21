package com.fertilegrounds.item;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

/**
 * Reads the guide's recipe, {@code recipes/guide_book.json}, as a vanilla shapeless recipe whose
 * result is the full guide. This Minecraft version's recipe JSON can't describe a written book's
 * pages. The loaded recipe is a plain shapeless one, so it syncs to players and shows in the recipe
 * book like any other.
 */
public final class GuideBookRecipeSerializer implements RecipeSerializer<ShapelessRecipe> {

  @Override
  public ShapelessRecipe fromJson(final ResourceLocation id, final JsonObject json) {
    final ShapelessRecipe recipe = RecipeSerializer.SHAPELESS_RECIPE.fromJson(id, json);
    return new ShapelessRecipe(
        id, recipe.getGroup(), recipe.category(), GuideBook.create(), recipe.getIngredients());
  }

  @Override
  public ShapelessRecipe fromNetwork(final ResourceLocation id, final FriendlyByteBuf buffer) {
    return RecipeSerializer.SHAPELESS_RECIPE.fromNetwork(id, buffer);
  }

  @Override
  public void toNetwork(final FriendlyByteBuf buffer, final ShapelessRecipe recipe) {
    RecipeSerializer.SHAPELESS_RECIPE.toNetwork(buffer, recipe);
  }
}
