package screret.voidrills.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import screret.voidrills.VoiDrills;
import screret.voidrills.block.block.BlockController;
import screret.voidrills.block.block.BlockEnergyIntake;

import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, VoiDrills.MODID);


    public static final RegistryObject<Block> DRILL_CASING = BLOCKS.register("drill_casing", makeBasicMetal());
    public static final RegistryObject<Block> ENERGY_INTAKE = BLOCKS.register("energy_intake", BlockEnergyIntake::new);
    public static final RegistryObject<Block> CONTROLLER = BLOCKS.register("drill_controller", BlockController::new);

    private static Supplier<Block> makeBasicMetal(){
        return () -> new Block(BlockBehaviour.Properties.of(Material.METAL));
    }
}
