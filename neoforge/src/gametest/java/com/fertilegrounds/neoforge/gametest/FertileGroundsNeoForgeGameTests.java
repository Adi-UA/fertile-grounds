package com.fertilegrounds.neoforge.gametest;

import com.fertilegrounds.gametest.TestCatalog;
import com.fertilegrounds.gametest.TestEnvironments;
import com.fertilegrounds.gametest.TestPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.network.registration.NetworkRegistry;

/** Registers every in-world test with vanilla's test registry on NeoForge, as a dev-only mod. */
@Mod("fertilegrounds_gametest")
public final class FertileGroundsNeoForgeGameTests {

  public FertileGroundsNeoForgeGameTests(final IEventBus modBus) {
    TestPlayer.setConnectionSetup(NetworkRegistry::configureMockConnection);
    modBus.addListener(
        (final RegisterGameTestsEvent event) -> {
          event.register(TestCatalog.class);
          event.register(TestEnvironments.class);
        });
  }
}
