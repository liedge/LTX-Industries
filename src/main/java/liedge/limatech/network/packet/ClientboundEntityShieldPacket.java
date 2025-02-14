package liedge.limatech.network.packet;

import io.netty.buffer.ByteBuf;
import liedge.limatech.LimaTech;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ClientboundEntityShieldPacket(int entityId, float shieldHealth) implements CustomPacketPayload
{
    static final Type<ClientboundEntityShieldPacket> TYPE = LimaTech.RESOURCES.packetType("entity_shield");
    static final StreamCodec<ByteBuf, ClientboundEntityShieldPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ClientboundEntityShieldPacket::entityId,
            ByteBufCodecs.FLOAT, ClientboundEntityShieldPacket::shieldHealth,
            ClientboundEntityShieldPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}