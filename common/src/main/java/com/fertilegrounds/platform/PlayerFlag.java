package com.fertilegrounds.platform;

import net.minecraft.server.level.ServerPlayer;

/** A yes-or-no value saved on a player, from {@link Platform#playerFlag}. */
public interface PlayerFlag {

  boolean isSet(ServerPlayer player);

  void set(ServerPlayer player);

  void clear(ServerPlayer player);
}
