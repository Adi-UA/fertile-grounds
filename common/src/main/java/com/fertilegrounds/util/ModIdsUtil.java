package com.fertilegrounds.util;

import com.fertilegrounds.FertileGrounds;
import net.minecraft.resources.Identifier;

/** Builds {@link Identifier}s in the mod's namespace. */
public final class ModIdsUtil {

  private ModIdsUtil() {}

  /** The id {@code fertilegrounds:<path>}. */
  public static Identifier id(final String path) {
    return Identifier.fromNamespaceAndPath(FertileGrounds.MOD_ID, path);
  }
}
