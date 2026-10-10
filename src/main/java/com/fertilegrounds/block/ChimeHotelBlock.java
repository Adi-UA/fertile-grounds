package com.fertilegrounds.block;

import com.fertilegrounds.hotel.ChimeRules;
import com.fertilegrounds.hotel.FairyHotelRules;
import com.fertilegrounds.hotel.HotelChimes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/**
 * An occupied Fairy Hotel with a note block and a spyglass: it does everything an occupied hotel
 * does, and chimes once when the crops around it are ready to harvest.
 *
 * <p>Every 5 seconds a scheduled tick checks the field. Scheduled ticks save with the chunk, so the
 * checks carry on after a reload, and unlike random ticks they come at a steady pace.
 */
public class ChimeHotelBlock extends FairyHotelBlock {

  /**
   * The field was ready at the last check, so the hotel waits for a harvest before ringing again.
   */
  public static final BooleanProperty READY = BooleanProperty.create("ready");

  /**
   * The hotel just rang for a ready field: comparators read 15 until the next check, 20 ticks
   * later, so a harvest alarm gets a 1-second pulse. The placement chime doesn't pulse, so placing
   * a hotel next to an auto-harvester never starts it.
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
            && !FairyHotelRules.isJustAfterDawn(
                level.getOverworldClockTime(), CHECK_INTERVAL_TICKS);
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
      final BlockState state, final Level level, final BlockPos pos, final Direction direction) {
    return state.getValue(RINGING) ? 15 : 0;
  }
}
