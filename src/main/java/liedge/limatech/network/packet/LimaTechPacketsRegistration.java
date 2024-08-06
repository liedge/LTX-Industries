package liedge.limatech.network.packet;

import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class LimaTechPacketsRegistration
{
    private LimaTechPacketsRegistration() {}

    public static void registerPacketHandlers(PayloadRegistrar registrar)
    {
        ClientboundWeaponControlsPacket.PACKET_SPEC.register(registrar);
        ClientboundBubbleShieldPacket.PACKET_SPEC.register(registrar);

        ServerboundWeaponControlsPacket.PACKET_SPEC.register(registrar);
        ServerboundItemModeSwitchPacket.PACKET_SPEC.register(registrar);
    }
}