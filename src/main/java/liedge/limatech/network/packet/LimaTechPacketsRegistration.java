package liedge.limatech.network.packet;

import net.minecraft.network.protocol.PacketFlow;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class LimaTechPacketsRegistration
{
    private LimaTechPacketsRegistration() {}

    public static void registerPacketHandlers(PayloadRegistrar registrar)
    {
        ClientboundWeaponPacket.PACKET_SPEC.registerPacket(registrar, PacketFlow.CLIENTBOUND);
        ClientboundBubbleShieldPacket.PACKET_SPEC.registerPacket(registrar, PacketFlow.CLIENTBOUND);

        ServerboundWeaponPacket.PACKET_SPEC.registerPacket(registrar, PacketFlow.SERVERBOUND);
        ServerboundItemModeSwitchPacket.PACKET_SPEC.registerPacket(registrar, PacketFlow.SERVERBOUND);
        ServerboundFabricatorCraftPacket.PACKET_SPEC.registerPacket(registrar, PacketFlow.SERVERBOUND);
    }
}