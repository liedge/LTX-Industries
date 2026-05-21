package liedge.ltxindustries.network.packet;

import io.netty.buffer.ByteBuf;
import liedge.limacore.network.ClientboundPayload;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.ltxindustries.LTXIndustries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundTriggerPacket(int slot, boolean holding) implements ClientboundPayload
{
    public static final Type<ClientboundTriggerPacket> TYPE = LTXIndustries.RESOURCES.packetType("client_trigger");
    public static final StreamCodec<ByteBuf, ClientboundTriggerPacket> STREAM_CODEC = StreamCodec.composite(
            LimaStreamCodecs.BYTE_INT, ClientboundTriggerPacket::slot,
            ByteBufCodecs.BOOL, ClientboundTriggerPacket::holding,
            ClientboundTriggerPacket::new);

    @Override
    public void handleClient(IPayloadContext context)
    {
        LTXIClientPacketHandler.handleTriggerPacket(this, context);
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}