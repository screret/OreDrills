package screret.oredrills.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import screret.oredrills.Config;
import screret.oredrills.capability.vein.IVeinCapability;
import screret.oredrills.capability.vein.VeinCapability;
import screret.oredrills.resources.OreVeinManager;
import screret.oredrills.resources.OreVeinType;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.ConcurrentLinkedQueue;

public class VeinFeature extends Feature<NoneFeatureConfiguration> {
    public VeinFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
        if (ctx.chunkGenerator() instanceof FlatLevelSource) {
            return false;
        }

        WorldGenLevel level = ctx.level();
        BlockPos pos = ctx.origin();

        IVeinCapability depCap = level.getLevel().getCapability(VeinCapability.CAPABILITY)
                .orElseThrow(() -> new RuntimeException("OreDrills Vein Capability Is Null.."));

        boolean placedVein = false;
        boolean placedPending = placePendingBlocks(level, depCap, pos);

        if (level.getRandom().nextDouble() > Config.Common.CHUNK_SKIP_CHANCE.get()) {
            for (int p = 0; p < Config.Common.NUMBER_VEINS_PER_CHUNK.get(); p++) {
                OreVeinType vein = OreVeinManager.INSTANCE.pick(level, pos);
                if (vein == null) {
                    continue;
                }

                boolean anyGenerated = vein.generate(level, pos, depCap) > 0;
                if (anyGenerated) {
                    placedVein = true;
                    vein.afterGen(pos);
                }
            }
        }
        // Let our tracker know that we did in fact traverse this chunk
        return placedVein || placedPending;
    }

    private boolean placePendingBlocks(WorldGenLevel level, IVeinCapability depCap, BlockPos origin) {
        ChunkPos cp = new ChunkPos(origin);
        ConcurrentLinkedQueue<VeinCapability.PendingBlock> q = depCap.getPendingBlocks(cp);
        q.forEach(x -> FeatureUtils.enqueueBlockPlacement(level, cp, x.pos(), x.state(), depCap, x.type()));
        depCap.removePendingBlocksForChunk(cp);
        return q.size() > 0;
    }
}