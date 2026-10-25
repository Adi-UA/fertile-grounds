package com.fertilegrounds.gametest;

import com.fertilegrounds.util.ModIdsUtil;
import com.mojang.authlib.GameProfile;
import io.netty.channel.embedded.EmbeddedChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * A mock survival player that records the messages it receives. It joins the server like vanilla's
 * {@code GameTestHelper.makeMockServerPlayerInLevel()}, which is always in creative mode, where
 * items are never used up.
 */
public final class TestPlayer extends ServerPlayer {

  /** Each loader's test mod can ready a mock connection here before the player joins. */
  private static Consumer<Connection> connectionSetup = connection -> {};

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
    return join(helper, new GameProfile(UUID.randomUUID(), "test-player"));
  }

  /**
   * Leaves, which saves this player to the world's player data, then joins again as the same player
   * loaded from that save, the way a real rejoin or server restart does.
   */
  TestPlayer rejoin(final GameTestHelper helper) {
    this.leave();
    return join(helper, this.getGameProfile());
  }

  private static TestPlayer join(final GameTestHelper helper, final GameProfile profile) {
    final CommonListenerCookie cookie = CommonListenerCookie.createInitial(profile, false);
    final TestPlayer player = new TestPlayer(helper, cookie);
    // Joining loads the player's save, if there is one, before placing them.
    final Connection connection = new Connection(PacketFlow.SERVERBOUND);
    new EmbeddedChannel(connection);
    connectionSetup.accept(connection);
    final MinecraftServer server = helper.getLevel().getServer();
    server.getPlayerList().placeNewPlayer(connection, player, cookie);
    // Joining applies the server's default game mode, so set survival afterward.
    player.setGameMode(GameType.SURVIVAL);
    return player;
  }

  /**
   * Sets how mock connections get ready. NeoForge only sends a mod's packets over a connection that
   * agreed on that mod's channels, so its test mod passes {@code configureMockConnection}.
   */
  public static void setConnectionSetup(final Consumer<Connection> setup) {
    connectionSetup = setup;
  }

  /** Stands the player at a spot relative to the test. */
  TestPlayer at(final GameTestHelper helper, final double x, final double y, final double z) {
    final Vec3 pos = helper.absoluteVec(new Vec3(x, y, z));
    this.setPos(pos.x, pos.y, pos.z);
    return this;
  }

  /**
   * Right-clicks a block with the main-hand item through the same server call a real click reaches,
   * so it also fires vanilla's item-used-on-block advancement trigger.
   */
  InteractionResult useHeldItemOn(final GameTestHelper helper, final BlockPos relativePos) {
    final BlockPos pos = helper.absolutePos(relativePos);
    return this.gameMode.useItemOn(
        this,
        helper.getLevel(),
        this.getMainHandItem(),
        InteractionHand.MAIN_HAND,
        new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false));
  }

  /** Whether this player has earned the mod advancement {@code fertilegrounds:<path>}. */
  boolean hasAdvancement(final String path) {
    final AdvancementHolder advancement =
        this.level().getServer().getAdvancements().get(ModIdsUtil.id(path));
    return advancement != null && this.getAdvancements().getOrStartProgress(advancement).isDone();
  }

  /** How many chat messages use the given translation key. */
  long chatCount(final String translationKey) {
    return this.chatMessages.stream().filter(message -> hasKey(message, translationKey)).count();
  }

  /** How many overlay messages use the given translation key. */
  long overlayCount(final String translationKey) {
    return this.overlayMessages.stream().filter(message -> hasKey(message, translationKey)).count();
  }

  /**
   * Ticks this player every test tick, for checks on things a player's tick runs, such as each
   * carried item's {@code inventoryTick}. The server ticks a real player through its network
   * connection, which a mock player's embedded one isn't part of.
   */
  TestPlayer tickedBy(final GameTestHelper helper) {
    helper.onEachTick(this::doTick);
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

  /** Whether the latest overlay message uses the given translation key, whatever its arguments. */
  boolean lastOverlayIs(final String translationKey) {
    return !this.overlayMessages.isEmpty()
        && hasKey(this.overlayMessages.getLast(), translationKey);
  }

  private static boolean hasKey(final Component message, final String translationKey) {
    return message.getContents() instanceof TranslatableContents contents
        && contents.getKey().equals(translationKey);
  }
}
