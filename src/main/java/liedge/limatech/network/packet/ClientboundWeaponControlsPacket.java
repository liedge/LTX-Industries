package liedge.limatech.network.packet;

import liedge.limatech.LimaTech;
import liedge.limatech.item.weapon.WeaponItem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ClientboundWeaponControlsPacket(int playerId, WeaponItem weaponItem, byte action) implements CustomPacketPayload
{
    static final Type<ClientboundWeaponControlsPacket> TYPE = LimaTech.RESOURCES.packetType("client_weapon_controls");
    static final StreamCodec<RegistryFriendlyByteBuf, ClientboundWeaponControlsPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ClientboundWeaponControlsPacket::playerId,
            WeaponItem.STREAM_CODEC, ClientboundWeaponControlsPacket::weaponItem,
            ByteBufCodecs.BYTE, ClientboundWeaponControlsPacket::action,
            ClientboundWeaponControlsPacket::new);

    public static final byte RELOAD_START = 0;
    public static final byte WEAPON_SHOOT = 1;
    public static final byte START_TRIGGER_HOLD = 2;
    public static final byte STOP_TRIGGER_HOLD = 3;

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}