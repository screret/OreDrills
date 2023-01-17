package screret.oredrills.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import screret.oredrills.block.ModBlockEntities;
import screret.oredrills.container.IOItemStackHandler;

import javax.annotation.Nullable;

public class BlockEntityItemIO extends BlockEntity {
    private static final int MAX_SLOTS = 4;

    private final boolean isInput;
    private final IOItemStackHandler itemHandler;
    private final LazyOptional<IItemHandler> itemOptional;

    public BlockEntityItemIO(BlockPos pPos, BlockState pBlockState, boolean isInput) {
        super(isInput ? ModBlockEntities.ITEM_INPUT.get() : ModBlockEntities.ITEM_OUTPUT.get(), pPos, pBlockState);
        this.isInput = isInput;
        this.itemHandler = new IOItemStackHandler(MAX_SLOTS, isInput);
        this.itemOptional = LazyOptional.of(() -> this.itemHandler);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction direction) {
        if (capability == ForgeCapabilities.ITEM_HANDLER){
            return itemOptional.cast();
        }
        return super.getCapability(capability, direction);
    }
}
