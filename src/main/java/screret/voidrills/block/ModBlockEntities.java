package screret.voidrills.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import screret.voidrills.VoiDrills;
import screret.voidrills.block.entity.*;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, VoiDrills.MODID);


    public static final RegistryObject<BlockEntityType<BlockEntityEnergyIntake>> ENERGY_INTAKE = BLOCK_ENTITY_TYPES.register("energy_intake", () -> BlockEntityType.Builder.of(BlockEntityEnergyIntake::new, ModBlocks.ENERGY_INTAKE.get()).build(null));
    public static final RegistryObject<BlockEntityType<BlockEntityController>> CONTROLLER = BLOCK_ENTITY_TYPES.register("drill_controller", () -> BlockEntityType.Builder.of(BlockEntityController::new, ModBlocks.CONTROLLER.get()).build(null));

}
