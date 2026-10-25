package com.fertilegrounds.forge.gametest;

import com.fertilegrounds.gametest.TestCatalog;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

/**
 * Registers every in-world test function on Forge, as a dev-only mod of its own. The test instances
 * that run the functions come from the data files {@link TestCatalog} writes.
 */
@Mod("fertilegrounds_gametest")
public final class FertileGroundsForgeGameTests {

  public FertileGroundsForgeGameTests(final FMLJavaModLoadingContext context) {
    RegisterEvent.getBus(context.getModBusGroup())
        .addListener(
            event ->
                event.register(
                    Registries.TEST_FUNCTION,
                    helper -> TestCatalog.functions().forEach(helper::register)));
  }
}
