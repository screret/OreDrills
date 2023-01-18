package screret.oredrills.resources;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.registries.ForgeRegistries;
import screret.oredrills.Config;
import screret.oredrills.OreDrills;
import screret.oredrills.capability.vein.IVeinCapability;
import screret.oredrills.capability.world.IChunkGennedCapability;
import screret.oredrills.world.feature.FeatureUtils;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class OreVeinType/* implements INBTSerializable<Tag>*/ {

    public OreVeinType(ResourceLocation id, int genWeight, float density, int minSpawnHeight, int maxSpawnHeight, ResourceLocation[] dimIdFilter, TagKey<Biome> biomeList, boolean isBiomesBlacklist, int sizeXZ, HashSet<BlockState> blockStateMatchers, Map<String, Map<BlockState, Float>> oreBlocks){
        this.id = id;
        this.genWeight = genWeight;
        this.densityPercentage = density;
        this.minSpawnHeight = minSpawnHeight;
        this.maxSpawnHeight = maxSpawnHeight;
        this.dimIdFilter = dimIdFilter;
        this.biomes = biomeList;
        this.isBiomesBlacklist = isBiomesBlacklist;
        this.sizeXZ = sizeXZ;

        this.oreToWeightMap = oreBlocks;
        if (!this.oreToWeightMap.containsKey("default")) {
            throw new RuntimeException("Vein blocks should always have a default key");
        }
        this.blockStateMatchers = blockStateMatchers;

        for (Map.Entry<String, Map<BlockState, Float>> i : this.oreToWeightMap.entrySet()) {
            if (!this.cumulOreWeightMap.containsKey(i.getKey())) {
                this.cumulOreWeightMap.put(i.getKey(), 0.0F);
            }

            for (Map.Entry<BlockState, Float> j : i.getValue().entrySet()) {
                float v = this.cumulOreWeightMap.get(i.getKey());
                this.cumulOreWeightMap.put(i.getKey(), v + j.getValue());
            }

            if (this.cumulOreWeightMap.get(i.getKey()) != 1.0F) {
                throw new RuntimeException("Sum of weights for vein blocks should equal 1.0");
            }
        }
    }

    public ResourceLocation id;

    public final Map<String, Map<BlockState, Float>> oreToWeightMap;
    private final HashSet<BlockState> blockStateMatchers;
    private final HashMap<String, Float> cumulOreWeightMap = new HashMap<>();

    public int genWeight;
    public float densityPercentage;
    public int minSpawnHeight, maxSpawnHeight;
    public ResourceLocation[] dimIdFilter;
    public final TagKey<Biome> biomes;
    public final boolean isBiomesBlacklist;
    public int sizeXZ;


    public boolean canPlaceInBiome(Holder<Biome> biomeHolder){
        return isBiomesBlacklist != biomeHolder.is(biomes);
    }

    @Nullable
    public BlockState getOre(BlockState currentState, RandomSource rand) {
        ResourceLocation res = ForgeRegistries.BLOCKS.getKey(currentState.getBlock());
        if (this.oreToWeightMap.containsKey(res)) {
            // Return a choice from a specialized set here
            Map<BlockState, Float> mp = this.oreToWeightMap.get(res);
            return VeinUtils.pick(mp, this.cumulOreWeightMap.get(res), rand);
        }
        return VeinUtils.pick(this.oreToWeightMap.get("default"), this.cumulOreWeightMap.get("default"), rand);
    }

    public int generate(WorldGenLevel level, BlockPos pos, IVeinCapability veins, IChunkGennedCapability chunksGenerated) {
        /* Dimension checking is done in PlutonRegistry#pick */
        /* Check biome allowance */
        if (!this.canPlaceInBiome(level.getBiome(pos))) {
            return 0;
        }

        int totalPlaced = 0;
        int randY = this.minSpawnHeight + level.getRandom().nextInt(this.maxSpawnHeight - this.minSpawnHeight);
        int max = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, pos.getX(), pos.getZ());
        if (randY > max) {
            randY = Math.max(minSpawnHeight, max);
        }

        float ranFlt = level.getRandom().nextFloat() * (float) Math.PI;
        double x1 = (float) (pos.getX() + 8) + Mth.sin(ranFlt) * (float) this.sizeXZ / 8.0F;
        double x2 = (float) (pos.getX() + 8) - Mth.sin(ranFlt) * (float) this.sizeXZ / 8.0F;
        double z1 = (float) (pos.getZ() + 8) + Mth.cos(ranFlt) * (float) this.sizeXZ / 8.0F;
        double z2 = (float) (pos.getZ() + 8) - Mth.cos(ranFlt) * (float) this.sizeXZ / 8.0F;
        double y1 = randY + level.getRandom().nextInt(3) - 2;
        double y2 = randY + level.getRandom().nextInt(3) - 2;

        for (int i = 0; i < this.sizeXZ; ++i) {
            float radScl = (float) i / (float) this.sizeXZ;
            double xn = x1 + (x2 - x1) * (double) radScl;
            double yn = y1 + (y2 - y1) * (double) radScl;
            double zn = z1 + (z2 - z1) * (double) radScl;
            double noise = level.getRandom().nextDouble() * (double) this.sizeXZ / 16.0D;
            double radius = (double) (Mth.sin((float) Math.PI * radScl) + 1.0F) * noise + 1.0D;
            int xmin = Mth.floor(xn - radius / 2.0D);
            int ymin = Mth.floor(yn - radius / 2.0D);
            int zmin = Mth.floor(zn - radius / 2.0D);
            int xmax = Mth.floor(xn + radius / 2.0D);
            int ymax = Mth.floor(yn + radius / 2.0D);
            int zmax = Mth.floor(zn + radius / 2.0D);

            for (int x = xmin; x <= xmax; ++x) {
                double layerRadX = ((double) x + 0.5D - xn) / (radius / 2.0D);

                if (layerRadX * layerRadX < 1.0D) {
                    for (int y = ymin; y <= ymax; ++y) {
                        double layerRadY = ((double) y + 0.5D - yn) / (radius / 2.0D);

                        if (layerRadX * layerRadX + layerRadY * layerRadY < 1.0D) {
                            for (int z = zmin; z <= zmax; ++z) {
                                double layerRadZ = ((double) z + 0.5D - zn) / (radius / 2.0D);

                                if (layerRadX * layerRadX + layerRadY * layerRadY + layerRadZ * layerRadZ < 1.0D) {
                                    BlockPos placePos = new BlockPos(x, y, z);
                                    BlockState current = level.getBlockState(placePos);
                                    BlockState tmp = this.getOre(current, level.getRandom());
                                    if (tmp == null) {
                                        continue;
                                    }

                                    // Skip this block if it can't replace the target block or doesn't have a
                                    // manually-configured replacer in the blocks object
                                    if (!(this.getBlockStateMatchers().contains(current) || this.oreToWeightMap.containsKey(ForgeRegistries.BLOCKS.getKey(current.getBlock()).toString()))) {
                                        continue;
                                    }

                                    if (FeatureUtils.enqueueBlockPlacement(level, new ChunkPos(pos), placePos, tmp, veins, chunksGenerated, this)) {
                                        totalPlaced++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return totalPlaced;
    }

    public void afterGen(BlockPos pos) {
        // Debug the vein
        if (Config.Common.DEBUG_WORLD_GEN.get()) {
            OreDrills.LOGGER.info("Generated {} in Chunk {} (Pos [{} {} {}])", this.id, new ChunkPos(pos), pos.getX(), pos.getY(), pos.getZ());
        }
    }

    public HashSet<BlockState> getBlockStateMatchers() {
        return this.blockStateMatchers == null ? VeinUtils.getDefaultMatchers() : this.blockStateMatchers;
    }

    public Tag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", this.id.toString());
        tag.putInt("gen_weight", genWeight);
        tag.putFloat("density", densityPercentage);
        tag.putInt("min_height", minSpawnHeight);
        tag.putInt("max_height", maxSpawnHeight);
        tag.putInt("size_xz", sizeXZ);

        ListTag dimIdFilter = new ListTag();
        for(var location : dimIdFilter){
            dimIdFilter.add(StringTag.valueOf(location.toString()));
        }
        tag.put("dim_id_filter", dimIdFilter);
        return tag;
    }

    public void deserializeNBT(Tag nbt) {
        if(nbt.getType() != CompoundTag.TYPE){
            throw new IllegalStateException("Tag was not of type CompoundTag!");
        }
        CompoundTag tag = (CompoundTag)nbt;
        this.id = new ResourceLocation(tag.getString("id"));
        this.genWeight = tag.getInt("gen_weight");
        this.densityPercentage = tag.getFloat("density");
        this.minSpawnHeight = tag.getInt("min_height");
        this.maxSpawnHeight = tag.getInt("max_height");
        this.sizeXZ = tag.getInt("size_xz");

        this.dimIdFilter = tag.getList("dim_id_filter", Tag.TAG_STRING).stream().map(kvp -> new ResourceLocation(kvp.toString())).toArray(ResourceLocation[]::new);
    }
}
