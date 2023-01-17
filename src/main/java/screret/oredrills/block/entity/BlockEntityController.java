package screret.oredrills.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockMaterialPredicate;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.material.Material;
import org.checkerframework.checker.units.qual.C;
import screret.oredrills.ModTags;
import screret.oredrills.block.ModBlockEntities;
import screret.oredrills.block.ModBlocks;
import screret.oredrills.capability.vein.VeinCapability;
import screret.oredrills.resources.OreVeinType;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class BlockEntityController extends BlockEntity {
    public static final int MAX_ORE_SCAN_DEPTH = 20;

    private static final Predicate<BlockState> CASINGS_IOS_PREDICATE = (state) -> state != null && (state.is(ModTags.Blocks.STEEL_MACHINE_HULLS) || state.is(ModTags.Blocks.ITEM_INPUT) || state.is(ModTags.Blocks.ENERGY_INPUT) || state.is(ModTags.Blocks.ITEM_OUTPUT));
    private static final Predicate<BlockState> FRAMES_PREDICATE = (state) -> state != null && (state.is(ModTags.Blocks.STEEL_MACHINE_FRAMES));

    private boolean isValid = false;
    private boolean isOverVein = false;
    private int tier;

    private Item currentlyMiningOreResult;
    private BlockEntityItemIO inputHatch;
    private BlockEntityItemIO outputHatch;

    @Nullable
    private BlockPattern drillFull;

    public BlockEntityController(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.CONTROLLER.get(), pPos, pBlockState);
    }

    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, BlockEntityController pBlockEntity) {
        int x = pPos.getX();
        int y = pPos.getY();
        int z = pPos.getZ();
        if(pLevel.getGameTime() % 10 == 0){
            pBlockEntity.isValid = pBlockEntity.getOrCreateDrillFull().find(pLevel, pPos) != null;
            var veins = pLevel.getCapability(VeinCapability.CAPABILITY).orElseThrow(() -> new IllegalStateException("VeinCapability doesn't exist")).getOreVeins(new ChunkPos(pPos));
            if(!veins.isEmpty()){
                pBlockEntity.isOverVein = true;
            }
        }
        if (!pBlockEntity.isValid) return;
        if (!pBlockEntity.isOverVein) return;


        int tier = pBlockEntity.tier;

        if(pLevel.getGameTime() % (100 / tier) == 0){

        }
    }

    private BlockPattern getOrCreateDrillFull() {
        if (this.drillFull == null) {
            this.drillFull = BlockPatternBuilder.start()
                    .aisle(  "###",
                                    "###",
                                    "#^#"
                    ).aisle( "~H~",
                                    "H#H",
                                    "~H~"
                    ).aisle( "~H~",
                                    "H#H",
                                    "~H~"
                    ).aisle( "~H~",
                                    "H#H",
                                    "~H~"
                    ).aisle( "~~~",
                                    "~H~",
                                    "~~~"
                    ).aisle( "~~~",
                                    "~H~",
                                    "~~~"
                    ).where('^', BlockInWorld.hasState(BlockStatePredicate.forBlock(ModBlocks.CONTROLLER.get())))
                    .where('#', BlockInWorld.hasState(CASINGS_IOS_PREDICATE))
                    .where('H', BlockInWorld.hasState(FRAMES_PREDICATE))
                    .where('~', BlockInWorld.hasState(BlockMaterialPredicate.forMaterial(Material.AIR))).build();
        }

        return this.drillFull;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putInt("tier", tier);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.tier = pTag.getInt("tier");
    }
}
