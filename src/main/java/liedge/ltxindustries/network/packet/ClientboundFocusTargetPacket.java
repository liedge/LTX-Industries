package liedge.ltxindustries.network.packet;

import io.netty.buffer.ByteBuf;
import liedge.limacore.network.ClientboundPayload;
import liedge.ltxindustries.LTXIndustries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundFocusTargetPacket(int playerId, int entityId) implements ClientboundPayload
{
    public static final Type<ClientboundFocusTargetPacket> TYPE = LTXIndustries.RESOURCES.packetType("focused_target");
    public static final StreamCodec<ByteBuf, ClientboundFocusTargetPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ClientboundFocusTargetPacket::playerId,
            ByteBufCodecs.VAR_INT, ClientboundFocusTargetPacket::entityId,
            ClientboundFocusTargetPacket::new);

    @Override
    public void handleClient(IPayloadContext context)
    {
        LTXIClientPacketHandler.handleFocusTargetPacket(this);
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}