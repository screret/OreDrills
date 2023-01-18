package screret.oredrills.capability.vein;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.registries.ForgeRegistries;
import screret.oredrills.resources.OreVeinManager;
import screret.oredrills.resources.OreVeinType;

import javax.annotation.Nullable;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

@AutoRegisterCapability
public class VeinCapability implements IVeinCapability {
    // Essentially indexed by ChunkPos for ease of use
    private final ConcurrentHashMap<ChunkPos, ConcurrentLinkedQueue<PendingBlock>> pendingBlocks;
    private final ConcurrentHashMap<ChunkPos, Map<OreVeinType, Integer>> oreTypeCountPerChunk;

    public static final Capability<IVeinCapability> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() { });

    private final Level level;

    public VeinCapability(Level level) {
        this.pendingBlocks = new ConcurrentHashMap<>();
        this.oreTypeCountPerChunk = new ConcurrentHashMap<>();
        this.level = level;
    }

    public int getPendingBlockCount() {
        return (int) this.pendingBlocks.values().stream().collect(Collectors.summarizingInt(ConcurrentLinkedQueue::size)).getSum();
    }

    @Override
    public void putPendingBlock(BlockPos pos, BlockState state, OreVeinType type) {
        PendingBlock p = new PendingBlock(pos, state, type);
        ChunkPos cp = new ChunkPos(pos);
        this.pendingBlocks.putIfAbsent(cp, new ConcurrentLinkedQueue<>());
        this.pendingBlocks.get(cp).add(p);
    }

    public void putVein(BlockPos pos, OreVeinType type){
        ChunkPos cp = new ChunkPos(pos);
        this.oreTypeCountPerChunk.putIfAbsent(cp, new HashMap<>());
        var chunkVeins = this.oreTypeCountPerChunk.get(cp);
        chunkVeins.put(type, chunkVeins.get(type) == null ? 1 : chunkVeins.get(type) + 1);
    }

    @Override
    public void removePendingBlocksForChunk(ChunkPos cp) {
        this.pendingBlocks.remove(cp);
    }

    @Override
    public ConcurrentLinkedQueue<PendingBlock> getPendingBlocks(ChunkPos chunkPos) {
        return this.pendingBlocks.getOrDefault(chunkPos, new ConcurrentLinkedQueue<>());
    }

    @Override
    public Map<OreVeinType, Integer> getOreVeins(ChunkPos chunkPos) {
        return this.oreTypeCountPerChunk.getOrDefault(chunkPos, new HashMap<>());
    }

    @Override
    public int deductOreFromVein(ChunkPos pos, OreVeinType type){
        var veins = this.oreTypeCountPerChunk.get(pos);
        if(veins != null && veins.get(type) != null) {
            var deducted = veins.get(type) - 1;
            veins.replace(type, deducted);
            return deducted;
        }
        return 0;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compound = new CompoundTag();
        CompoundTag pendingTag = new CompoundTag();
        this.pendingBlocks.forEach((pos, pending) -> {
            ListTag p = new ListTag();
            String key = pos.x + "_" + pos.z;
            pending.forEach(pb -> p.add(pb.serialize()));
            pendingTag.put(key, p);
        });
        CompoundTag countTag = new CompoundTag();
        this.oreTypeCountPerChunk.forEach((pos, veins) -> {
            CompoundTag veinTag = new CompoundTag();
            String key = pos.x + "_" + pos.z;
            veins.forEach((vein, count) -> {
                veinTag.putInt(vein.id.toString(), count);
            });
            countTag.put(key, veinTag);
        });
        compound.put("pending", pendingTag);
        compound.put("veins", countTag);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        var pendingTag = compound.getCompound("pending");
        pendingTag.getAllKeys().forEach(chunkPosAsString -> {
            // Parse out the ChunkPos
            String[] parts = chunkPosAsString.split("_");
            ChunkPos cp = new ChunkPos(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            // Parse out the pending block objects
            ListTag pending = pendingTag.getList(chunkPosAsString, 10);
            ConcurrentLinkedQueue<PendingBlock> lq = new ConcurrentLinkedQueue<>();
            pending.forEach(x -> {
                PendingBlock pb = PendingBlock.deserialize(x, this.level);
                if (pb != null) {
                    lq.add(pb);
                }
            });
            this.pendingBlocks.put(cp, lq);
        });
        var veinsTag = compound.getCompound("veins");
        veinsTag.getAllKeys().forEach(chunkPosAsString -> {
            // Parse out the ChunkPos
            String[] parts = chunkPosAsString.split("_");
            ChunkPos cp = new ChunkPos(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            // Parse out the pending block objects
            oreTypeCountPerChunk.putIfAbsent(cp, new HashMap<>());
            CompoundTag vein = veinsTag.getCompound(chunkPosAsString);
            vein.getAllKeys().forEach((key) -> {
                oreTypeCountPerChunk.get(cp).put(OreVeinManager.INSTANCE.getAllVeins().get(new ResourceLocation(key)), vein.getInt(key));
            });
        });
    }

    public record PendingBlock(BlockPos pos, BlockState state, OreVeinType type) {

        public CompoundTag serialize() {
            CompoundTag tmp = new CompoundTag();
            CompoundTag posTag = NbtUtils.writeBlockPos(this.pos);
            CompoundTag stateTag = NbtUtils.writeBlockState(this.state);
            tmp.put("pos", posTag);
            tmp.put("state", stateTag);
            tmp.putString("type", this.type.id.toString());
            return tmp;
        }

        @Nullable
        public static PendingBlock deserialize(Tag t, Level level) {
            if (t instanceof CompoundTag tag) {
                BlockPos pos = NbtUtils.readBlockPos(tag.getCompound("pos"));
                BlockState state = NbtUtils.readBlockState(level.holderLookup(Registries.BLOCK), tag.getCompound("state"));
                OreVeinType type = OreVeinManager.INSTANCE.getAllVeins().get(new ResourceLocation(tag.getString("type")));
                return new PendingBlock(pos, state, type);
            }
            return null;
        }

        @Override
        public String toString() {
            return "[" + this.pos.getX() + " " + this.pos.getY() + " " + this.pos.getZ() + "]: " + ForgeRegistries.BLOCKS.getKey(this.state.getBlock()) + "; " + type.id.toString();
        }
    }
}