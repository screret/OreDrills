package screret.oredrills.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import screret.oredrills.block.entity.BlockEntityDummy;
import screret.oredrills.block.entity.BlockEntityOre;

@Mixin(DropExperienceBlock.class)
public class MixinDropExperienceBlock implements EntityBlock {

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return ForgeRegistries.BLOCKS.getKey(pState.getBlock()).getPath().contains("ore") ? new BlockEntityOre(pPos, pState) : new BlockEntityDummy(pPos, pState);
    }
}
