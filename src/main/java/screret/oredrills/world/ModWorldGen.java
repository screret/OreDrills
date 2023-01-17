package screret.oredrills.world;

import com.google.common.collect.Lists;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import screret.oredrills.OreDrills;
import screret.oredrills.world.feature.VeinFeature;

import java.util.List;

public class ModWorldGen {
    public static final DeferredRegister<Feature<?>> FEATURE = DeferredRegister.create(Registries.FEATURE, OreDrills.MODID);
    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURE = DeferredRegister.create(Registries.CONFIGURED_FEATURE, OreDrills.MODID);
    public static final DeferredRegister<PlacedFeature> PLACED_FEATURE = DeferredRegister.create(Registries.PLACED_FEATURE, OreDrills.MODID);


    public static final RegistryObject<Feature<NoneFeatureConfiguration>> VEINS = FEATURE.register("veins", () -> new VeinFeature(NoneFeatureConfiguration.CODEC));
    //public static final RegistryObject<ConfiguredFeature<?, ?>> CONFIGURED_VEINS = CONFIGURED_FEATURE.register("veins_configured", () -> new ConfiguredFeature<>(VEINS.get(), NoneFeatureConfiguration.INSTANCE));
    //public static final RegistryObject<PlacedFeature> PLACED_VEINS = PLACED_FEATURE.register("veins_placed", () -> new PlacedFeature(CONFIGURED_VEINS.getHolder().get(), Lists.newArrayList(HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(320)))));
}
