package screret.oredrills.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import screret.oredrills.block.ModBlockEntities;

public class BlockEntityDummy extends BlockEntity {
    public BlockEntityDummy(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.DUMMY_FAKE_BE_DO_NOT_USE.get(), pPos, pBlockState);
    }
}
