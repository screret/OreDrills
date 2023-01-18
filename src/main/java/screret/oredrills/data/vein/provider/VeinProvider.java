package screret.oredrills.data.vein.provider;

import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraftforge.common.Tags;
import org.slf4j.Logger;
import screret.oredrills.OreDrills;
import screret.oredrills.data.vein.builder.VeinBuilder;
import screret.oredrills.resources.OreVeinType;
import screret.oredrills.resources.VeinUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class VeinProvider implements DataProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    protected final PackOutput.PathProvider pathProvider;


    public VeinProvider(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "ore_veins");
    }

    private ResourceLocation loc(String path){
        return new ResourceLocation(OreDrills.MODID, path);
    }
    private ResourceLocation mcLoc(String path){
        return new ResourceLocation(path);
    }


    protected void buildCraftingRecipes(Consumer<VeinBuilder.Result> finished) {
        addVein(finished, new OreVeinType(
                loc("iron"),
                80,
                0.8f,
                -48,
                128,
                new ResourceLocation[] {mcLoc("overworld") },
                BiomeTags.IS_OVERWORLD,
                false,
                30,
                VeinUtils.getDefaultMatchers(),
                Map.of("default", Map.of(Blocks.IRON_ORE.defaultBlockState(), 1.0f),
                        "minecraft:deepslate", Map.of(Blocks.DEEPSLATE_IRON_ORE.defaultBlockState(), 1.0f))
        ));
        addVein(finished, new OreVeinType(
                loc("gold"),
                60,
                0.5f,
                -64,
                32,
                new ResourceLocation[] {mcLoc("overworld") },
                BiomeTags.IS_OVERWORLD,
                false,
                20,
                VeinUtils.getDefaultMatchers(),
                Map.of("default", Map.of(Blocks.GOLD_ORE.defaultBlockState(), 0.5f, Blocks.STONE.defaultBlockState(), 0.5f),
                        "minecraft:deepslate", Map.of(Blocks.DEEPSLATE_GOLD_ORE.defaultBlockState(), 0.5f, Blocks.DEEPSLATE.defaultBlockState(), 0.5f))
        ));
        addVein(finished, new OreVeinType(
                loc("coal"),
                100,
                0.8f,
                34,
                78,
                new ResourceLocation[] {mcLoc("overworld") },
                BiomeTags.IS_OVERWORLD,
                false,
                40,
                VeinUtils.getDefaultMatchers(),
                Map.of("default", Map.of(Blocks.COAL_ORE.defaultBlockState(), 0.8f, Blocks.STONE.defaultBlockState(), 0.2f),
                        "minecraft:deepslate", Map.of(Blocks.DEEPSLATE_COAL_ORE.defaultBlockState(), 0.8f, Blocks.DEEPSLATE.defaultBlockState(), 0.2f))
        ));
        addVein(finished, new OreVeinType(
                loc("copper"),
                90,
                0.8f,
                -16,
                90,
                new ResourceLocation[] {mcLoc("overworld") },
                BiomeTags.IS_OVERWORLD,
                false,
                128,
                VeinUtils.getDefaultMatchers(),
                Map.of("default", Map.of(Blocks.COPPER_ORE.defaultBlockState(), 0.8f, Blocks.STONE.defaultBlockState(), 0.2f),
                        "minecraft:deepslate", Map.of(Blocks.DEEPSLATE_COPPER_ORE.defaultBlockState(), 0.8f, Blocks.DEEPSLATE.defaultBlockState(), 0.2f))
        ));
        addVein(finished, new OreVeinType(
                loc("emerald"),
                30,
                0.4f,
                -52,
                100,
                new ResourceLocation[] {mcLoc("overworld") },
                BiomeTags.IS_MOUNTAIN,
                false,
                128,
                VeinUtils.getDefaultMatchers(),
                Map.of("default", Map.of(Blocks.EMERALD_ORE.defaultBlockState(), 0.4f, Blocks.STONE.defaultBlockState(), 0.6f),
                        "minecraft:deepslate", Map.of(Blocks.DEEPSLATE_EMERALD_ORE.defaultBlockState(), 0.4f, Blocks.DEEPSLATE.defaultBlockState(), 0.6f))
        ));
        addVein(finished, new OreVeinType(
                loc("lapis"),
                40,
                0.4f,
                -64,
                64,
                new ResourceLocation[] {mcLoc("overworld") },
                BiomeTags.IS_OVERWORLD,
                false,
                128,
                VeinUtils.getDefaultMatchers(),
                Map.of("default", Map.of(Blocks.LAPIS_ORE.defaultBlockState(), 0.4f, Blocks.STONE.defaultBlockState(), 0.6f),
                        "minecraft:deepslate", Map.of(Blocks.DEEPSLATE_LAPIS_ORE.defaultBlockState(), 0.4f, Blocks.DEEPSLATE.defaultBlockState(), 0.6f))
        ));
        addVein(finished, new OreVeinType(
                loc("redstone"),
                60,
                0.9f,
                -60,
                15,
                new ResourceLocation[] {mcLoc("overworld") },
                BiomeTags.IS_OVERWORLD,
                false,
                128,
                VeinUtils.getDefaultMatchers(),
                Map.of("default", Map.of(Blocks.REDSTONE_ORE.defaultBlockState(), 0.9f, Blocks.STONE.defaultBlockState(), 0.1f),
                        "minecraft:deepslate", Map.of(Blocks.DEEPSLATE_REDSTONE_ORE.defaultBlockState(), 0.9f, Blocks.DEEPSLATE.defaultBlockState(), 0.1f))
        ));
        addVein(finished, new OreVeinType(
                loc("diamond"),
                20,
                0.3f,
                -64,
                10,
                new ResourceLocation[] {mcLoc("overworld") },
                BiomeTags.IS_OVERWORLD,
                false,
                128,
                VeinUtils.getDefaultMatchers(),
                Map.of("default", Map.of(Blocks.DIAMOND_ORE.defaultBlockState(), 0.3f, Blocks.STONE.defaultBlockState(), 0.7f),
                        "minecraft:deepslate", Map.of(Blocks.DEEPSLATE_DIAMOND_ORE.defaultBlockState(), 0.3f, Blocks.DEEPSLATE.defaultBlockState(), 0.7f))
        ));
    }
    protected void addVein(Consumer<VeinBuilder.Result> finished, OreVeinType result){
        VeinBuilder.vein(result)
                .save(finished, result.id);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput pOutput) {
        Set<ResourceLocation> set = Sets.newHashSet();
        List<CompletableFuture<?>> futures = new ArrayList<>();
        buildCraftingRecipes((result) -> {
            if (!set.add(result.getId())) {
                throw new IllegalStateException("Duplicate recipe " + result.getId());
            } else {
                futures.add(DataProvider.saveStable(pOutput, result.serializeRecipe(), this.pathProvider.json(result.getId())));
            }
        });

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

        @Override
    public String getName() {
        return "Ore Veins";
    }
}
