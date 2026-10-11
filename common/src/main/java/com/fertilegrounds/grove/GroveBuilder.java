package com.fertilegrounds.grove;

import com.fertilegrounds.block.ModBlocks;
import com.fertilegrounds.entity.BeanFairy;
import com.fertilegrounds.entity.ModEntities;
import com.fertilegrounds.util.ModSavedData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Builds Fairy Groves a few columns per tick. Jobs are saved with the world through Fabric's data
 * attachment API, so a grove interrupted by a restart finishes afterward.
 */
public final class GroveBuilder {

  /** 1,144 columns at 2 per tick takes about 30 seconds. */
  private static final int COLUMNS_PER_TICK = 2;

  private static final int BUILDER_FAIRIES = 4;

  /** How high above the ground the builder fairies circle. */
  private static final double FAIRY_HEIGHT = 3.5;

  private static final List<GroveLayout.Offset> ORDER = GroveLayout.buildOrder();

  /** Small flowers for borders and the meadow. Wither roses are left out on purpose. */
  private static final List<Block> FLOWERS =
      List.of(
          Blocks.DANDELION,
          Blocks.POPPY,
          Blocks.BLUE_ORCHID,
          Blocks.ALLIUM,
          Blocks.AZURE_BLUET,
          Blocks.RED_TULIP,
          Blocks.ORANGE_TULIP,
          Blocks.WHITE_TULIP,
          Blocks.PINK_TULIP,
          Blocks.OXEYE_DAISY,
          Blocks.CORNFLOWER,
          Blocks.LILY_OF_THE_VALLEY);

  /** Builder fairies aren't saved, so they live here and get respawned after a restart. */
  private static final Map<GlobalPos, List<BeanFairy>> FAIRIES = new HashMap<>();

  private GroveBuilder() {}

  /** Forgets the builder fairies when the server stops; the jobs stay saved with the world. */
  public static void onServerStopped() {
    FAIRIES.clear();
  }

  public static List<GroveJob> jobs(final ServerLevel level) {
    return ModSavedData.GROVE_JOBS.get(level);
  }

  public static boolean isBuilding(final ServerLevel level, final BlockPos soil) {
    return jobs(level).stream().anyMatch(job -> job.soil().equals(soil));
  }

  static void start(final ServerLevel level, final BlockPos soil) {
    add(level, new GroveJob(soil, 0));
  }

  /** Queues a job, such as one loaded from a save partway through. */
  public static void add(final ServerLevel level, final GroveJob job) {
    final List<GroveJob> updated = new ArrayList<>(jobs(level));
    updated.add(job);
    ModSavedData.GROVE_JOBS.set(level, List.copyOf(updated));
  }

  /** How many columns a whole grove has, for checking a job's progress. */
  public static int columnCount() {
    return ORDER.size();
  }

  /** The builder fairies of a grove in progress, or none. */
  public static List<BeanFairy> fairies(final ServerLevel level, final BlockPos soil) {
    return FAIRIES.getOrDefault(GlobalPos.of(level.dimension(), soil), List.of());
  }

  /** Runs at the end of each world's tick: builds the next column of every job. */
  public static void tick(final ServerLevel level) {
    final List<GroveJob> jobs = jobs(level);
    if (jobs.isEmpty()) {
      return;
    }
    final List<GroveJob> updated = new ArrayList<>();
    for (final GroveJob job : jobs) {
      if (!level.isLoaded(job.soil())) {
        updated.add(job);
        continue;
      }
      final int end = Math.min(ORDER.size(), job.next() + COLUMNS_PER_TICK);
      for (int i = job.next(); i < end; i++) {
        buildColumn(level, job.soil(), ORDER.get(i), i);
      }
      if (end < ORDER.size()) {
        updated.add(job.advancedBy(end - job.next()));
        guideFairies(level, job.soil(), ORDER.get(end).ring());
      } else {
        finish(level, job.soil());
      }
    }
    ModSavedData.GROVE_JOBS.set(level, List.copyOf(updated));
  }

  /**
   * Rebuilds one column: clears it above the ground, fills dips below, and places the layout block.
   * A column that gained a non-natural block since the safety check is left alone.
   */
  private static void buildColumn(
      final ServerLevel level,
      final BlockPos soil,
      final GroveLayout.Offset offset,
      final int index) {
    final BlockPos ground = soil.offset(offset.dx(), 0, offset.dz());
    for (int dy = -FairyGrove.FILL_DEPTH; dy <= FairyGrove.CLEAR_HEIGHT; dy++) {
      if (!FairyGrove.isReplaceable(level.getBlockState(ground.above(dy)))) {
        return;
      }
    }
    // Top down, so nothing above a cleared block can fall into it.
    for (int dy = FairyGrove.CLEAR_HEIGHT; dy >= 1; dy--) {
      if (!level.getBlockState(ground.above(dy)).isAir()) {
        level.setBlockAndUpdate(ground.above(dy), Blocks.AIR.defaultBlockState());
      }
    }
    // Fill down to the first solid block.
    for (int dy = 1; dy <= FairyGrove.FILL_DEPTH; dy++) {
      final BlockPos pos = ground.below(dy);
      if (level.getBlockState(pos).isSolidRender()) {
        break;
      }
      level.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
    }
    // Seeded by position, so the same grove gets the same flowers if it's ever rebuilt.
    final RandomSource random = RandomSource.create(ground.asLong());
    placeLayout(level, ground, GroveLayout.cell(offset.dx(), offset.dz()), random);
    if (index % 4 == 0) {
      level.playSound(null, ground, SoundEvents.ROOTED_DIRT_PLACE, SoundSource.BLOCKS, 0.3F, 1.0F);
      level.sendParticles(
          ParticleTypes.HAPPY_VILLAGER,
          ground.getX() + 0.5,
          ground.getY() + 1.2,
          ground.getZ() + 0.5,
          2,
          0.3,
          0.2,
          0.3,
          0.0);
    }
  }

