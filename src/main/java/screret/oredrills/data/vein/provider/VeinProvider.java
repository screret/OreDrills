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
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraftforge.common.Tags;
import org.slf4j.Logger;
import screret.oredrills.OreDrills;
import screret.oredrills.data.vein.builder.VeinBuilder;
import screret.oredrills.resources.OreVeinType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
                mcLoc("block/iron_ore"),
                mcLoc("blocks/iron_ore"),
                80,
                0.8f,
                50,
                200,
                new ResourceLocation[] {mcLoc("overworld") },
                BiomeTags.IS_OVERWORLD,
                false,
                30
        ));
        addVein(finished, new OreVeinType(
                loc("gold"),
                mcLoc("block/gold_ore"),
                mcLoc("blocks/gold_ore"),
                60,
                0.5f,
                50,
                200,
                new ResourceLocation[] {mcLoc("overworld") },
                BiomeTags.IS_OVERWORLD,
                false,
                20
        ));
        addVein(finished, new OreVeinType(
                loc("coal"),
                mcLoc("block/coal_ore"),
                mcLoc("blocks/coal_ore"),
                100,
                0.8f,
                0,
                320,
                new ResourceLocation[] {mcLoc("overworld") },
                BiomeTags.IS_OVERWORLD,
                false,
                40
        ));
        addVein(finished, new OreVeinType(
                loc("copper"),
                mcLoc("block/copper_ore"),
                mcLoc("blocks/copper_ore"),
                90,
                0.8f,
                0,
                320,
                new ResourceLocation[] {mcLoc("overworld") },
                BiomeTags.IS_OVERWORLD,
                false,
                40
        ));
        addVein(finished, new OreVeinType(
                loc("emerald"),
                mcLoc("block/emerald_ore"),
                mcLoc("blocks/emerald_ore"),
                30,
                0.4f,
                0,
                320,
                new ResourceLocation[] {mcLoc("overworld") },
                BiomeTags.IS_MOUNTAIN,
                false,
                40
        ));
        addVein(finished, new OreVeinType(
                loc("lapis"),
                mcLoc("block/lapis_ore"),
                mcLoc("blocks/lapis_ore"),
                40,
                0.4f,
                0,
                320,
                new ResourceLocation[] {mcLoc("overworld") },
                BiomeTags.IS_OVERWORLD,
                false,
                40
        ));
        addVein(finished, new OreVeinType(
                loc("redstone"),
                mcLoc("block/redstone_ore"),
                mcLoc("blocks/redstone_ore"),
                60,
                0.9f,
                0,
                320,
                new ResourceLocation[] {mcLoc("overworld") },
                BiomeTags.IS_OVERWORLD,
                false,
                40
        ));
        addVein(finished, new OreVeinType(
                loc("diamond"),
                mcLoc("block/diamond_ore"),
                mcLoc("blocks/diamond_ore"),
                20,
                0.3f,
                0,
                100,
                new ResourceLocation[] {mcLoc("overworld") },
                BiomeTags.IS_OVERWORLD,
                false,
                40
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
