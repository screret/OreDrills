package screret.oredrills.resources;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.registries.ForgeRegistries;
import screret.oredrills.Config;
import screret.oredrills.OreDrills;
import screret.oredrills.capability.vein.IVeinCapability;
import screret.oredrills.world.feature.FeatureUtils;

public class OreVeinType/* implements INBTSerializable<Tag>*/ {

    public OreVeinType(ResourceLocation id, ResourceLocation oreTexture, ResourceLocation miningResultLootTable, int genWeight, float density, int minSpawnHeight, int maxSpawnHeight, ResourceLocation[] dimIdFilter, TagKey<Biome> biomeList, boolean isBiomesBlacklist, int sizeXZ){
        this.id = id;
        this.oreTexture = oreTexture;
        this.miningResultLootTable = miningResultLootTable;
        this.genWeight = genWeight;
        this.densityPercentage = density;
        this.minSpawnHeight = minSpawnHeight;
        this.maxSpawnHeight = maxSpawnHeight;
        this.dimIdFilter = dimIdFilter;
        this.biomes = biomeList;
        this.isBiomesBlacklist = isBiomesBlacklist;
        this.sizeXZ = sizeXZ;
    }

    public ResourceLocation id;
    public ResourceLocation oreTexture;
    public ResourceLocation miningResultLootTable;
    public int genWeight;
    public float densityPercentage;
    public int minSpawnHeight, maxSpawnHeight;
    public ResourceLocation[] dimIdFilter;
    public TagKey<Biome> biomes;
    public boolean isBiomesBlacklist = true;
    public int sizeXZ;


    public boolean canPlaceInBiome(Holder<Biome> biomeHolder){
        return isBiomesBlacklist != biomeHolder.is(biomes);
    }

    public int generate(WorldGenLevel level, BlockPos pos, IVeinCapability veins) {
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

                                    if (FeatureUtils.enqueueBlockPlacement(level, new ChunkPos(pos), placePos, current, veins, this)) {
                                        totalPlaced++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        OreDrills.LOGGER.debug("" + totalPlaced);
        return totalPlaced;
    }

    public void afterGen(BlockPos pos) {
        // Debug the pluton
        if (Config.Common.DEBUG_WORLD_GEN.get()) {
            OreDrills.LOGGER.info("Generated {} in Chunk {} (Pos [{} {} {}])", this, new ChunkPos(pos), pos.getX(), pos.getY(), pos.getZ());
        }
    }

    public Tag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", this.id.toString());
        tag.putString("ore_texture", oreTexture.toString());
        tag.putString("mining_drop_loot_table", miningResultLootTable.toString());
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
        this.oreTexture = new ResourceLocation(tag.getString("ore_texture"));
        this.miningResultLootTable = new ResourceLocation(tag.getString("mining_drop_loot_table"));
        this.genWeight = tag.getInt("gen_weight");
        this.densityPercentage = tag.getFloat("density");
        this.minSpawnHeight = tag.getInt("min_height");
        this.maxSpawnHeight = tag.getInt("max_height");
        this.sizeXZ = tag.getInt("size_xz");

        this.dimIdFilter = tag.getList("dim_id_filter", Tag.TAG_STRING).stream().map(kvp -> new ResourceLocation(kvp.toString())).toArray(ResourceLocation[]::new);
    }
}