  private static void placeLayout(
      final ServerLevel level,
      final BlockPos ground,
      final GroveLayout.Cell cell,
      final RandomSource random) {
    switch (cell) {
      case FARMLAND ->
          level.setBlockAndUpdate(
              ground,
              ModBlocks.ENRICHED_FARMLAND
                  .defaultBlockState()
                  .setValue(FarmlandBlock.MOISTURE, FarmlandBlock.MAX_MOISTURE));
      case WATER -> level.setBlockAndUpdate(ground, Blocks.WATER.defaultBlockState());
      case BORDER -> {
        grass(level, ground);
        if (random.nextInt(4) != 0) {
          flower(level, ground.above(), random);
        }
      }
      case HIVE_POST -> post(level, ground, Blocks.BEEHIVE.defaultBlockState());
      case HEDGE_POST -> post(level, ground, leaves(Blocks.FLOWERING_AZALEA_LEAVES));
      case MEADOW -> {
        grass(level, ground);
        final int roll = random.nextInt(20);
        if (roll == 0) {
          post(level, ground, leaves(Blocks.FLOWERING_AZALEA_LEAVES));
        } else if (roll < 4) {
          level.setBlockAndUpdate(
              ground.above(),
              leaves(random.nextBoolean() ? Blocks.AZALEA_LEAVES : Blocks.FLOWERING_AZALEA_LEAVES));
        } else if (roll < 10) {
          flower(level, ground.above(), random);
        } else if (roll < 16) {
          level.setBlockAndUpdate(ground.above(), Blocks.SHORT_GRASS.defaultBlockState());
        }
      }
      case CENTER, OUTSIDE -> {}
    }
  }

  /** Grass with a block on it and a lantern on top. */
  private static void post(
      final ServerLevel level, final BlockPos ground, final BlockState middle) {
    grass(level, ground);
    level.setBlockAndUpdate(ground.above(), middle);
    level.setBlockAndUpdate(ground.above(2), Blocks.LANTERN.defaultBlockState());
  }

  private static void grass(final ServerLevel level, final BlockPos ground) {
    level.setBlockAndUpdate(ground, Blocks.GRASS_BLOCK.defaultBlockState());
  }

  private static void flower(
      final ServerLevel level, final BlockPos pos, final RandomSource random) {
    level.setBlockAndUpdate(pos, FLOWERS.get(random.nextInt(FLOWERS.size())).defaultBlockState());
  }

  /** Placed leaves never decay, like leaves a player places. */
  private static BlockState leaves(final Block leaves) {
    return leaves.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true);
  }

  /** The builder fairies circle the shrine at the ring being built, spread evenly around it. */
  private static void guideFairies(final ServerLevel level, final BlockPos soil, final int ring) {
    final List<BeanFairy> fairies =
        FAIRIES.computeIfAbsent(
            GlobalPos.of(level.dimension(), soil), key -> spawnFairies(level, soil));
    final double turn = level.getGameTime() * 0.05;
    for (int i = 0; i < fairies.size(); i++) {
      final double angle = turn + i * Math.PI * 2 / fairies.size();
      fairies
          .get(i)
          .setGroveTarget(
              new Vec3(
                  soil.getX() + 0.5 + ring * Math.cos(angle),
                  soil.getY() + FAIRY_HEIGHT,
                  soil.getZ() + 0.5 + ring * Math.sin(angle)));
    }
  }

  /** One fairy flies out of each of the 4 shrine hotels. */
  private static List<BeanFairy> spawnFairies(final ServerLevel level, final BlockPos soil) {
    final List<BeanFairy> fairies = new ArrayList<>();
    for (int i = 0; i < BUILDER_FAIRIES; i++) {
      final BeanFairy fairy = ModEntities.BEAN_FAIRY.create(level, EntitySpawnReason.EVENT);
      if (fairy == null) {
        continue;
      }
      final double angle = i * Math.PI / 2;
      final Vec3 hotel =
          new Vec3(
              soil.getX() + 0.5 + GroveShrine.HOTEL_DISTANCE * Math.cos(angle),
              soil.getY() + 1.5,
              soil.getZ() + 0.5 + GroveShrine.HOTEL_DISTANCE * Math.sin(angle));
      fairy.setPos(hotel);
      fairy.setGroveTarget(hotel);
      level.addFreshEntity(fairy);
      fairies.add(fairy);
    }
    return fairies;
  }

  private static void finish(final ServerLevel level, final BlockPos soil) {
    final List<BeanFairy> fairies = FAIRIES.remove(GlobalPos.of(level.dimension(), soil));
    if (fairies != null) {
      for (final BeanFairy fairy : fairies) {
        level.sendParticles(
            ParticleTypes.END_ROD,
            fairy.getX(),
            fairy.getY(),
            fairy.getZ(),
            8,
            0.2,
            0.2,
            0.2,
            0.02);
        fairy.discard();
      }
    }
    final BlockPos shrine = soil.above();
    level.playSound(null, shrine, SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 1.0F, 1.0F);
    level.sendParticles(
        ParticleTypes.FIREWORK,
        shrine.getX() + 0.5,
        shrine.getY() + 3.0,
        shrine.getZ() + 0.5,
        40,
        1.5,
        1.0,
        1.5,
        0.05);
  }
}
