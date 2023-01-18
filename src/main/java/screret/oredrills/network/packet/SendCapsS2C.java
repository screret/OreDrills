package screret.oredrills.network.packet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import screret.oredrills.network.ClientNetworkHandler;

import java.util.function.Supplier;

public record SendCapsS2C(CompoundTag tag) {

    public static SendCapsS2C decode(FriendlyByteBuf buf) {
        return new SendCapsS2C(buf.readAnySizeNbt());
    }

    public  void encode(FriendlyByteBuf buffer) {
        buffer.writeNbt(tag);
    }

    public static void handle(SendCapsS2C msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                // Make sure it's only executed on the physical client
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientNetworkHandler.handleCapsS2C(msg, ctx))
        );
        ctx.get().setPacketHandled(true);
    }
}
