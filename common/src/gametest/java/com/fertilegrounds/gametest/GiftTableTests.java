package com.fertilegrounds.gametest;

import com.fertilegrounds.util.ModIdsUtil;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

/** Each gift tier's loot table loads and rolls at least one item, the way a gift rolls it. */
public class GiftTableTests {

  @FertileTest
  public void commonRolls(final GameTestHelper helper) {
    assertRolls(helper, "common");
  }

  @FertileTest
  public void uncommonRolls(final GameTestHelper helper) {
    assertRolls(helper, "uncommon");
  }

  @FertileTest
  public void rareRolls(final GameTestHelper helper) {
    assertRolls(helper, "rare");
  }

  private static void assertRolls(final GameTestHelper helper, final String tier) {
    final List<ItemStack> items =
        helper
            .getLevel()
            .getServer()
            .reloadableRegistries()
            .getLootTable(
                ResourceKey.create(
                    Registries.LOOT_TABLE, ModIdsUtil.id("gameplay/fairy_gift/" + tier)))
            .getRandomItems(
                new LootParams.Builder(helper.getLevel())
                    .withParameter(LootContextParams.ORIGIN, Vec3.ZERO)
                    .create(LootContextParamSets.CHEST));
    helper.assertTrue(!items.isEmpty(), Component.literal(tier + " gift dropped nothing"));
    helper.succeed();
  }
}
