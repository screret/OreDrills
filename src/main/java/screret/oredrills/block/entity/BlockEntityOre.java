package screret.oredrills.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;
import screret.oredrills.block.ModBlockEntities;
import screret.oredrills.capability.vein.VeinCapability;
import screret.oredrills.resources.OreVeinManager;
import screret.oredrills.resources.OreVeinType;

import javax.annotation.Nullable;
import java.util.Objects;

public class BlockEntityOre extends BlockEntity {

    public static final ModelProperty<BlockState> BLOCK_TO_COPY = new ModelProperty<>();
    private BlockState oreToImitate;

    private OreVeinType type;
    public BlockEntityOre(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.ORE.get(), pPos, pBlockState);
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithFullMetadata();
    }

    @Override
    public void setLevel(Level pLevel) {
        super.setLevel(pLevel);
        if(this.type == null && this.level != null) this.setOreType();
        this.load(this.saveWithFullMetadata());
    }

    public void setOreType() {
        var oreTypes = this.level.getCapability(VeinCapability.CAPABILITY).orElseThrow(() -> new IllegalStateException("VeinCapability was not found")).getOreVeins(new ChunkPos(this.worldPosition)).keySet().toArray(OreVeinType[]::new);
        this.type = oreTypes[level.getRandom().nextInt(oreTypes.length - 1)];
    }

    public void setOreType(OreVeinType type) {
        this.type = type;
    }

    public OreVeinType getOreType(){
        return this.type;
    }

    public void onDestroyOre(){
        this.level.getCapability(VeinCapability.CAPABILITY).orElseThrow(() -> new IllegalStateException("VeinCapability was null")).deductOreFromVein(new ChunkPos(this.worldPosition), this.type);
    }

    @Override
    public void saveToItem(ItemStack pStack) {
        super.saveToItem(pStack);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        BlockState oldOre = oreToImitate;
        super.handleUpdateTag(tag);
        if(tag.contains("oreToImitate")) {
            oreToImitate = NbtUtils.readBlockState(this.level.holderLookup(Registries.BLOCK), tag.getCompound("oreToImitate"));
            if(!Objects.equals(oldOre, oreToImitate)) {
                this.requestModelDataUpdate();
                level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    }

    @NotNull
    @Override
    public ModelData getModelData() {
        return ModelData.builder()
                .with(BLOCK_TO_COPY, oreToImitate)
                .build();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putString("type", type == null || type.id == null ? "oredrills:diamond" : type.id.toString());
        if(oreToImitate != null) {
            pTag.put("oreToImitate", NbtUtils.writeBlockState(oreToImitate));
        }
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        type = OreVeinManager.INSTANCE.getAllVeins().get(new ResourceLocation(pTag.getString("type")));
        if (pTag.contains("oreToImitate")) {
            oreToImitate = NbtUtils.readBlockState(this.level.holderLookup(Registries.BLOCK), pTag.getCompound("oreToImitate"));
        }
    }
}
