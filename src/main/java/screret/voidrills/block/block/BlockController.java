package screret.voidrills.block.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;
import screret.voidrills.block.ModBlockEntities;
import screret.voidrills.block.entity.BlockEntityController;

public class BlockController extends BaseEntityBlock {
    public BlockController() {
        super(BlockBehaviour.Properties.of(Material.HEAVY_METAL));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return ModBlockEntities.CONTROLLER.get().create(pPos, pState);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if(!pLevel.isClientSide){
            return createTickerHelper(pBlockEntityType, ModBlockEntities.CONTROLLER.get(), BlockEntityController::serverTick);
        }
        return null;
    }
}
