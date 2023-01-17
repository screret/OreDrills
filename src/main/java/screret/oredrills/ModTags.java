package screret.oredrills;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModTags {

    public static final class Blocks {
        public static final TagKey<Block> STEEL_MACHINE_HULLS = forgeTag("machine_hulls/steel");
        public static final TagKey<Block> STEEL_MACHINE_FRAMES = forgeTag("machine_frames/steel");
        public static final TagKey<Block> ENERGY_INPUT = tag("energy_input");
        public static final TagKey<Block> ITEM_INPUT = tag("item_input");
        public static final TagKey<Block> ITEM_OUTPUT = tag("item_output");


        private static TagKey<Block> tag(String name)
        {
            return BlockTags.create(new ResourceLocation(OreDrills.MODID, name));
        }
        private static TagKey<Block> forgeTag(String name)
        {
            return BlockTags.create(new ResourceLocation("forge", name));
        }
    }
}
