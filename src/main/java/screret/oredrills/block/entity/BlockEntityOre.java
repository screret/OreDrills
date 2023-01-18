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
import net.minecraftforge.network.PacketDistributor;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;
import screret.oredrills.OreDrills;
import screret.oredrills.block.ModBlockEntities;
import screret.oredrills.capability.vein.VeinCapability;
import screret.oredrills.network.packet.SendCapsS2C;
import screret.oredrills.resources.OreVeinManager;
import screret.oredrills.resources.OreVeinType;

import javax.annotation.Nullable;
import java.util.Objects;

public class BlockEntityOre extends BlockEntity {

    public static final ModelProperty<BlockState> BLOCK_TO_COPY = new ModelProperty<>();

    private OreVeinType type;
    public BlockEntityOre(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.ORE, pPos, pBlockState);
    }

    @Override
    public void setLevel(Level pLevel) {
        super.setLevel(pLevel);
        if(this.type == null && this.level != null) this.setOreType();
    }

    public void setOreType() {
        var oreTypes = this.level.getCapability(VeinCapability.CAPABILITY).orElseThrow(() -> new IllegalStateException("VeinCapability was not found")).getOreVeins(new ChunkPos(this.worldPosition)).keySet().toArray(OreVeinType[]::new);
        if(oreTypes.length > 1){
            this.type = oreTypes[level.getRandom().nextInt(oreTypes.length - 1)];
        } else if (oreTypes.length == 1) {
            this.type = oreTypes[0];
        }
        this.setChanged();
    }

    public void setOreType(OreVeinType type) {
        this.type = type;
        this.setChanged();
    }

    public OreVeinType getOreType(){
        return this.type;
    }

    @Override
    public void setRemoved() {
        this.level.getCapability(VeinCapability.CAPABILITY).orElseThrow(() -> new IllegalStateException("VeinCapability was null")).deductOreFromVein(new ChunkPos(this.worldPosition), this.type);
        OreDrills.NETWORK_HANDLER.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), 128, this.level.dimension())), new SendCapsS2C(this.level.getCapability(VeinCapability.CAPABILITY).orElseThrow(() -> new IllegalStateException("VeinCapability is null")).serializeNBT()));
        super.setRemoved();
    }

    @Override
    public void saveToItem(ItemStack pStack) {
        super.saveToItem(pStack);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putString("type", type == null || type.id == null ? "oredrills:coal" : type.id.toString());
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        type = OreVeinManager.INSTANCE.getAllVeins().get(new ResourceLocation(pTag.getString("type")));
    }
}
