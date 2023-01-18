package screret.oredrills.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import screret.oredrills.OreDrills;
import screret.oredrills.capability.vein.VeinCapability;
import screret.oredrills.network.packet.SendCapsS2C;

import java.util.function.Supplier;

public class ClientNetworkHandler {

    public static void handleCapsS2C(SendCapsS2C packet, Supplier<NetworkEvent.Context> ctx) {
        Minecraft.getInstance().level.getCapability(VeinCapability.CAPABILITY).orElseThrow(() -> new IllegalStateException("VeinCapability is null")).deserializeNBT(packet.tag());
        OreDrills.LOGGER.debug("handled ore veins");
    }
}
