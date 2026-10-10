package com.fertilegrounds.block;

import com.fertilegrounds.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * A Fairy Hotel: a lantern that hangs and sits like vanilla's. Empty and occupied hotels are two
 * blocks of this class, so each has its own item and loot table and an occupied hotel keeps its
 * fairy when broken and placed again, with no block entity or item data.
 */
public class FairyHotelBlock extends LanternBlock {

  private final boolean occupied;

  public FairyHotelBlock(final Properties properties, final boolean occupied) {
    super(properties);
    this.occupied = occupied;
  }

  public boolean isOccupied() {
    return this.occupied;
  }

  /** A Jarred Fairy used on an empty hotel moves in for good, and the bottle comes back. */
  @Override
  protected InteractionResult useItemOn(
      final ItemStack stack,
      final BlockState state,
      final Level level,
      final BlockPos pos,
      final Player player,
      final InteractionHand hand,
      final BlockHitResult hitResult) {
    if (this.occupied || !stack.is(ModItems.JARRED_FAIRY)) {
      return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
    if (level instanceof ServerLevel serverLevel) {
      level.setBlockAndUpdate(pos, ModBlocks.FAIRY_HOTEL.withPropertiesOf(state));
      player.setItemInHand(
          hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
      level.playSound(null, pos, SoundEvents.ALLAY_ITEM_GIVEN, SoundSource.BLOCKS, 1.0F, 1.0F);
      level.playSound(
          null, pos, SoundEvents.AMETHYST_CLUSTER_PLACE, SoundSource.BLOCKS, 1.0F, 1.2F);
      serverLevel.sendParticles(
          ParticleTypes.HEART,
          pos.getX() + 0.5,
          pos.getY() + 0.8,
          pos.getZ() + 0.5,
          3,
          0.3,
          0.2,
          0.3,
          0.0);
      serverLevel.sendParticles(
          ParticleTypes.END_ROD,
          pos.getX() + 0.5,
          pos.getY() + 0.5,
          pos.getZ() + 0.5,
          8,
          0.2,
          0.2,
          0.2,
          0.05);
    }
    return InteractionResult.SUCCESS;
  }

  /** An occupied hotel glows softly and its fairy hums now and then. Runs on the client only. */
  @Override
  public void animateTick(
      final BlockState state, final Level level, final BlockPos pos, final RandomSource random) {
    if (!this.occupied) {
      return;
    }
    if (random.nextInt(5) == 0) {
      level.addParticle(
          ParticleTypes.GLOW,
          pos.getX() + 0.3 + random.nextDouble() * 0.4,
          pos.getY() + 0.2 + random.nextDouble() * 0.4,
          pos.getZ() + 0.3 + random.nextDouble() * 0.4,
          0.0,
          0.01,
          0.0);
    }
    if (random.nextInt(400) == 0) {
      level.playLocalSound(
          pos, SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM, SoundSource.BLOCKS, 0.4F, 1.4F, false);
    }
  }
}
