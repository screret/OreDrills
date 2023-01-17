package screret.oredrills.block.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;
import screret.oredrills.block.ModBlockEntities;

public class BlockEnergyIntake extends BaseEntityBlock {
    public BlockEnergyIntake() {
        super(BlockBehaviour.Properties.of(Material.HEAVY_METAL));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return ModBlockEntities.ENERGY_INTAKE.get().create(pPos, pState);
    }
}
