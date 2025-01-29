package liedge.limatech.network.packet;

import io.netty.buffer.ByteBuf;
import liedge.limatech.LimaTech;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ServerboundItemModeSwitchPacket(int slot, int delta) implements CustomPacketPayload
{
    static final Type<ServerboundItemModeSwitchPacket> TYPE = LimaTech.RESOURCES.packetType("mode_switch");
    static final StreamCodec<ByteBuf, ServerboundItemModeSwitchPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ServerboundItemModeSwitchPacket::slot,
            ByteBufCodecs.VAR_INT, ServerboundItemModeSwitchPacket::delta,
            ServerboundItemModeSwitchPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}