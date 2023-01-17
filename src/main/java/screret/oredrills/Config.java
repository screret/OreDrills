package screret.oredrills;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.nio.file.Path;
import java.util.List;

public class Config {

    public static final class Common {
        public static ForgeConfigSpec.BooleanValue DEBUG_WORLD_GEN;
        public static ForgeConfigSpec.BooleanValue REMOVE_VEIN_ORES;
        public static ForgeConfigSpec.DoubleValue CHUNK_SKIP_CHANCE;
        public static ForgeConfigSpec.IntValue NUMBER_VEINS_PER_CHUNK;

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
            REMOVE_VEIN_ORES = COMMON_BUILDER.comment("Setting this to true will flat out replace all Vein blocks (Copper + Granite Veins and Iron + Tuff Veins)", "Otherwise, setting to false will allow you to replace vanilla ores associated with ore veins with OreDrills equivalents.").define("removeVeinOres", false);
            NUMBER_VEINS_PER_CHUNK = COMMON_BUILDER.comment("The number of times OreDrills will attempt to place veins in a given chunk").defineInRange("numVeinsPerChunk", 2, 1, Integer.MAX_VALUE);
            CHUNK_SKIP_CHANCE = COMMON_BUILDER.comment("The upper limit of RNG for generating any vein in a given chunk", "Larger values indicate further distance between veins.").defineInRange("chunkSkipChance", 0.9, 0.0, 1.0);
            COMMON_BUILDER.pop();
        }

        static {
            init();
            COMMON_CONFIG = COMMON_BUILDER.build();
        }
    }
}
