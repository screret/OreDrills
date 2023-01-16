package screret.voidrills.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import screret.voidrills.block.ModBlockEntities;

import javax.annotation.Nullable;

public class BlockEntityEnergyIntake extends BlockEntity {
    public static final int MAX_ENERGY_STORAGE = 100000;

    private final EnergyStorage storage = new EnergyStorage(MAX_ENERGY_STORAGE);
    private final LazyOptional<IEnergyStorage> energyOptional = LazyOptional.of(() -> this.storage);

    public BlockEntityEnergyIntake(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.ENERGY_INTAKE.get(), pPos, pBlockState);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction direction) {
        if (capability == ForgeCapabilities.ENERGY){
            return energyOptional.cast();
        }
        return super.getCapability(capability, direction);
    }
}
