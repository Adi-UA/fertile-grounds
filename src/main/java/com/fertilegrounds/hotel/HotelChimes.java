package com.fertilegrounds.hotel;

import com.fertilegrounds.util.CropGrowthUtil;
import com.fertilegrounds.util.ModGameRules;
import com.fertilegrounds.util.ModSounds;
import com.fertilegrounds.util.ModTags;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.BlockState;

/**
 * What a Chime Hotel does: counts the fairy crops around it and rings a 3-note rising chime. The
 * notes play 4 ticks apart from a small queue drained each server tick, since a block can only have
 * one pending scheduled tick, and the hotel's 5-second check already uses it.
 */
public final class HotelChimes {

  /** Crops this far from the hotel, horizontally, count toward its field. */
  public static final int REACH = 8;

  /** And this far up or down. */
  public static final int REACH_HEIGHT = 2;

  /** Ticks between the chime's notes. */
  private static final int NOTE_GAP_TICKS = 4;

  /** The chime's pitches: a rising major chord. */
  private static final float[] PITCHES = {1.0F, 1.26F, 1.5F};

  /** Volume 2 makes the variable-range sound audible 32 blocks away. */
  private static final float VOLUME = 2.0F;

  private static final List<Note> PENDING = new ArrayList<>();

  private HotelChimes() {}

  public static void register() {
    ServerTickEvents.END_LEVEL_TICK.register(HotelChimes::playDueNotes);
    ServerLifecycleEvents.SERVER_STOPPED.register(server -> PENDING.clear());
  }

  /** Whether the field around {@code hotel} is ready, given whether it was at the last check. */
  public static ChimeRules.Check check(
      final ServerLevel level, final BlockPos hotel, final boolean wasReady) {
    final Field field = countField(level, hotel);
    return ChimeRules.next(
        wasReady,
        field.grown(),
        field.total(),
        level.getGameRules().get(ModGameRules.CHIME_READY_PERCENT));
  }

  /** The fairy crops within reach of {@code hotel}, and how many of them are fully grown. */
  public static Field countField(final ServerLevel level, final BlockPos hotel) {
    int grown = 0;
    int total = 0;
    for (final BlockPos pos :
        BlockPos.betweenClosed(
            hotel.offset(-REACH, -REACH_HEIGHT, -REACH),
            hotel.offset(REACH, REACH_HEIGHT, REACH))) {
      final BlockState state = level.getBlockState(pos);
      if (state.is(ModTags.FAIRY_CROPS)) {
        total++;
        if (!CropGrowthUtil.canGrow(level, pos)) {
          grown++;
        }
      }
    }
    return new Field(grown, total);
  }

  /** A Chime Hotel's field: fully grown crops out of all crops in reach. */
  public record Field(int grown, int total) {}

  /**
   * Rings the chime at {@code hotel}. A ready field gets more notes floating up and a burst of
   * glow; a newly placed hotel rings plainly, so players learn its sound.
   */
  public static void ring(final ServerLevel level, final BlockPos hotel, final boolean ready) {
    for (int note = 0; note < PITCHES.length; note++) {
      PENDING.add(
          new Note(
              level,
              hotel.immutable(),
              note,
              ready,
              level.getGameTime() + (long) note * NOTE_GAP_TICKS));
    }
    if (ready) {
      level.sendParticles(
          ParticleTypes.GLOW,
          hotel.getX() + 0.5,
          hotel.getY() + 0.4,
          hotel.getZ() + 0.5,
          10,
          0.25,
          0.25,
          0.25,
          0.02);
    }
    playDueNotes(level);
  }

  private static void playDueNotes(final ServerLevel level) {
    PENDING.removeIf(note -> note.level == level && note.playIfDue());
  }

  private record Note(ServerLevel level, BlockPos pos, int index, boolean ready, long playAt) {

    boolean playIfDue() {
      if (this.level.getGameTime() < this.playAt) {
        return false;
      }
      this.level.playSound(
          null, this.pos, ModSounds.CHIME_HOTEL_CHIME, SoundSource.BLOCKS, VOLUME, PITCHES[index]);
      // With a count of 0, the note particle's x speed picks its color (0 to 1 around the wheel).
      for (int i = 0; i < (this.ready ? 2 : 1); i++) {
        this.level.sendParticles(
            ParticleTypes.NOTE,
            this.pos.getX() + 0.5,
            this.pos.getY() + 0.9 + 0.25 * this.index + 0.15 * i,
            this.pos.getZ() + 0.5,
            0,
            (this.index * 4 + i * 2) / 24.0,
            0.0,
            0.0,
            1.0);
      }
      return true;
    }
  }
}
