package liedge.ltxindustries.network.packet;

import liedge.limacore.network.ClientboundPayload;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.item.weapon.WeaponItem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundWeaponControlsPacket(int playerId, WeaponItem weaponItem, byte action) implements ClientboundPayload
{
    public static final Type<ClientboundWeaponControlsPacket> TYPE = LTXIndustries.RESOURCES.packetType("client_weapon_controls");
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundWeaponControlsPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ClientboundWeaponControlsPacket::playerId,
            WeaponItem.STREAM_CODEC, ClientboundWeaponControlsPacket::weaponItem,
            ByteBufCodecs.BYTE, ClientboundWeaponControlsPacket::action,
            ClientboundWeaponControlsPacket::new);

    public static final byte RELOAD_START = 0;
    public static final byte WEAPON_SHOOT = 1;
    public static final byte START_TRIGGER_HOLD = 2;
    public static final byte STOP_TRIGGER_HOLD = 3;

    @Override
    public void handleClient(IPayloadContext context)
    {
        LTXIClientPacketHandler.handleWeaponsControlPacket(this);
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}