package liedge.limatech.network.packet;

import io.netty.buffer.ByteBuf;
import liedge.limatech.LimaTech;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ClientboundFocusTargetPacket(int playerId, int entityId) implements CustomPacketPayload
{
    static final Type<ClientboundFocusTargetPacket> TYPE = LimaTech.RESOURCES.packetType("focused_target");
    static final StreamCodec<ByteBuf, ClientboundFocusTargetPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ClientboundFocusTargetPacket::playerId,
            ByteBufCodecs.VAR_INT, ClientboundFocusTargetPacket::entityId,
            ClientboundFocusTargetPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}