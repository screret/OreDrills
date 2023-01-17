package screret.oredrills.capability.vein;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import screret.oredrills.resources.OreVeinType;

public interface IVeinCapability {
    void putPendingBlock(BlockPos pos, BlockState state, OreVeinType type);

    void removePendingBlocksForChunk(ChunkPos p);

    int getPendingBlockCount();

    ConcurrentLinkedQueue<VeinCapability.PendingBlock> getPendingBlocks(ChunkPos chunkPos);

    Map<OreVeinType, Integer> getOreVeins(ChunkPos chunkPos);

    int deductOreFromVein(ChunkPos pos, OreVeinType type);

    CompoundTag serializeNBT();

    void deserializeNBT(CompoundTag nbt);
}
