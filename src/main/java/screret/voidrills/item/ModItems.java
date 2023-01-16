package screret.voidrills.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import screret.voidrills.VoiDrills;
import screret.voidrills.block.ModBlocks;

import java.util.function.Supplier;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, VoiDrills.MODID);


    public static final RegistryObject<Item> DRILL_CASING = ITEMS.register("drill_casing", makeBasicBI(ModBlocks.DRILL_CASING));
    public static final RegistryObject<Item> ENERGY_INTAKE = ITEMS.register("energy_intake", makeBasicBI(ModBlocks.DRILL_CASING));

    private static Supplier<BlockItem> makeBasicBI(RegistryObject<Block> block){
        return () -> new BlockItem(block.get(), new Item.Properties());
    }
}
