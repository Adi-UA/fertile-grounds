package com.fertilegrounds.fairy;

import com.fertilegrounds.util.ModCriteria;
import com.fertilegrounds.util.ModGameRules;
import com.fertilegrounds.util.ModIdsUtil;
import com.fertilegrounds.util.ModTags;
import java.util.Locale;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

/**
 * Drops the occasional gift after a visit grows crops. The rules live in {@link FairyGifts}; this
 * class gathers their inputs from the world and spawns the loot.
 */
final class FairyGiftDrops {

  private static final int CROP_RADIUS = 16;

  private static final int CROP_HEIGHT = 4;

  private static final double MESSAGE_DISTANCE = 32;

  private FairyGiftDrops() {}

  static void maybeDrop(
      final ServerLevel level, final FairyVisit visit, final boolean harvestMoon) {
    if (visit.owner().isEmpty()) {
      return;
    }
    final UUID owner = visit.owner().get();
    final GameRules rules = level.getGameRules();
    final long today = level.getOverworldClockTime() / 24000;
    if (!FairyGifts.isOffCooldown(
        FairyGiftCooldowns.lastGiftDay(level, owner),
        today,
        rules.get(ModGameRules.FAIRY_GIFT_COOLDOWN_DAYS))) {
      return;
    }
    final int chance =
        FairyGifts.chance(
            visit.source(),
            rules.get(ModGameRules.FAIRY_GIFT_CHANCE_NATURAL),
            rules.get(ModGameRules.FAIRY_GIFT_CHANCE_DUST),
            FarmAttractions.around(level, visit.pos()).flowerTypes());
    if (level.getRandom().nextInt(100) >= chance) {
      return;
    }
    // Count the farm only after the roll succeeds, so most visits skip this bigger scan.
    final FairyGifts.Tier tier = FairyGifts.tier(countFairyCrops(level, visit.pos()), harvestMoon);
    drop(level, visit.pos(), tier);
    FairyGiftCooldowns.recordGift(level, owner, today);
    final ServerPlayer player = level.getServer().getPlayerList().getPlayer(owner);
    if (player != null) {
      ModCriteria.FAIRY_GIFT.trigger(player);
      if (player.level() == level
          && player.distanceToSqr(Vec3.atCenterOf(visit.pos()))
              <= MESSAGE_DISTANCE * MESSAGE_DISTANCE) {
        player.sendOverlayMessage(Component.translatable("message.fertilegrounds.fairy_gift"));
      }
    }
  }

  private static void drop(
      final ServerLevel level, final BlockPos pos, final FairyGifts.Tier tier) {
    final LootTable table =
        level
            .getServer()
            .reloadableRegistries()
            .getLootTable(
                ResourceKey.create(
                    Registries.LOOT_TABLE,
                    ModIdsUtil.id("gameplay/fairy_gift/" + tier.name().toLowerCase(Locale.ROOT))));
    final Vec3 spot = Vec3.atCenterOf(pos);
    final LootParams params =
        new LootParams.Builder(level)
            .withParameter(LootContextParams.ORIGIN, spot)
            .create(LootContextParamSets.CHEST);
    table.getRandomItems(
        params,
        stack -> {
          final ItemEntity item = new ItemEntity(level, spot.x, spot.y, spot.z, stack);
          // The same flag vanilla uses for items that must wait for a player, so it never despawns.
          item.setUnlimitedLifetime();
          level.addFreshEntity(item);
        });
    level.playSound(null, pos, SoundEvents.AMETHYST_CLUSTER_PLACE, SoundSource.BLOCKS, 1.0F, 1.3F);
    level.sendParticles(ParticleTypes.GLOW, spot.x, spot.y, spot.z, 6, 0.3, 0.3, 0.3, 0.0);
    level.sendParticles(
        ParticleTypes.END_ROD, spot.x, spot.y + 0.5, spot.z, 4, 0.2, 0.4, 0.2, 0.02);
  }

  private static int countFairyCrops(final ServerLevel level, final BlockPos center) {
    int count = 0;
    for (final BlockPos pos :
        BlockPos.betweenClosed(
            center.offset(-CROP_RADIUS, -CROP_HEIGHT, -CROP_RADIUS),
            center.offset(CROP_RADIUS, CROP_HEIGHT, CROP_RADIUS))) {
      if (level.getBlockState(pos).is(ModTags.FAIRY_CROPS)) {
        count++;
      }
    }
    return count;
  }
}
