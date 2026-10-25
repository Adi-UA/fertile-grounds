package com.fertilegrounds.gametest;

import com.fertilegrounds.block.ModBlocks;
import com.fertilegrounds.compat.jade.ChimeFieldData;
import com.fertilegrounds.compat.jade.PendingVisitData;
import com.fertilegrounds.compat.jade.SoilChanceData;
import com.fertilegrounds.fairy.FairyVisit;
import com.fertilegrounds.fairy.FairyVisits;
import com.fertilegrounds.platform.Platform;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;

/**
 * The data the server sends Jade for each tooltip, when Jade is installed. Every Jade type stays in
 * {@link JadeProbe}, since the test runner reads this class's methods even without Jade.
 */
public class JadeDataTests {

  private static final BlockPos POS = new BlockPos(3, 2, 3);

  @FertileTest(environment = "fertilegrounds:no_random_ticks")
  public void soilSendsItsBoostChance(final GameTestHelper helper) {
    if (skipWithoutJade(helper)) {
      return;
    }
    helper.setBlock(POS, ModBlocks.SUPER_ENRICHED_FARMLAND);
    final CompoundTag data = JadeProbe.soilChance(helper, POS);
    helper.assertValueEqual(data.getInt(SoilChanceData.CHANCE), 50, "boost chance");
    helper.succeed();
  }

  @FertileTest(environment = "fertilegrounds:no_random_ticks")
  public void cropWithAVisitSendsComing(final GameTestHelper helper) {
    if (skipWithoutJade(helper)) {
      return;
    }
    TestSupport.plantWheat(helper, POS);
    helper.assertFalse(
        JadeProbe.pendingVisit(helper, POS).getBoolean(PendingVisitData.COMING),
        "coming before any visit");
    final FairyVisit visit =
        new FairyVisit(
            helper.absolutePos(POS),
            helper.getLevel().getGameTime() + 100_000,
            FairyVisit.Source.DUST,
            Optional.empty());
    FairyVisits.add(helper.getLevel(), visit);
    helper.assertTrue(
        JadeProbe.pendingVisit(helper, POS).getBoolean(PendingVisitData.COMING),
        "coming after Fairy Dust");
    FairyVisits.remove(helper.getLevel(), visit);
    helper.succeed();
  }

  /** 3 of 5 wheat grown next to a Chime Hotel. */
  @FertileTest(environment = "fertilegrounds:no_random_ticks")
  public void chimeHotelSendsItsCropCount(final GameTestHelper helper) {
    if (skipWithoutJade(helper)) {
      return;
    }
    HotelEffectTests.placeHotel(helper, POS, ModBlocks.CHIME_FAIRY_HOTEL);
    for (int i = 0; i < 5; i++) {
      final BlockPos crop = new BlockPos(1 + i, 2, 5);
      TestSupport.plantWheat(helper, crop);
      if (i < 3) {
        helper.setBlock(
            crop, Blocks.WHEAT.defaultBlockState().setValue(CropBlock.AGE, CropBlock.MAX_AGE));
      }
    }
    final CompoundTag data = JadeProbe.chimeField(helper, POS);
    helper.assertValueEqual(data.getInt(ChimeFieldData.GROWN), 3, "grown crops");
    helper.assertValueEqual(data.getInt(ChimeFieldData.TOTAL), 5, "crops");
    helper.succeed();
  }

  private static boolean skipWithoutJade(final GameTestHelper helper) {
    if (Platform.INSTANCE.isModLoaded("jade")) {
      return false;
    }
    helper.succeed();
    return true;
  }
}
