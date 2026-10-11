package com.fertilegrounds.forge;

import com.fertilegrounds.platform.LevelData;
import com.fertilegrounds.platform.Platform;
import com.fertilegrounds.platform.PlayerFlag;
import com.fertilegrounds.util.ModIdsUtil;
import com.mojang.serialization.Codec;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraftforge.fml.ModList;

/**
 * Saves the mod's data as vanilla saved data, since Forge has no data attachments. Each value is a
 * file in the world's data folder, per dimension; the guide flag is the set of players who got the
 * book, kept with the overworld so it follows a player through death and between dimensions.
 */
public final class ForgePlatform implements Platform {

  @Override
  public <T> LevelData<T> levelData(final String path, final Codec<T> codec, final T empty) {
    final SavedDataType<Value<T>> type =
        new SavedDataType<>(
            ModIdsUtil.id(path),
            () -> new Value<>(empty),
            codec.xmap(Value::new, value -> value.value),
            null);
    return new LevelData<>() {
      @Override
      public T get(final ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(type).value;
      }

      @Override
      public void set(final ServerLevel level, final T value) {
        final Value<T> saved = level.getDataStorage().computeIfAbsent(type);
        saved.value = value;
        saved.setDirty();
      }
    };
  }

  @Override
  public PlayerFlag playerFlag(final String path) {
    final LevelData<List<UUID>> players =
        levelData(path, UUIDUtil.STRING_CODEC.listOf(), List.of());
    return new PlayerFlag() {
      @Override
      public boolean isSet(final ServerPlayer player) {
        return players.get(overworld(player)).contains(player.getUUID());
      }

      @Override
      public void set(final ServerPlayer player) {
        update(player, true);
      }

      @Override
      public void clear(final ServerPlayer player) {
        update(player, false);
      }

      private void update(final ServerPlayer player, final boolean set) {
        final ServerLevel overworld = overworld(player);
        final Set<UUID> updated = new HashSet<>(players.get(overworld));
        if (set ? updated.add(player.getUUID()) : updated.remove(player.getUUID())) {
          players.set(overworld, List.copyOf(updated));
        }
      }
    };
  }

  @Override
  public boolean isModLoaded(final String modId) {
    return ModList.isLoaded(modId);
  }

  private static ServerLevel overworld(final ServerPlayer player) {
    return player.level().getServer().overworld();
  }

  /** One saved value; vanilla saves it when marked dirty. */
  private static final class Value<T> extends SavedData {
    private T value;

    private Value(final T value) {
      this.value = value;
    }
  }
}
