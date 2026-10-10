package com.fertilegrounds.gametest;

import com.fertilegrounds.entity.BeanFairy;
import com.fertilegrounds.entity.ModEntities;
import java.util.List;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.MinecraftServer;

/** Small helpers shared by the test classes. */
final class TestSupport {

  /** Leaving fairies climb 12 blocks before vanishing, above the test area, so look wider. */
  private static final double FAIRY_SEARCH_MARGIN = 20;

  private TestSupport() {}

  /** Every fairy around this test, including one flying away above it. */
  static List<BeanFairy> fairies(final GameTestHelper helper) {
    return helper
        .getLevel()
        .getEntities(
            ModEntities.BEAN_FAIRY, helper.getBounds().inflate(FAIRY_SEARCH_MARGIN), f -> true);
  }

  /** Runs a console command, such as {@code time set 13000}, with its output hidden. */
  static void command(final GameTestHelper helper, final String command) {
    final MinecraftServer server = helper.getLevel().getServer();
    server
        .getCommands()
        .performPrefixedCommand(server.createCommandSourceStack().withSuppressedOutput(), command);
  }
}
