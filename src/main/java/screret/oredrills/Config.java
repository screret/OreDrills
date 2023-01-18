package screret.oredrills;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;

import java.nio.file.Path;
import java.util.List;

public class Config {

    public static final class Common {
        public static ForgeConfigSpec.BooleanValue DEBUG_WORLD_GEN;
        public static ForgeConfigSpec.DoubleValue CHUNK_SKIP_CHANCE;
        public static ForgeConfigSpec.IntValue NUMBER_VEINS_PER_CHUNK;
        public static ForgeConfigSpec.ConfigValue<List<? extends String>> DEFAULT_REPLACEMENT_MATS;


        private static final List<String> stoneIshMaterials = Lists.newArrayList("minecraft:stone", "minecraft:andesite", "minecraft:diorite", "minecraft:granite", "minecraft:netherrack", "minecraft:sandstone", "minecraft:deepslate", "minecraft:tuff", "minecraft:calcite", "minecraft:dripstone_block");

        public static final ForgeConfigSpec COMMON_CONFIG;
        private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

        public static void loadConfig(ForgeConfigSpec spec, Path path) {
            final CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();

            configData.load();
            spec.setConfig(configData);
        }

        private static void init() {
            COMMON_BUILDER.comment("Feature Control").push("Feature Control");
            DEBUG_WORLD_GEN = COMMON_BUILDER.comment("Output info into the logs when generating OreDrills deposits").define("debugWorldgen", false);
            NUMBER_VEINS_PER_CHUNK = COMMON_BUILDER.comment("The number of times OreDrills will attempt to place veins in a given chunk").defineInRange("numVeinsPerChunk", 1, 1, Integer.MAX_VALUE);
            CHUNK_SKIP_CHANCE = COMMON_BUILDER.comment("The upper limit of RNG for generating any vein in a given chunk", "Larger values indicate further distance between veins.").defineInRange("chunkSkipChance", 0.9, 0.0, 1.0);
            DEFAULT_REPLACEMENT_MATS = COMMON_BUILDER.comment("The fallback materials which a Deposit can replace if they're not specified by the deposit itself", "Format: Comma-delimited set of <modid:block> (see default for example)").defineList("defaultReplacementMaterials", stoneIshMaterials, rawName -> rawName instanceof String);
            COMMON_BUILDER.pop();
        }

        static {
            init();
            COMMON_CONFIG = COMMON_BUILDER.build();
        }
    }
}
