package liedge.limatech.network.packet;

import io.netty.buffer.ByteBuf;
import liedge.limatech.LimaTech;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ServerboundWeaponControlsPacket(byte action) implements CustomPacketPayload
{
    static final Type<ServerboundWeaponControlsPacket> TYPE = LimaTech.RESOURCES.packetType("server_weapon_controls");
    static final StreamCodec<ByteBuf, ServerboundWeaponControlsPacket> STREAM_CODEC = ByteBufCodecs.BYTE.map(ServerboundWeaponControlsPacket::new, ServerboundWeaponControlsPacket::action);

    public static final byte TRIGGER_PRESS = 0;
    public static final byte TRIGGER_RELEASE = 1;
    public static final byte RELOAD_PRESS = 2;

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}