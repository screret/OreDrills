package screret.oredrills.data.vein.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import screret.oredrills.resources.OreVeinType;
import screret.oredrills.resources.VeinUtils;

import java.util.Arrays;
import java.util.function.Consumer;

public class VeinBuilder {
    private final OreVeinType result;
    public VeinBuilder(OreVeinType pResult) {
        this.result = pResult;
    }

    /**
     * Creates a new builder for an ore vein.
     */
    public static VeinBuilder vein(OreVeinType pResult) {
        return new VeinBuilder(pResult);
    }

    public OreVeinType getResult() {
        return this.result;
    }

    public void save(Consumer<Result> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {
        this.ensureValid(pRecipeId);
        pFinishedRecipeConsumer.accept(new VeinBuilder.Result(pRecipeId, this.result));
    }

    /**
     * Makes sure that this recipe is valid and obtainable.
     */
    private void ensureValid(ResourceLocation pId) {
        if (result.sizeXZ <= 0 ||
                result.minSpawnHeight > result.maxSpawnHeight ||
                result.biomes == null ||
                result.id == null ||
                result.dimIdFilter == null ||
                result.genWeight == 0) {
            throw new IllegalArgumentException("Something's wrong with vein " + pId);
        }
    }

    public static class Result {
        private final ResourceLocation id;
        private final OreVeinType result;

        public Result(ResourceLocation id, OreVeinType result) {
            this.id = id;
            this.result = result;
        }

        public void serializeVeinData(JsonObject pJson) {
            pJson.addProperty("gen_weight", result.genWeight);
            pJson.addProperty("density", result.densityPercentage);
            pJson.addProperty("min_height", result.minSpawnHeight);
            pJson.addProperty("max_height", result.maxSpawnHeight);
            JsonArray dimFilter = new JsonArray();
            Arrays.stream(result.dimIdFilter).forEach(x -> dimFilter.add(x.toString()));
            pJson.add("dim_id_filter", dimFilter);
            pJson.addProperty("biome_filter", "#" + result.biomes.location());
            pJson.addProperty("is_biomes_blacklist", result.isBiomesBlacklist);
            pJson.addProperty("size_xz", result.sizeXZ);
            pJson.add("blocks", VeinUtils.deconstructMultiBlockMatcherMap(result.oreToWeightMap));
        }

        public JsonObject serializeRecipe() {
            JsonObject jsonobject = new JsonObject();
            this.serializeVeinData(jsonobject);
            return jsonobject;
        }

        public RecipeSerializer<?> getType() {
            return RecipeSerializer.SHAPED_RECIPE;
        }

        /**
         * Gets the ID for the recipe.
         */
        public ResourceLocation getId() {
            return this.id;
        }
    }
}  