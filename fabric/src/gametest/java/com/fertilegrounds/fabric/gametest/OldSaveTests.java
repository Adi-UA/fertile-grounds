package com.fertilegrounds.fabric.gametest;

import com.fertilegrounds.fairy.FairyVisit;
import com.fertilegrounds.util.ModIdsUtil;
import java.util.List;
import java.util.Optional;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.fabricmc.fabric.impl.attachment.AttachmentRegistryImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

/**
 * Worlds saved by 1.2.0, the last Fabric-only release for 1.21.1, keep their pending fairy visits.
 * 1.2.0 stored them in a Fabric world attachment with the same id, as a list of {@code pos} and
 * {@code arrival_tick}.
 */
public class OldSaveTests {

  @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
  public void pendingVisitsFromVersionOneTwoLoad(final GameTestHelper helper) {
    // This Fabric API has no public lookup by id; the test never ships, so the internal one is
    // fine.
    final AttachmentType<?> type =
        AttachmentRegistryImpl.get(ModIdsUtil.id("pending_fairy_visits"));
    if (type == null) {
      throw new GameTestAssertException("no pending_fairy_visits attachment");
    }
    helper.assertTrue(type.isPersistent(), "the attachment is saved with the world");

    final CompoundTag visit = new CompoundTag();
    visit.putIntArray("pos", new int[] {1, 64, 2});
    visit.putLong("arrival_tick", 100L);
    final ListTag saved = new ListTag();
    saved.add(visit);

    final Object loaded =
        type.persistenceCodec()
            .parse(NbtOps.INSTANCE, (Tag) saved)
            .getOrThrow(GameTestAssertException::new);
    helper.assertValueEqual(
        loaded,
        List.of(
            new FairyVisit(
                new BlockPos(1, 64, 2), 100L, FairyVisit.Source.UNOWNED, Optional.empty())),
        "pending visits");
    helper.succeed();
  }
}
