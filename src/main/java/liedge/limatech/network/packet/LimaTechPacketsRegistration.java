package liedge.limatech.network.packet;

import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import static liedge.limacore.util.LimaNetworkUtil.serverPacketHandler;

public final class LimaTechPacketsRegistration
{
    private LimaTechPacketsRegistration() {}

    public static void registerPacketHandlers(PayloadRegistrar registrar)
    {
        registrar.playToClient(ClientboundWeaponControlsPacket.TYPE, ClientboundWeaponControlsPacket.STREAM_CODEC, LimaTechClientPacketHandler::handleWeaponsControlPacket);
        registrar.playToClient(ClientboundEntityShieldPacket.TYPE, ClientboundEntityShieldPacket.STREAM_CODEC, LimaTechClientPacketHandler::handleEntityShieldPacket);
        registrar.playToClient(ClientboundPlayerShieldPacket.TYPE, ClientboundPlayerShieldPacket.STREAM_CODEC, LimaTechClientPacketHandler::handlePlayerShieldPacket);
        registrar.playToClient(ClientboundFocusTargetPacket.TYPE, ClientboundFocusTargetPacket.STREAM_CODEC, LimaTechClientPacketHandler::handleFocusTargetPacket);

        registrar.playToServer(ServerboundWeaponControlsPacket.TYPE, ServerboundWeaponControlsPacket.STREAM_CODEC, serverPacketHandler(LimaTechServerPacketHandler::handleWeaponControlsPacket));
        registrar.playToServer(ServerboundItemModeSwitchPacket.TYPE, ServerboundItemModeSwitchPacket.STREAM_CODEC, serverPacketHandler(LimaTechServerPacketHandler::handleModeSwitchPacket));
    }
}