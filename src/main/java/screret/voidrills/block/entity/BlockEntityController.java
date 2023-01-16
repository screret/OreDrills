package screret.voidrills.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import screret.voidrills.block.ModBlockEntities;

public class BlockEntityController extends BlockEntity {
    private boolean isValid;

    public BlockEntityController(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.CONTROLLER.get(), pPos, pBlockState);
    }

    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, BlockEntityController pBlockEntity) {
        pBlockEntity.isValid = updateBase(pLevel, pPos.getX(), pPos.getY(), pPos.getZ());s
    }

    private static boolean updateBase(Level pLevel, int pX, int pY, int pZ) {
        int i = 0;
        boolean isValid = false;

        for(int y = 1; y <= 4; i = y++) {
            int realY = pY - y;
            if (realY < pLevel.getMinBuildHeight()) {
                break;
            }

            boolean flag = true;

            for(int x = pX - y; x <= pX + y && flag; ++x) {
                for(int z = pZ - y; z <= pZ + y; ++z) {
                    if (!pLevel.getBlockState(new BlockPos(x, realY, z)).is(BlockTags.BEACON_BASE_BLOCKS)) {
                        flag = false;
                        break;
                    }
                }
            }

            if (!flag) {
                return false;
            }
        }

        return true;
    }
}
