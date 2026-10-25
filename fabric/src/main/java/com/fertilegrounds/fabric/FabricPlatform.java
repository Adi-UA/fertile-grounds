package com.fertilegrounds.fabric;

import com.fertilegrounds.platform.LevelData;
import com.fertilegrounds.platform.Platform;
import com.fertilegrounds.platform.PlayerFlag;
import com.fertilegrounds.util.ModIdsUtil;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/**
 * Saves the mod's data with Fabric API's data attachments on the world or player. Saved worlds hold
 * these attachments, so their ids never change.
 */
public final class FabricPlatform implements Platform {

  @Override
  public <T> LevelData<T> levelData(final String path, final Codec<T> codec, final T empty) {
    final AttachmentType<T> type = AttachmentRegistry.createPersistent(ModIdsUtil.id(path), codec);
    return new LevelData<>() {
      @Override
      public T get(final ServerLevel level) {
        return level.getAttachedOrElse(type, empty);
      }

      @Override
      public void set(final ServerLevel level, final T value) {
        level.setAttached(type, value);
      }
    };
  }

  @Override
  public PlayerFlag playerFlag(final String path) {
    final AttachmentType<Boolean> type =
        AttachmentRegistry.create(
            ModIdsUtil.id(path), builder -> builder.persistent(Codec.BOOL).copyOnDeath());
    return new PlayerFlag() {
      @Override
      public boolean isSet(final ServerPlayer player) {
        return player.hasAttached(type);
      }

      @Override
      public void set(final ServerPlayer player) {
        player.setAttached(type, true);
      }

      @Override
      public void clear(final ServerPlayer player) {
        player.removeAttached(type);
      }
    };
  }

  @Override
  public boolean isModLoaded(final String modId) {
    return FabricLoader.getInstance().isModLoaded(modId);
  }
}
