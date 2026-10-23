package com.fertilegrounds.gametest;

import com.fertilegrounds.entity.BeanFairy;
import com.fertilegrounds.entity.ModEntities;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** Small helpers shared by the test classes. */
final class TestSupport {

  /** Leaving fairies climb 12 blocks before vanishing, above the test area, so look higher. */
  private static final double FAIRY_SEARCH_HEIGHT = 20;

  /** Less than the 5-block gap between tests, so the next test's fairy stays out of the search. */
  private static final double FAIRY_SEARCH_SIDE = 4;

  private TestSupport() {}

  /** Every fairy around this test, including one flying away above it. */
  static List<BeanFairy> fairies(final GameTestHelper helper) {
    return helper
        .getLevel()
        .getEntities(
            ModEntities.BEAN_FAIRY,
            helper.getBounds().inflate(FAIRY_SEARCH_SIDE, FAIRY_SEARCH_HEIGHT, FAIRY_SEARCH_SIDE),
            f -> true);
  }

  /** Runs a console command, such as {@code time set 13000}, with its output hidden. */
  static void command(final GameTestHelper helper, final String command) {
    final MinecraftServer server = helper.getLevel().getServer();
    server
        .getCommands()
        .performPrefixedCommand(server.createCommandSourceStack().withSuppressedOutput(), command);
  }

  /**
   * Stands a mock player in the test so its chunk gets random ticks, which Minecraft 1.21.1 only
   * runs within 128 blocks of a player. The test calls {@link TestPlayer#leave()} once it passes.
   */
  static TestPlayer randomTickWatcher(final GameTestHelper helper) {
    final TestPlayer player = TestPlayer.join(helper).at(helper, 0.5, 1, 0.5);
    // A real player's movement packets tell the chunk map where it is; a mock player has none.
    helper.getLevel().getChunkSource().move(player);
    return player;
  }

  /** Plants young wheat at {@code pos} on farmland. */
  static void plantWheat(final GameTestHelper helper, final BlockPos pos) {
    helper.setBlock(pos.below(), Blocks.FARMLAND);
    helper.setBlock(pos, Blocks.WHEAT);
  }

  /** Fails unless the block at {@code pos} is exactly {@code expected}, properties included. */
  static void assertState(
      final GameTestHelper helper, final BlockPos pos, final BlockState expected) {
    helper.assertBlockState(pos, state -> state == expected, () -> "expected " + expected);
  }
}
