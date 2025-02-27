package liedge.limatech.network.packet;

import liedge.limatech.LimaTech;
import liedge.limatech.item.weapon.WeaponItem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ServerboundWeaponControlsPacket(WeaponItem weaponItem, byte action) implements CustomPacketPayload
{
    static final Type<ServerboundWeaponControlsPacket> TYPE = LimaTech.RESOURCES.packetType("server_weapon_controls");
    static final StreamCodec<RegistryFriendlyByteBuf, ServerboundWeaponControlsPacket> STREAM_CODEC = StreamCodec.composite(
            WeaponItem.STREAM_CODEC, ServerboundWeaponControlsPacket::weaponItem,
            ByteBufCodecs.BYTE, ServerboundWeaponControlsPacket::action,
            ServerboundWeaponControlsPacket::new);

    public static final byte TRIGGER_PRESS = 0;
    public static final byte TRIGGER_RELEASE = 1;
    public static final byte RELOAD_PRESS = 2;

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}