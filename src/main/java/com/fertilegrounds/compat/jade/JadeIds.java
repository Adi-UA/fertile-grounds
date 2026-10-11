package com.fertilegrounds.compat.jade;

import com.fertilegrounds.util.ModIdsUtil;
import net.minecraft.resources.Identifier;

/**
 * The ids of the mod's Jade tooltips, shared by the server data each one sends and the client line
 * that shows it. Each id also names the tooltip's switch in Jade's settings.
 */
public final class JadeIds {

  public static final Identifier SOIL_CHANCE = ModIdsUtil.id("soil_chance");

  public static final Identifier PENDING_VISIT = ModIdsUtil.id("pending_visit");

  public static final Identifier FAIRY_HOTEL = ModIdsUtil.id("fairy_hotel");

  private JadeIds() {}
}
