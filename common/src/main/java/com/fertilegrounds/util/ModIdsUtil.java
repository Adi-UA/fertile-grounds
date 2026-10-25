package com.fertilegrounds.util;

import com.fertilegrounds.FertileGrounds;
import net.minecraft.resources.ResourceLocation;

/** Builds {@link ResourceLocation}s in the mod's namespace. */
public final class ModIdsUtil {

  private ModIdsUtil() {}

  /** The id {@code fertilegrounds:<path>}. */
  public static ResourceLocation id(final String path) {
    return ResourceLocation.fromNamespaceAndPath(FertileGrounds.MOD_ID, path);
  }
}
