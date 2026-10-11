package com.fertilegrounds.neoforge.gametest;

import com.fertilegrounds.gametest.TestCatalog;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.RegisterEvent;

/**
 * Registers every in-world test function on NeoForge, as a dev-only mod of its own. The test
 * instances that run the functions come from the data files {@link TestCatalog} writes.
 */
@Mod("fertilegrounds_gametest")
public final class FertileGroundsNeoForgeGameTests {

  public FertileGroundsNeoForgeGameTests(final IEventBus modBus) {
    modBus.addListener(
        (final RegisterEvent event) ->
            event.register(
                Registries.TEST_FUNCTION,
                helper -> TestCatalog.functions().forEach(helper::register)));
  }
}
