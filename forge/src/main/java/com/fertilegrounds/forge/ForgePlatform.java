package com.fertilegrounds.forge;

import com.fertilegrounds.FertileGrounds;
import com.fertilegrounds.mixin.IntegerRuleInvoker;
import com.fertilegrounds.platform.LevelData;
import com.fertilegrounds.platform.Platform;
import com.fertilegrounds.platform.PlayerFlag;
import com.mojang.serialization.Codec;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.fml.ModList;

/**
 * Saves the mod's data as vanilla saved data, since Forge has no data attachments. Each value is a
 * file in the world's data folder, per dimension; the guide flag is the set of players who got the
 * book, kept with the overworld so it follows a player through death and between dimensions.
 */
public final class ForgePlatform implements Platform {

  @Override
  public <T> LevelData<T> levelData(final String path, final Codec<T> codec, final T empty) {
    final SavedData.Factory<Value<T>> factory =
        new SavedData.Factory<>(
            () -> new Value<>(codec, empty),
            (tag, registries) -> Value.load(codec, empty, tag, registries),
            null);
    final String name = FertileGrounds.MOD_ID + "_" + path;
    return new LevelData<>() {
      @Override
      public T get(final ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(factory, name).value;
      }

      @Override
      public void set(final ServerLevel level, final T value) {
        final Value<T> saved = level.getDataStorage().computeIfAbsent(factory, name);
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
  public GameRules.Key<GameRules.IntegerValue> intRule(
      final String name,
      final GameRules.Category category,
      final int defaultValue,
      final int min,
      final int max) {
    return GameRules.register(
        name,
        category,
        IntegerRuleInvoker.fertilegrounds$create(defaultValue, min, max, (server, rule) -> {}));
  }

  @Override
  public GameRules.Key<GameRules.BooleanValue> boolRule(
      final String name, final GameRules.Category category, final boolean defaultValue) {
    return GameRules.register(name, category, GameRules.BooleanValue.create(defaultValue));
  }

  @Override
  public SpawnEggItem spawnEgg(
      final Supplier<? extends EntityType<? extends Mob>> type, final Item.Properties properties) {
    return new ForgeSpawnEggItem(type, 0xFFFFFF, 0xFFFFFF, properties);
  }

  @Override
  public boolean isModLoaded(final String modId) {
    return ModList.get().isLoaded(modId);
  }

  private static ServerLevel overworld(final ServerPlayer player) {
    return player.server.overworld();
  }

  /** One saved value, stored with its codec; vanilla saves it when marked dirty. */
  private static final class Value<T> extends SavedData {
    private final Codec<T> codec;
    private T value;

    private Value(final Codec<T> codec, final T value) {
      this.codec = codec;
      this.value = value;
    }

    private static <T> Value<T> load(
        final Codec<T> codec,
        final T empty,
        final CompoundTag tag,
        final HolderLookup.Provider registries) {
      if (!tag.contains("value")) {
        return new Value<>(codec, empty);
      }
      final T value =
          codec
              .parse(registries.createSerializationContext(NbtOps.INSTANCE), tag.get("value"))
              .resultOrPartial(FertileGrounds.LOGGER::error)
              .orElse(empty);
      return new Value<>(codec, value);
    }

    @Override
    public CompoundTag save(final CompoundTag tag, final HolderLookup.Provider registries) {
      this.codec
          .encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), this.value)
          .resultOrPartial(FertileGrounds.LOGGER::error)
          .ifPresent(encoded -> tag.put("value", encoded));
      return tag;
    }
  }
}
