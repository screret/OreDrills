package screret.oredrills.resources;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import screret.oredrills.OreDrills;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OreVeinManager extends SimpleJsonResourceReloadListener {
    public static OreVeinManager INSTANCE;

    private static final Logger LOGGER = LogManager.getLogger();
    public static final Gson GSON_INSTANCE = Deserializers.createFunctionSerializer().create();
    private static final String folder = "ore_veins";

    private Map<ResourceLocation, OreVeinType> registeredVeins = ImmutableMap.of();

    public OreVeinManager() {
        super(GSON_INSTANCE, folder);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resourceList, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        ImmutableMap.Builder<ResourceLocation, OreVeinType> builder = ImmutableMap.builder();

        for(Map.Entry<ResourceLocation, JsonElement> entry : resourceList.entrySet()) {
            ResourceLocation recipeLocation = entry.getKey();

            try {
                OreVeinType veinType = fromJson(recipeLocation, GsonHelper.convertToJsonObject(entry.getValue(), "top element"));
                builder.put(recipeLocation, veinType);
            } catch (IllegalArgumentException | JsonParseException jsonparseexception) {
                LOGGER.error("Parsing error loading vein type {}", recipeLocation, jsonparseexception);
            }
        }

        this.registeredVeins = builder.build();
        LOGGER.info("Loaded {} recipes", registeredVeins.size());
    }

    public static OreVeinType fromJson(ResourceLocation pVeinId, JsonObject pJson) {
        ResourceLocation oreTexture = new ResourceLocation(pJson.getAsJsonPrimitive("ore_texture").getAsString());
        ResourceLocation result = new ResourceLocation(pJson.getAsJsonPrimitive("mining_drop_loot_table").getAsString());
        int chancePerChunk = pJson.getAsJsonPrimitive("gen_weight").getAsInt();
        float density = pJson.getAsJsonPrimitive("density").getAsFloat();
        int minSpawnHeight = pJson.getAsJsonPrimitive("min_height").getAsInt();
        int maxSpawnHeight = pJson.getAsJsonPrimitive("max_height").getAsInt();
        ResourceLocation[] dimIdFilter = pJson.getAsJsonArray("dim_id_filter").asList().stream().map(jsonElement -> new ResourceLocation(jsonElement.getAsString())).toArray(ResourceLocation[]::new);
        int sizeXZ = pJson.getAsJsonPrimitive("size_xz").getAsInt();
        TagKey<Biome> biomeTag = TagKey.create(Registries.BIOME, new ResourceLocation(pJson.get("biome_filter").getAsString().replace("#", "")));
        boolean isBiomesBlacklist = pJson.getAsJsonPrimitive("is_biomes_blacklist").getAsBoolean();
        return new OreVeinType(pVeinId, oreTexture, result, chancePerChunk, density, minSpawnHeight, maxSpawnHeight, dimIdFilter, biomeTag, isBiomesBlacklist, sizeXZ);
    }

    /**
     * An immutable collection of the registered eye conversions in layered order.
     */
    public Map<ResourceLocation, OreVeinType> getAllVeins() {
        return registeredVeins;
    }

    @Nullable
    public OreVeinType pick(WorldGenLevel level, BlockPos pos) {
        List<OreVeinType> choices = new ArrayList<>(this.getAllVeins().values());
        // Dimension Filtering done here!
        choices.removeIf((dep) -> !dep.canPlaceInBiome(level.getBiome(pos)));

        if (choices.size() == 0) {
            return null;
        }

        int totalWeight = 0;
        for (OreVeinType vein : choices) {
            totalWeight += vein.genWeight;
        }

        int rng = level.getRandom().nextInt(totalWeight);
        for (OreVeinType vein : choices) {
            int genWeight = vein.genWeight;
            if (rng < genWeight) {
                return vein;
            }
            rng -= genWeight;
        }

        OreDrills.LOGGER.error("Could not reach decision on vein to generate at OreVeinRegistry#pick");
        return null;
    }
}

