package com.fertilegrounds.forge.gametest;

import com.fertilegrounds.gametest.TestCatalog;
import com.fertilegrounds.gametest.TestEnvironments;
import net.minecraftforge.event.RegisterGameTestsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Registers every in-world test with vanilla's test registry on Forge, as a dev-only mod of its
 * own.
 */
@Mod("fertilegrounds_gametest")
public final class FertileGroundsForgeGameTests {

  public FertileGroundsForgeGameTests(final FMLJavaModLoadingContext context) {
    context
        .getModEventBus()
        .addListener(
            (final RegisterGameTestsEvent event) -> {
              event.register(TestCatalog.class);
              event.register(TestEnvironments.class);
            });
  }
}
