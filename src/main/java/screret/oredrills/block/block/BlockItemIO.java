package screret.oredrills.block.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;
import screret.oredrills.block.entity.BlockEntityItemIO;

public class BlockItemIO extends BaseEntityBlock {

    private final boolean isInput;

    public BlockItemIO(boolean isInput) {
        super(BlockBehaviour.Properties.of(Material.METAL));
        this.isInput = isInput;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new BlockEntityItemIO(pPos, pState, isInput);
    }
}
