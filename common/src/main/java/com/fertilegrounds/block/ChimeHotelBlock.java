package com.fertilegrounds.block;

import com.fertilegrounds.grove.FairyGrove;
import com.fertilegrounds.hotel.ChimeRules;
import com.fertilegrounds.hotel.FairyHotelRules;
import com.fertilegrounds.hotel.HotelChimes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

/**
 * An occupied Fairy Hotel that chimes once when the crops around it are ready to harvest. A
 * scheduled tick checks the field every 5 seconds, since scheduled ticks save with the chunk.
 */
public class ChimeHotelBlock extends FairyHotelBlock {

  /**
   * The field was ready at the last check, so the hotel waits for a harvest before ringing again.
   */
  public static final BooleanProperty READY = BooleanProperty.create("ready");

  /**
   * The hotel rang for a ready field, so comparators read 15 for 20 ticks. The placement chime
   * doesn't pulse, so placing a hotel never starts an auto-harvester.
   */
  public static final BooleanProperty RINGING = BooleanProperty.create("ringing");

  public static final int CHECK_INTERVAL_TICKS = 100;

  public static final int RINGING_TICKS = 20;

  public ChimeHotelBlock(final Properties properties) {
    super(properties, true);
    this.registerDefaultState(
        this.defaultBlockState().setValue(READY, false).setValue(RINGING, false));
  }

  @Override
  protected void createBlockStateDefinition(
      final StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
    builder.add(READY, RINGING);
  }

  /** A diamond wakes a Fairy Grove when this hotel is the heart of a complete shrine. */
  @Override
  protected ItemInteractionResult useItemOn(
      final ItemStack stack,
      final BlockState state,
      final Level level,
      final BlockPos pos,
      final Player player,
      final InteractionHand hand,
      final BlockHitResult hitResult) {
    if (!stack.is(Items.DIAMOND)) {
      return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
    // The client only predicts the arm swing; the server checks the shrine and the area.
    if (level instanceof ServerLevel serverLevel) {
      FairyGrove.tryStart(serverLevel, pos, player, stack);
    }
    return ItemInteractionResult.SUCCESS;
  }

  /** Starts the checks and rings once, so players learn the sound. */
  @Override
  protected void onPlace(
      final BlockState state,
      final Level level,
      final BlockPos pos,
      final BlockState oldState,
      final boolean movedByPiston) {
    super.onPlace(state, level, pos, oldState, movedByPiston);
    if (!oldState.is(this) && level instanceof ServerLevel serverLevel) {
      level.scheduleTick(pos, this, CHECK_INTERVAL_TICKS);
      HotelChimes.ring(serverLevel, pos, false);
    }
  }

  /**
   * One check of the field. A block holds only one pending scheduled tick, so petting can't
   * schedule its own dawn tick here; instead the first check after dawn ends the petted mood.
   */
  @Override
  protected void tick(
      final BlockState state,
      final ServerLevel level,
      final BlockPos pos,
      final RandomSource random) {
    final ChimeRules.Check check = HotelChimes.check(level, pos, state.getValue(READY));
    final boolean petted =
        state.getValue(PETTED)
            && !FairyHotelRules.isJustAfterDawn(level.getDayTime(), CHECK_INTERVAL_TICKS);
    final BlockState next =
        state
            .setValue(READY, check.ready())
            .setValue(RINGING, check.chime())
            .setValue(PETTED, petted);
    if (next != state) {
      level.setBlockAndUpdate(pos, next);
    }
    if (check.chime()) {
      HotelChimes.ring(level, pos, true);
    }
    level.scheduleTick(pos, this, check.chime() ? RINGING_TICKS : CHECK_INTERVAL_TICKS);
  }

  @Override
  protected boolean hasAnalogOutputSignal(final BlockState state) {
    return true;
  }

  @Override
  protected int getAnalogOutputSignal(
      final BlockState state, final Level level, final BlockPos pos) {
    return state.getValue(RINGING) ? 15 : 0;
  }
}
