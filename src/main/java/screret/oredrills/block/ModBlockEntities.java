package screret.oredrills.block;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegistryObject;
import screret.oredrills.OreDrills;
import screret.oredrills.block.entity.*;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, OreDrills.MODID);


    public static final RegistryObject<BlockEntityType<BlockEntityEnergyIntake>> ENERGY_INTAKE = BLOCK_ENTITY_TYPES.register("energy_input", () -> BlockEntityType.Builder.of(BlockEntityEnergyIntake::new, ModBlocks.ENERGY_INPUT.get()).build(null));
    public static final RegistryObject<BlockEntityType<BlockEntityController>> CONTROLLER = BLOCK_ENTITY_TYPES.register("drill_controller", () -> BlockEntityType.Builder.of(BlockEntityController::new, ModBlocks.CONTROLLER.get()).build(null));
    public static final RegistryObject<BlockEntityType<BlockEntityItemIO>> ITEM_INPUT = BLOCK_ENTITY_TYPES.register("item_input", () -> BlockEntityType.Builder.of((pos, state) -> new BlockEntityItemIO(pos, state, true), ModBlocks.ITEM_INPUT.get()).build(null));
    public static final RegistryObject<BlockEntityType<BlockEntityItemIO>> ITEM_OUTPUT = BLOCK_ENTITY_TYPES.register("item_output", () -> BlockEntityType.Builder.of((pos, state) -> new BlockEntityItemIO(pos, state, false), ModBlocks.ITEM_OUTPUT.get()).build(null));


    public static final RegistryObject<BlockEntityType<BlockEntityDummy>> DUMMY_FAKE_BE_DO_NOT_USE = BLOCK_ENTITY_TYPES.register("dummy", () -> BlockEntityType.Builder.of(BlockEntityDummy::new, Blocks.SCULK).build(null));


    @ObjectHolder(registryName = "minecraft:block_entity_type", value = OreDrills.MODID + ":ore")
    public static final BlockEntityType<BlockEntityOre> ORE = null;


}
