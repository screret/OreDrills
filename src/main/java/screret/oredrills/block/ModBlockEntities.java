package screret.oredrills.block;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import screret.oredrills.OreDrills;
import screret.oredrills.block.entity.*;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, OreDrills.MODID);


    public static final RegistryObject<BlockEntityType<BlockEntityEnergyIntake>> ENERGY_INTAKE = BLOCK_ENTITY_TYPES.register("energy_input", () -> BlockEntityType.Builder.of(BlockEntityEnergyIntake::new, ModBlocks.ENERGY_INTAKE.get()).build(null));
    public static final RegistryObject<BlockEntityType<BlockEntityController>> CONTROLLER = BLOCK_ENTITY_TYPES.register("drill_controller", () -> BlockEntityType.Builder.of(BlockEntityController::new, ModBlocks.CONTROLLER.get()).build(null));
    public static final RegistryObject<BlockEntityType<BlockEntityItemIO>> ITEM_INPUT = BLOCK_ENTITY_TYPES.register("item_input", () -> BlockEntityType.Builder.of((pos, state) -> new BlockEntityItemIO(pos, state, true), ModBlocks.ITEM_INPUT.get()).build(null));
    public static final RegistryObject<BlockEntityType<BlockEntityItemIO>> ITEM_OUTPUT = BLOCK_ENTITY_TYPES.register("item_output", () -> BlockEntityType.Builder.of((pos, state) -> new BlockEntityItemIO(pos, state, false), ModBlocks.ITEM_OUTPUT.get()).build(null));


    public static final RegistryObject<BlockEntityType<BlockEntityOre>> ORE = BLOCK_ENTITY_TYPES.register("ore", () -> BlockEntityType.Builder.of(BlockEntityOre::new, ModBlocks.ORE.get()).build(null));
}
