package liedge.ltxindustries.network.packet;

import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import static liedge.limacore.util.LimaNetworkUtil.serverPacketHandler;

public final class LTXIPacketsRegistration
{
    private LTXIPacketsRegistration() {}

    public static void registerPacketHandlers(PayloadRegistrar registrar)
    {
        registrar.playToClient(ClientboundWeaponControlsPacket.TYPE, ClientboundWeaponControlsPacket.STREAM_CODEC, LTXIClientPacketHandler::handleWeaponsControlPacket);
        registrar.playToClient(ClientboundEntityShieldPacket.TYPE, ClientboundEntityShieldPacket.STREAM_CODEC, LTXIClientPacketHandler::handleEntityShieldPacket);
        registrar.playToClient(ClientboundPlayerShieldPacket.TYPE, ClientboundPlayerShieldPacket.STREAM_CODEC, LTXIClientPacketHandler::handlePlayerShieldPacket);
        registrar.playToClient(ClientboundFocusTargetPacket.TYPE, ClientboundFocusTargetPacket.STREAM_CODEC, LTXIClientPacketHandler::handleFocusTargetPacket);

        registrar.playToServer(ServerboundWeaponControlsPacket.TYPE, ServerboundWeaponControlsPacket.STREAM_CODEC, serverPacketHandler(LTXIServerPacketHandler::handleWeaponControlsPacket));
        registrar.playToServer(ServerboundItemModeSwitchPacket.TYPE, ServerboundItemModeSwitchPacket.STREAM_CODEC, serverPacketHandler(LTXIServerPacketHandler::handleModeSwitchPacket));
    }
}