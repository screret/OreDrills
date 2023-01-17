package screret.oredrills.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import screret.oredrills.OreDrills;
import screret.oredrills.block.ModBlocks;

import java.util.function.Supplier;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, OreDrills.MODID);


    public static final RegistryObject<Item> DRILL_CASING = ITEMS.register("drill_casing", makeBasicBI(ModBlocks.DRILL_CASING));
    public static final RegistryObject<Item> DRILL_FRAME_BOX = ITEMS.register("drill_frame_box", makeBasicBI(ModBlocks.DRILL_FRAME_BOX));
    public static final RegistryObject<Item> ENERGY_INPUT = ITEMS.register("energy_intake", makeBasicBI(ModBlocks.DRILL_CASING));
    public static final RegistryObject<Item> ITEM_INPUT = ITEMS.register("item_input", makeBasicBI(ModBlocks.ITEM_INPUT));
    public static final RegistryObject<Item> ITEM_OUTPUT = ITEMS.register("item_output", makeBasicBI(ModBlocks.ITEM_OUTPUT));
    public static final RegistryObject<Item> CONTROLLER = ITEMS.register("drill_controller", makeBasicBI(ModBlocks.CONTROLLER));

    public static final RegistryObject<Item> ORE = ITEMS.register("ore", makeBasicBI(ModBlocks.ORE));

    private static Supplier<BlockItem> makeBasicBI(RegistryObject<Block> block){
        return () -> new BlockItem(block.get(), new Item.Properties());
    }
}
