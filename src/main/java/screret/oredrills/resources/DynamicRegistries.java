package screret.oredrills.resources;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.NetherFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.holdersets.NotHolderSet;
import net.minecraftforge.registries.holdersets.OrHolderSet;
import screret.oredrills.OreDrills;
import screret.oredrills.world.ModWorldGen;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class DynamicRegistries {

    private static final ResourceKey<PlacedFeature> VEINS_PLACED = ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(OreDrills.MODID, "veins"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> VEINS_CONFIGURED = ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(OreDrills.MODID, "veins"));
    private static final ResourceKey<BiomeModifier> ADD_VEINS_MODIFIER = ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, new ResourceLocation(OreDrills.MODID, "add_veins"));

    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, context -> context.register(VEINS_CONFIGURED, new ConfiguredFeature<>(ModWorldGen.VEINS.get(), NoneFeatureConfiguration.INSTANCE)))
            .add(Registries.PLACED_FEATURE, context -> context.register(VEINS_PLACED,
                    new PlacedFeature(
                            context.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(VEINS_CONFIGURED),
                            List.of(HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.top()))
                    )
            ))
            /*.add(ForgeRegistries.Keys.BIOME_MODIFIERS, context -> {
                context.register(ADD_VEINS_MODIFIER, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                        new NotHolderSet<Biome>(context.registryLookup(Registries.BIOME).get(), (HolderSet<Biome>) context.registryLookup(Registries.BIOME).get().getOrThrow(Biomes.THE_VOID)),
                        HolderSet.direct(context.lookup(Registries.PLACED_FEATURE).getOrThrow(VEINS_PLACED)),
                        GenerationStep.Decoration.UNDERGROUND_ORES
                ));
            })*/;
}
