package com.fertilegrounds.compat.jade;

import com.fertilegrounds.util.ModIdsUtil;
import net.minecraft.resources.ResourceLocation;

/**
 * The ids of the mod's Jade tooltips, shared by the server data each one sends and the client line
 * that shows it. Each id also names the tooltip's switch in Jade's settings.
 */
public final class JadeIds {

  public static final ResourceLocation SOIL_CHANCE = ModIdsUtil.id("soil_chance");

  public static final ResourceLocation PENDING_VISIT = ModIdsUtil.id("pending_visit");

  public static final ResourceLocation FAIRY_HOTEL = ModIdsUtil.id("fairy_hotel");

  private JadeIds() {}
}
