package com.fertilegrounds.util;

import com.fertilegrounds.fairy.FairyGiftCooldowns;
import com.fertilegrounds.fairy.FairyVisit;
import com.fertilegrounds.grove.GroveJob;
import com.fertilegrounds.platform.LevelData;
import com.fertilegrounds.platform.Platform;
import com.fertilegrounds.platform.PlayerFlag;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Everything the mod saves on worlds and players. Each loader stores these its own way (Fabric and
 * NeoForge as data attachments, Forge as saved data). The ids are part of every saved world, so
 * they never change.
 */
public final class ModSavedData {

  /** Fairy visits promised for tonight, see {@link com.fertilegrounds.fairy.FairyVisits}. */
  public static final LevelData<List<FairyVisit>> PENDING_FAIRY_VISITS =
      Platform.INSTANCE.levelData("pending_fairy_visits", FairyVisit.CODEC.listOf(), List.of());

  /** Each player's last gift day, see {@link FairyGiftCooldowns}. */
  public static final LevelData<Map<UUID, Long>> FAIRY_GIFT_COOLDOWNS =
      Platform.INSTANCE.levelData("fairy_gift_cooldowns", FairyGiftCooldowns.CODEC, Map.of());

  /** Fairy Groves still being built, see {@link com.fertilegrounds.grove.GroveBuilder}. */
  public static final LevelData<List<GroveJob>> GROVE_JOBS =
      Platform.INSTANCE.levelData("grove_jobs", GroveJob.CODEC.listOf(), List.of());

  /**
   * Whether a player already got the guide book, see {@link com.fertilegrounds.item.GuideBookGift}.
   */
  public static final PlayerFlag RECEIVED_GUIDE = Platform.INSTANCE.playerFlag("received_guide");

  private ModSavedData() {}

  /** Loads this class so each loader creates its storage before any world loads. */
  public static void register() {}
}
