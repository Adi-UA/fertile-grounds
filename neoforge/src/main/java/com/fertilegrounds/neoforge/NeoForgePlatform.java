package com.fertilegrounds.neoforge;

import com.fertilegrounds.platform.LevelData;
import com.fertilegrounds.platform.Platform;
import com.fertilegrounds.platform.PlayerFlag;
import com.fertilegrounds.util.ModIdsUtil;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * Saves the mod's data with NeoForge's data attachments, which store it alongside the world or
 * player. {@code ModSavedData} loads during the attachment types' registration event, which is when
 * these register.
 */
public final class NeoForgePlatform implements Platform {

  @Override
  public <T> LevelData<T> levelData(final String path, final Codec<T> codec, final T empty) {
    final AttachmentType<T> type =
        register(
            path, AttachmentType.builder(() -> empty).serialize(codec.fieldOf("value")).build());
    return new LevelData<>() {
      @Override
      public T get(final ServerLevel level) {
        return level.getData(type);
      }

      @Override
      public void set(final ServerLevel level, final T value) {
        level.setData(type, value);
      }
    };
  }

  @Override
  public PlayerFlag playerFlag(final String path) {
    final AttachmentType<Boolean> type =
        register(
            path,
            AttachmentType.builder(() -> false)
                .serialize(Codec.BOOL.fieldOf("value"))
                .copyOnDeath()
                .build());
    return new PlayerFlag() {
      @Override
      public boolean isSet(final ServerPlayer player) {
        return player.getData(type);
      }

      @Override
      public void set(final ServerPlayer player) {
        player.setData(type, true);
      }

      @Override
      public void clear(final ServerPlayer player) {
        player.removeData(type);
      }
    };
  }

  @Override
  public boolean isModLoaded(final String modId) {
    return ModList.get().isLoaded(modId);
  }

  private static <T> AttachmentType<T> register(final String path, final AttachmentType<T> type) {
    return Registry.register(NeoForgeRegistries.ATTACHMENT_TYPES, ModIdsUtil.id(path), type);
  }
}
