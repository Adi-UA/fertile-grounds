package com.fertilegrounds.gametest;

import com.mojang.authlib.GameProfile;
import io.netty.channel.embedded.EmbeddedChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;

/**
 * A mock survival player that records the messages it receives. It joins the server like vanilla's
 * {@code GameTestHelper.makeMockServerPlayerInLevel()}, which is always in creative mode, where
 * items are never used up.
 */
final class TestPlayer extends ServerPlayer {

  private final List<Component> overlayMessages = new ArrayList<>();

  private final List<Component> chatMessages = new ArrayList<>();

  private TestPlayer(final GameTestHelper helper, final CommonListenerCookie cookie) {
    super(
        helper.getLevel().getServer(),
        helper.getLevel(),
        cookie.gameProfile(),
        cookie.clientInformation());
  }

  /**
   * Joins a new survival player to the server. Vanilla's test cleanup removes every entity except
   * players, so each test calls {@link #leave()} when done.
   */
  static TestPlayer join(final GameTestHelper helper) {
    final CommonListenerCookie cookie =
        CommonListenerCookie.createInitial(
            new GameProfile(UUID.randomUUID(), "test-player"), false);
    final TestPlayer player = new TestPlayer(helper, cookie);
    // Fully qualified: inside ServerPlayer, the plain name means WaypointTransmitter.Connection.
    final net.minecraft.network.Connection connection =
        new net.minecraft.network.Connection(PacketFlow.SERVERBOUND);
    new EmbeddedChannel(connection);
    helper.getLevel().getServer().getPlayerList().placeNewPlayer(connection, player, cookie);
    // Joining applies the server's default game mode, so set survival afterward; this also
    // updates the player's abilities, which decide whether items get used up.
    player.setGameMode(GameType.SURVIVAL);
    return player;
  }

  /** Stands the player at a spot relative to the test. */
  TestPlayer at(final GameTestHelper helper, final double x, final double y, final double z) {
    final Vec3 pos = helper.absoluteVec(new Vec3(x, y, z));
    this.setPos(pos.x, pos.y, pos.z);
    return this;
  }

  void leave() {
    this.level().getServer().getPlayerList().remove(this);
  }

  @Override
  public void sendSystemMessage(final Component message, final boolean overlay) {
    (overlay ? this.overlayMessages : this.chatMessages).add(message);
    super.sendSystemMessage(message, overlay);
  }

  /** Overlay messages, the short ones shown above the hotbar, oldest first. */
  List<Component> overlayMessages() {
    return this.overlayMessages;
  }

  List<Component> chatMessages() {
    return this.chatMessages;
  }

  /** Whether the latest overlay message is the given translation. */
  boolean lastOverlayIs(final String translationKey) {
    return !this.overlayMessages.isEmpty()
        && this.overlayMessages.getLast().equals(Component.translatable(translationKey));
  }
}
