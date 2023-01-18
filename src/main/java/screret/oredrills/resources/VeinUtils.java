package screret.oredrills.resources;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import screret.oredrills.Config;
import screret.oredrills.OreDrills;

import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;

public class VeinUtils {
    private static HashSet<BlockState> defaultMatchersCached = null;

    /**
     * picks a choice out of a mapping between blockstate to weight passing -1.0F as
     * totl will result in a total being calculated.
     *
     * @param map  the map between a blockstate and its chance
     * @param total the total of all chances
     * @return null if no block should be used or placed, T instanceof BlockState if
     * actual block should be placed.
     */
    @Nullable
    public static BlockState pick(Map<BlockState, Float> map, float total, RandomSource random) {
        if (total == 1.0F) {
            total = 0;
            for (Entry<BlockState, Float> e : map.entrySet()) {
                total += e.getValue();
            }
        }

        float rng = random.nextFloat();
        for (Entry<BlockState, Float> e : map.entrySet()) {
            float wt = e.getValue();
            if (rng < wt) {
                return e.getKey();
            }
            rng -= wt;
        }

        OreDrills.LOGGER.error("Could not reach decision on block to place at Utils#pick");
        return null;
    }

    @SuppressWarnings("unchecked")
    public static HashSet<BlockState> getDefaultMatchers() {
        // If the cached data isn't there yet, load it.
        if (defaultMatchersCached == null) {
            defaultMatchersCached = new HashSet<>();
            Config.Common.DEFAULT_REPLACEMENT_MATS.get().forEach(s -> {
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(s));
                if (block == null || !addDefaultMatcher(block)) {
                    OreDrills.LOGGER.warn("{} is not a valid block. Please verify.", s);
                }
            });
        }

        return (HashSet<BlockState>) defaultMatchersCached.clone();
    }

    public static boolean addDefaultMatcher(Block block) {
        BlockState defaultState = block.defaultBlockState();
        if (!defaultState.isAir()) {
            defaultMatchersCached.add(defaultState);
            return true;
        }
        return false;
    }

    public static @NotNull BlockState fromString(@Nullable String string) {
        if (string == null) {
            return Blocks.AIR.defaultBlockState();
        }
        ResourceLocation r = new ResourceLocation(string);
        return Objects.requireNonNull(ForgeRegistries.BLOCKS.getValue(r)).defaultBlockState();
    }

    public static HashMap<BlockState, Float> buildMultiBlockMap(JsonArray arr) {
        HashMap<BlockState, Float> ret = new HashMap<BlockState, Float>();

        for (JsonElement j : arr) {
            JsonObject pair = j.getAsJsonObject();
            if (pair.get("block").isJsonNull()) {
                ret.put(null, pair.get("chance").getAsFloat());
            } else {
                ret.put(fromString(pair.get("block").getAsString()), pair.get("chance").getAsFloat());
            }
        }

        return ret;
    }

    public static JsonArray deconstructMultiBlockMap(Map<BlockState, Float> in) {
        JsonArray ret = new JsonArray();

        for (Entry<BlockState, Float> e : in.entrySet()) {
            JsonObject obj = new JsonObject();
            obj.addProperty("block", ForgeRegistries.BLOCKS.getKey(e.getKey().getBlock()).toString());
            obj.addProperty("chance", e.getValue());
            ret.add(obj);
        }

        return ret;
    }

    public static HashMap<String, Map<BlockState, Float>> buildMultiBlockMatcherMap(JsonObject obj) {
        HashMap<String, Map<BlockState, Float>> ret = new HashMap<>();

        obj.keySet().forEach((key) -> {
            HashMap<BlockState, Float> value = buildMultiBlockMap(obj.get(key).getAsJsonArray());
            ret.put(key, value);
        });

        return ret;
    }

    public static JsonObject deconstructMultiBlockMatcherMap(Map<String, Map<BlockState, Float>> in) {
        JsonObject ret = new JsonObject();

        for (Entry<String, Map<BlockState, Float>> i : in.entrySet()) {
            String key = i.getKey();
            JsonArray value = deconstructMultiBlockMap(i.getValue());
            ret.add(key, value);
        }

        return ret;
    }

    public static String[] toStringArray(JsonArray arr) {
        String[] ret = new String[arr.size()];

        for (int i = 0; i < arr.size(); i++) {
            ret[i] = arr.get(i).getAsString();
        }

        return ret;
    }

    public static HashSet<BlockState> toBlockStateList(JsonArray arr) {
        HashSet<BlockState> ret = new HashSet<BlockState>();

        for (String s : toStringArray(arr)) {
            ret.add(fromString(s));
        }

        return ret;
    }
}
