package com.fertilegrounds.item;

import com.fertilegrounds.fairy.FairyVisitScheduler;
import com.fertilegrounds.util.CropGrowthUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/**
 * Sprinkled on a growing crop, Fairy Dust guarantees that a Bean Fairy visits it. Fairies only come
 * at night: within half a minute if it's already dark, otherwise as soon as night falls.
 */
public class FairyDustItem extends Item {

  public FairyDustItem(final Properties properties) {
    super(properties);
  }

  @Override
  public InteractionResult useOn(final UseOnContext context) {
    final Level level = context.getLevel();
    final BlockPos pos = context.getClickedPos();
    if (!CropGrowthUtil.canGrow(level, pos)) {
      return InteractionResult.PASS;
    }
    // The client only predicts the arm swing; the server decides what actually happens.
    if (!(level instanceof ServerLevel serverLevel)) {
      return InteractionResult.SUCCESS;
    }

    final Player player = context.getPlayer();
    if (level.dimension() != Level.OVERWORLD) {
      tell(player, "message.fertilegrounds.fairy_dust.no_night");
      return InteractionResult.FAIL;
    }
    if (!FairyVisitScheduler.requestVisit(serverLevel, pos)) {
      tell(player, "message.fertilegrounds.fairy_dust.already_coming");
      return InteractionResult.FAIL;
    }

    context.getItemInHand().consume(1, player);
    serverLevel.sendParticles(
        ParticleTypes.END_ROD,
        pos.getX() + 0.5,
        pos.getY() + 0.5,
        pos.getZ() + 0.5,
        10,
        0.3,
        0.3,
        0.3,
        0.02);
    level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0F, 1.2F);
    tell(
        player,
        level.isDarkOutside()
            ? "message.fertilegrounds.fairy_dust.coming_now"
            : "message.fertilegrounds.fairy_dust.coming_tonight");
    return InteractionResult.SUCCESS;
  }

  /** Shows a short message above the hotbar. Dispensers have no player, so this skips them. */
  private static void tell(final Player player, final String translationKey) {
    if (player != null) {
      player.displayClientMessage(Component.translatable(translationKey), true);
    }
  }
}
