package com.fertilegrounds.block;

import com.fertilegrounds.hotel.FairyHotelEffects;
import com.fertilegrounds.hotel.FairyHotelLookup;
import com.fertilegrounds.hotel.FairyHotelRules;
import com.fertilegrounds.item.ModItems;
import com.fertilegrounds.util.ModCriteria;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

/**
 * A Fairy Hotel: a lantern that hangs and sits like vanilla's. Empty and occupied hotels are two
 * blocks of this class, so each has its own item and loot table and an occupied hotel keeps its
 * fairy when broken and placed again, with no block entity or item data.
 */
public class FairyHotelBlock extends LanternBlock {

  /** Brushed since the last dawn: the fairy boosts crops at the petted chance. */
  public static final BooleanProperty PETTED = BooleanProperty.create("petted");

  private final boolean occupied;

  public FairyHotelBlock(final Properties properties, final boolean occupied) {
    super(properties);
    this.occupied = occupied;
    this.registerDefaultState(this.defaultBlockState().setValue(PETTED, false));
  }

  @Override
  protected void createBlockStateDefinition(
      final StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
    builder.add(PETTED);
  }

  public boolean isOccupied() {
    return this.occupied;
  }

  /** A Jarred Fairy used on an empty hotel moves in for good, and the bottle comes back. */
  @Override
  protected ItemInteractionResult useItemOn(
      final ItemStack stack,
      final BlockState state,
      final Level level,
      final BlockPos pos,
      final Player player,
      final InteractionHand hand,
      final BlockHitResult hitResult) {
    if (this.occupied && stack.is(Items.BRUSH)) {
      return pet(stack, state, level, pos, player, hand);
    }
    if (this.occupied || !stack.is(ModItems.JARRED_FAIRY)) {
      return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
    if (level instanceof ServerLevel serverLevel) {
      level.setBlockAndUpdate(pos, ModBlocks.FAIRY_HOTEL.withPropertiesOf(state));
      player.setItemInHand(
          hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
      if (player instanceof ServerPlayer serverPlayer) {
        ModCriteria.FAIRY_MOVED_IN.trigger(serverPlayer);
      }
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
    return ItemInteractionResult.SUCCESS;
  }

  /**
   * A Brush pets the fairy once per day: it boosts crops more until the next dawn, when a scheduled
   * tick clears it. Brushing again the same day does nothing, and the brush skips its usual
   * hold-to-brush use.
   */
  private ItemInteractionResult pet(
      final ItemStack brush,
      final BlockState state,
      final Level level,
      final BlockPos pos,
      final Player player,
      final InteractionHand hand) {
    if (state.getValue(PETTED)) {
      return ItemInteractionResult.FAIL;
    }
    if (level instanceof ServerLevel serverLevel) {
      level.setBlockAndUpdate(pos, state.setValue(PETTED, true));
      level.scheduleTick(pos, this, FairyHotelRules.ticksUntilDawn(serverLevel.getDayTime()));
      brush.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
      level.playSound(null, pos, SoundEvents.BRUSH_GENERIC, SoundSource.BLOCKS, 1.0F, 1.0F);
      level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.0F, 1.3F);
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
          ParticleTypes.GLOW,
          pos.getX() + 0.5,
          pos.getY() + 0.4,
          pos.getZ() + 0.5,
          6,
          0.2,
          0.2,
          0.2,
          0.02);
    }
    return ItemInteractionResult.SUCCESS;
  }

  /** At dawn the fairy's happy mood wears off, and it can be petted again. */
  @Override
  protected void tick(
      final BlockState state,
      final ServerLevel level,
      final BlockPos pos,
      final RandomSource random) {
    super.tick(state, level, pos, random);
    if (state.getValue(PETTED)) {
      level.setBlockAndUpdate(pos, state.setValue(PETTED, false));
    }
  }

  /** An occupied hotel that leads its cluster helps the farm around it. */
  @Override
  protected void randomTick(
      final BlockState state,
      final ServerLevel level,
      final BlockPos pos,
      final RandomSource random) {
    if (this.occupied && FairyHotelLookup.isClusterLeader(level, pos)) {
      FairyHotelEffects.randomTick(level, pos, state.getValue(PETTED), random);
    }
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
