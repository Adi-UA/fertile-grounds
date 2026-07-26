package com.fertilegrounds.util;

import com.fertilegrounds.FertileGrounds;
import net.minecraft.resources.ResourceLocation;

/**
 * Builds namespaced {@link ResourceLocation}s for this mod's own registry
 * entries (blocks, items, loot tables, etc.), so the mod's namespace string
 * only has to be correct in one place.
 */
public final class ModIdsUtil {

    private ModIdsUtil() {
    }

    /**
     * @param path the registry path, e.g. {@code "enriched_dirt"}
     * @return a {@link ResourceLocation} in this mod's namespace, e.g. {@code fertilegrounds:enriched_dirt}
     */
    public static ResourceLocation id(final String path) {
        return new ResourceLocation(FertileGrounds.MOD_ID, path);
    }
}
