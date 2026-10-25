package com.fertilegrounds.item;

import com.fertilegrounds.block.SoilUpgrades;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Upgrades the soil it's used on in place (see {@link SoilUpgrades}), keeping whatever grows on
 * top. Dispensers use the same {@link #upgrade} through {@code FertilizerDispenseBehavior}.
 */
public class FertilizerItem extends Item {

  public FertilizerItem(final Properties properties) {
    super(properties);
  }

  @Override
  public InteractionResult useOn(final UseOnContext context) {
    final Level level = context.getLevel();
    final BlockPos pos = context.getClickedPos();
    if (upgradedBlock(level, pos).isEmpty()) {
      return InteractionResult.PASS;
    }
    // The client only predicts the arm swing; the server swaps the block.
    if (level instanceof ServerLevel serverLevel) {
      upgrade(serverLevel, pos);
      context.getItemInHand().consume(1, context.getPlayer());
    }
    return InteractionResult.SUCCESS;
  }

  /**
   * Swaps the soil at {@code pos} for its upgrade, with a sound and particles.
   *
   * @return false if Fertilizer does nothing to that block
   */
  public static boolean upgrade(final ServerLevel level, final BlockPos pos) {
    final Optional<Block> upgraded = upgradedBlock(level, pos);
    if (upgraded.isEmpty()) {
      return false;
    }
    // Keeps the properties both blocks share, such as farmland's moisture.
    level.setBlockAndUpdate(pos, upgraded.get().withPropertiesOf(level.getBlockState(pos)));
    level.playSound(null, pos, SoundEvents.COMPOSTER_FILL_SUCCESS, SoundSource.BLOCKS, 1.0F, 1.0F);
    level.sendParticles(
        ParticleTypes.HAPPY_VILLAGER,
        pos.getX() + 0.5,
        pos.getY() + 1.0,
        pos.getZ() + 0.5,
        8,
        0.3,
        0.2,
        0.3,
        0.0);
    return true;
  }

  private static Optional<Block> upgradedBlock(final LevelReader level, final BlockPos pos) {
    final BlockState state = level.getBlockState(pos);
    return SoilUpgrades.upgradeOf(BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString())
        .map(id -> BuiltInRegistries.BLOCK.get(ResourceLocation.parse(id)));
  }
}
