package com.fertilegrounds.gametest;

import com.fertilegrounds.fairy.FairyVisit;
import com.fertilegrounds.fairy.FairyVisits;
import com.fertilegrounds.item.ModItems;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Fairy Dust used by a player. Each test removes the visit it scheduled, so no fairy flies into a
 * later test.
 */
public class FairyDustTests {

  private static final BlockPos CROP = new BlockPos(3, 2, 3);

  @FertileTest(environment = "fertilegrounds:no_random_ticks")
  public void dustSchedulesAVisitOwnedByThePlayer(final GameTestHelper helper) {
    final TestPlayer player = holdingDust(helper);
    TestSupport.plantWheat(helper, CROP);
    player.useHeldItemOn(helper, CROP);

    final FairyVisit visit =
        pendingVisit(helper)
            .orElseThrow(() -> helper.assertionException(Component.literal("no visit scheduled")));
    helper.assertValueEqual(
        visit.source(), FairyVisit.Source.DUST, Component.literal("visit source"));
    helper.assertValueEqual(
        visit.owner(), Optional.of(player.getUUID()), Component.literal("visit owner"));
    helper.assertValueEqual(
        player.getMainHandItem().getCount(), 1, Component.literal("Fairy Dust left"));
    helper.assertTrue(
        player.lastOverlayIs("message.fertilegrounds.fairy_dust.coming_now")
            || player.lastOverlayIs("message.fertilegrounds.fairy_dust.coming_tonight"),
        Component.literal("player told a fairy is coming"));
    cleanUp(helper, player);
  }

  @FertileTest(environment = "fertilegrounds:no_random_ticks")
  public void secondDustOnTheSameCropIsRefused(final GameTestHelper helper) {
    final TestPlayer player = holdingDust(helper);
    TestSupport.plantWheat(helper, CROP);
    player.useHeldItemOn(helper, CROP);
    player.useHeldItemOn(helper, CROP);

    helper.assertTrue(
        player.lastOverlayIs("message.fertilegrounds.fairy_dust.already_coming"),
        Component.literal("player told a fairy is already coming"));
    helper.assertValueEqual(
        player.getMainHandItem().getCount(), 1, Component.literal("Fairy Dust left"));
    cleanUp(helper, player);
  }

  /** The Nether has no night, so dust there is refused and kept. */
  @FertileTest
  public void dustInTheNetherIsRefused(final GameTestHelper helper) {
    final TestPlayer player = holdingDust(helper);
    final ServerLevel nether = helper.getLevel().getServer().getLevel(Level.NETHER);
    final BlockPos crop = new BlockPos(0, 64, 0);
    nether.setBlockAndUpdate(crop.below(), Blocks.FARMLAND.defaultBlockState());
    nether.setBlockAndUpdate(crop, Blocks.WHEAT.defaultBlockState());
    // The constructor that takes a level is protected, so an empty subclass reaches it.
    player
        .getMainHandItem()
        .useOn(
            new UseOnContext(
                nether,
                player,
                InteractionHand.MAIN_HAND,
                player.getMainHandItem(),
                new BlockHitResult(Vec3.atCenterOf(crop), Direction.UP, crop, false)) {});
    nether.setBlockAndUpdate(crop, Blocks.AIR.defaultBlockState());
    nether.setBlockAndUpdate(crop.below(), Blocks.AIR.defaultBlockState());

    helper.assertTrue(
        player.lastOverlayIs("message.fertilegrounds.fairy_dust.no_night"),
        Component.literal("player told there's no night here"));
    helper.assertValueEqual(
        player.getMainHandItem().getCount(), 2, Component.literal("Fairy Dust left"));
    player.leave();
    helper.succeed();
  }

  private static TestPlayer holdingDust(final GameTestHelper helper) {
    final TestPlayer player = TestPlayer.join(helper);
    player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.FAIRY_DUST, 2));
    return player;
  }

  private static Optional<FairyVisit> pendingVisit(final GameTestHelper helper) {
    final BlockPos crop = helper.absolutePos(CROP);
    return FairyVisits.get(helper.getLevel()).stream()
        .filter(visit -> visit.pos().equals(crop))
        .findFirst();
  }

  private static void cleanUp(final GameTestHelper helper, final TestPlayer player) {
    pendingVisit(helper).ifPresent(visit -> FairyVisits.remove(helper.getLevel(), visit));
    player.leave();
    helper.succeed();
  }
}
