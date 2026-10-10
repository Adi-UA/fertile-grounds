package com.fertilegrounds.gametest;

import com.mojang.authlib.GameProfile;
import io.netty.channel.embedded.EmbeddedChannel;
import java.util.UUID;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.world.level.GameType;

/** Mock players for tests that need one. */
final class TestPlayers {

  private TestPlayers() {}

  /**
   * A mock player in survival mode, joined to the server like vanilla's {@code
   * GameTestHelper.makeMockServerPlayerInLevel()}. Vanilla's is always in creative mode, where
   * items are never used up, so tests that count items use this one instead.
   */
  static ServerPlayer survival(final GameTestHelper helper) {
    final CommonListenerCookie cookie =
        CommonListenerCookie.createInitial(
            new GameProfile(UUID.randomUUID(), "test-survival-player"), false);
    final ServerPlayer player =
        new ServerPlayer(
            helper.getLevel().getServer(),
            helper.getLevel(),
            cookie.gameProfile(),
            cookie.clientInformation());
    final Connection connection = new Connection(PacketFlow.SERVERBOUND);
    new EmbeddedChannel(connection);
    helper.getLevel().getServer().getPlayerList().placeNewPlayer(connection, player, cookie);
    // Joining applies the server's default game mode, so set survival afterward; this also
    // updates the player's abilities, which decide whether items get used up.
    player.setGameMode(GameType.SURVIVAL);
    return player;
  }
}
