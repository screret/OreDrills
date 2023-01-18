package screret.oredrills;

import com.mojang.logging.LogUtils;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import screret.oredrills.block.ModBlockEntities;
import screret.oredrills.block.ModBlocks;
import screret.oredrills.block.entity.BlockEntityOre;
import screret.oredrills.capability.vein.VeinCapability;
import screret.oredrills.capability.vein.IVeinCapability;
import screret.oredrills.capability.world.ChunkGennedCapability;
import screret.oredrills.capability.world.IChunkGennedCapability;
import screret.oredrills.command.ProspectCommand;
import screret.oredrills.data.vein.provider.VeinProvider;
import screret.oredrills.item.ModItems;
import screret.oredrills.network.packet.SendCapsS2C;
import screret.oredrills.resources.DynamicRegistries;
import screret.oredrills.resources.OreVeinManager;
import screret.oredrills.world.ModWorldGen;

import java.util.*;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(OreDrills.MODID)
public class OreDrills {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "oredrills";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel NETWORK_HANDLER = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public OreDrills() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::gatherData);
        modEventBus.addListener(this::addCreativeTabs);
        modEventBus.addListener(this::registerOre);

        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);

        ModWorldGen.FEATURE.register(modEventBus);
        //ModWorldGen.CONFIGURED_FEATURE.register(modEventBus);
        //ModWorldGen.PLACED_FEATURE.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.Common.COMMON_CONFIG);


        MinecraftForge.EVENT_BUS.addListener(this::registerReloadListeners);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerJoinWorld);
        MinecraftForge.EVENT_BUS.addGenericListener(Level.class, this::attachWorldCaps);

        MinecraftForge.EVENT_BUS.register(this);



        Config.Common.loadConfig(Config.Common.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("oredrills-common.toml"));
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        int id = 0;
        NETWORK_HANDLER.registerMessage(id++, SendCapsS2C.class, SendCapsS2C::encode, SendCapsS2C::decode, SendCapsS2C::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));

    }

    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
    }

    public void registerCommands(RegisterCommandsEvent event){
        ProspectCommand.register(event.getDispatcher());
    }


    public void registerReloadListeners(final AddReloadListenerEvent event){
        OreVeinManager.INSTANCE = new OreVeinManager();
        event.addListener(OreVeinManager.INSTANCE);
    }

    private void onPlayerJoinWorld(final PlayerEvent.PlayerLoggedInEvent event) {
        NETWORK_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()), new SendCapsS2C(event.getEntity().level.getCapability(VeinCapability.CAPABILITY).orElseThrow(() -> new IllegalStateException("VeinCapability is null")).serializeNBT()));
    }

    private static final List<GenerationStep.Decoration> decorations = new LinkedList<>();
    static {
        decorations.add(GenerationStep.Decoration.UNDERGROUND_ORES);
        decorations.add(GenerationStep.Decoration.UNDERGROUND_DECORATION);
    }

    private void gatherData(GatherDataEvent event)
    {
        DataGenerator gen = event.getGenerator();
        PackOutput packOutput = gen.getPackOutput();

        gen.addProvider(event.includeServer(), (DataProvider.Factory<DatapackBuiltinEntriesProvider>) output -> new DatapackBuiltinEntriesProvider(output, event.getLookupProvider(), DynamicRegistries.BUILDER, Set.of(OreDrills.MODID)));
        gen.addProvider(event.includeServer(), new VeinProvider(packOutput));
    }

    public void attachWorldCaps(AttachCapabilitiesEvent<Level> event) {
        try {
            final LazyOptional<IVeinCapability> inst = LazyOptional.of(() -> new VeinCapability(event.getObject()));
            final ICapabilitySerializable<CompoundTag> provider = new ICapabilitySerializable<>() {
                @Override
                public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
                    return VeinCapability.CAPABILITY.orEmpty(cap, inst);

                }

                @Override
                public CompoundTag serializeNBT() {
                    IVeinCapability cap = this.getCapability(VeinCapability.CAPABILITY).orElseThrow(RuntimeException::new);
                    return cap.serializeNBT();
                }

                @Override
                public void deserializeNBT(CompoundTag nbt) {
                    IVeinCapability cap = this.getCapability(VeinCapability.CAPABILITY).orElseThrow(RuntimeException::new);
                    cap.deserializeNBT(nbt);
                }
            };
            event.addCapability(new ResourceLocation(OreDrills.MODID, "veins"), provider);
            event.addListener(inst::invalidate);
        } catch (Exception e) {
            LOGGER.error("OreDrills has faced a fatal error. The game will crash...");
            throw new RuntimeException(e);
        }

        if (event.getObject().isClientSide()) {
            return;
        }

        try {
            final LazyOptional<IChunkGennedCapability> inst = LazyOptional.of(ChunkGennedCapability::new);
            final ICapabilitySerializable<CompoundTag> provider = new ICapabilitySerializable<>() {
                @Override
                public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
                    return ChunkGennedCapability.CAPABILITY.orEmpty(cap, inst);
                }

                @Override
                public CompoundTag serializeNBT() {
                    IChunkGennedCapability cap = this.getCapability(ChunkGennedCapability.CAPABILITY).orElseThrow(RuntimeException::new);
                    return cap.serializeNBT();
                }

                @Override
                public void deserializeNBT(CompoundTag nbt) {
                    IChunkGennedCapability cap = this.getCapability(ChunkGennedCapability.CAPABILITY).orElseThrow(RuntimeException::new);
                    cap.deserializeNBT(nbt);
                }
            };
            event.addCapability(new ResourceLocation(OreDrills.MODID, "chunks_generated"), provider);
            event.addListener(inst::invalidate);
        } catch (Exception e) {
            LOGGER.error("Geolosys has faced a fatal error. The game will crash...");
            throw new RuntimeException(e);
        }
    }

    public void addCreativeTabs(final CreativeModeTabEvent.Register event) {
        event.registerCreativeModeTab(new ResourceLocation(OreDrills.MODID, "spellsandsorcerers"),
                e -> e.icon(() -> new ItemStack(ModItems.CONTROLLER.get()))
                        .title(Component.translatable("itemGroup." + OreDrills.MODID))
                        .displayItems((enabledFeatures, entries, operatorEnabled) -> {
                            entries.accept(ModItems.CONTROLLER.get());
                            entries.accept(ModItems.ITEM_INPUT.get());
                            entries.accept(ModItems.ITEM_OUTPUT.get());
                            entries.accept(ModItems.ENERGY_INPUT.get());
                            entries.accept(ModItems.DRILL_CASING.get());
                            entries.accept(ModItems.DRILL_FRAME_BOX.get());
                        }));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void registerOre(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper -> helper.register(new ResourceLocation(MODID, "ore"), BlockEntityType.Builder.of(BlockEntityOre::new, getAllOreBlocks()).build(null)));
    }

    private static Block[] getAllOreBlocks() {
        return ForgeRegistries.BLOCKS.getEntries().stream()
                .filter(object -> object.getKey().location().getPath().contains("ore"))
                .map(Map.Entry::getValue)
                .toArray(Block[]::new);
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents {

    }
}
