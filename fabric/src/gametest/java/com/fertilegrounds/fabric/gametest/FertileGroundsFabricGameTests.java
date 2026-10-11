package com.fertilegrounds.fabric.gametest;

import com.fertilegrounds.gametest.TestCatalog;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

/**
 * Registers every in-world test function on Fabric. The test instances that run them come from the
 * data files {@link TestCatalog} writes, which Fabric API's test server loads like any data.
 */
public class FertileGroundsFabricGameTests implements ModInitializer {
  @Override
  public void onInitialize() {
    TestCatalog.functions()
        .forEach((id, test) -> Registry.register(BuiltInRegistries.TEST_FUNCTION, id, test));
  }
}
