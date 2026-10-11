package com.fertilegrounds.gametest;

import com.fertilegrounds.block.ModBlocks;
import com.fertilegrounds.hotel.FairySpeedEffect;
import com.fertilegrounds.util.ModMobEffects;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * Fairy Speed near occupied hotels. Players are ticked like real ones, so effects count down. The
 * aura checks once a second, so each check waits past that.
 */
public class FairySpeedTests {

  private static final BlockPos HOTEL = new BlockPos(8, 2, 8);

  /**
   * Runs in the 33x33 structure, so the 8 blocks around both spots are cleared at the start and no
   * hotel left by an earlier test is in range.
   */
  @GameTest(structure = "fertilegrounds:empty_large", maxTicks = 200)
  public void playerNearAHotelGetsTenPercentThenLosesItAway(final GameTestHelper helper) {
    HotelEffectTests.placeHotel(helper, new BlockPos(4, 2, 16), ModBlocks.FAIRY_HOTEL);
    final TestPlayer player = TestPlayer.join(helper).tickedBy(helper).at(helper, 10, 2, 16);
    final double baseSpeed = player.getAttributeBaseValue(Attributes.MOVEMENT_SPEED);
    helper
        .startSequence()
        .thenWaitUntil(
            () -> helper.assertTrue(player.hasEffect(ModMobEffects.FAIRY_SPEED), "Fairy Speed"))
        .thenExecute(
            () ->
                helper.assertTrue(
                    Math.abs(
                            player.getAttributeValue(Attributes.MOVEMENT_SPEED)
                                - baseSpeed * (1 + FairySpeedEffect.SPEED_BONUS))
                        < 1e-6,
                    "moves 10% faster"))
        .thenExecute(() -> player.at(helper, 24, 2, 16))
        .thenExecuteAfter(
            61,
            () -> {
              helper.assertFalse(player.hasEffect(ModMobEffects.FAIRY_SPEED), "still has it");
              player.leave();
            })
        .thenSucceed();
  }

  @GameTest(structure = "fertilegrounds:empty_medium", maxTicks = 100)
  public void threeHotelsDontStack(final GameTestHelper helper) {
    for (final BlockPos hotel : new BlockPos[] {HOTEL, HOTEL.east(4), HOTEL.west(4)}) {
      HotelEffectTests.placeHotel(helper, hotel, ModBlocks.FAIRY_HOTEL);
    }
    final TestPlayer player = TestPlayer.join(helper).tickedBy(helper).at(helper, 8, 2, 10);
    final double baseSpeed = player.getAttributeBaseValue(Attributes.MOVEMENT_SPEED);
    helper.succeedWhen(
        () -> {
          helper.assertTrue(player.hasEffect(ModMobEffects.FAIRY_SPEED), "Fairy Speed");
          helper.assertTrue(
              Math.abs(
                      player.getAttributeValue(Attributes.MOVEMENT_SPEED)
                          - baseSpeed * (1 + FairySpeedEffect.SPEED_BONUS))
                  < 1e-6,
              "moves 10% faster, not 30%");
          player.leave();
        });
  }

  @GameTest(
      structure = "fertilegrounds:empty_medium",
      environment = "fertilegrounds:hotel_speed_off",
      maxTicks = 60)
  public void gameruleTurnsItOff(final GameTestHelper helper) {
    HotelEffectTests.placeHotel(helper, HOTEL, ModBlocks.FAIRY_HOTEL);
    final TestPlayer player = TestPlayer.join(helper).tickedBy(helper).at(helper, 10, 2, 8);
    helper.runAfterDelay(
        45,
        () -> {
          helper.assertFalse(player.hasEffect(ModMobEffects.FAIRY_SPEED), "Fairy Speed");
          player.leave();
          helper.succeed();
        });
  }
}
