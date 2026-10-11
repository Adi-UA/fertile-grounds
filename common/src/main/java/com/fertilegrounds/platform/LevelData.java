package com.fertilegrounds.platform;

import net.minecraft.server.level.ServerLevel;

/** A value saved with a world, from {@link Platform#levelData}. Treat values as immutable. */
public interface LevelData<T> {

  T get(ServerLevel level);

  void set(ServerLevel level, T value);
}
