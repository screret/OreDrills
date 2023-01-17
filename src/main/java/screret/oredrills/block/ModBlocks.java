package screret.oredrills.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import screret.oredrills.OreDrills;
import screret.oredrills.block.block.BlockController;
import screret.oredrills.block.block.BlockEnergyIntake;
import screret.oredrills.block.block.BlockItemIO;
import screret.oredrills.block.block.BlockOre;

import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, OreDrills.MODID);


    public static final RegistryObject<Block> DRILL_CASING = BLOCKS.register("drill_casing", makeBasicMetal());
    public static final RegistryObject<Block> DRILL_FRAME_BOX = BLOCKS.register("drill_frame_box", makeBasicMetal());
    public static final RegistryObject<Block> ENERGY_INTAKE = BLOCKS.register("energy_input", BlockEnergyIntake::new);
    public static final RegistryObject<Block> ITEM_INPUT = BLOCKS.register("item_input", () -> new BlockItemIO(true));
    public static final RegistryObject<Block> ITEM_OUTPUT = BLOCKS.register("item_output", () -> new BlockItemIO(false));
    public static final RegistryObject<Block> CONTROLLER = BLOCKS.register("drill_controller", BlockController::new);


    public static final RegistryObject<Block> ORE = BLOCKS.register("ore", BlockOre::new);

    private static Supplier<Block> makeBasicMetal(){
        return () -> new Block(BlockBehaviour.Properties.of(Material.METAL));
    }
}
