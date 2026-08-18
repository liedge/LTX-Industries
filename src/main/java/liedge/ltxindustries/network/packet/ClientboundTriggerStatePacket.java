package liedge.ltxindustries.network.packet;

import io.netty.buffer.ByteBuf;
import liedge.limacore.network.ClientboundPayload;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.ltxindustries.LTXIndustries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundTriggerStatePacket(int slot, boolean holding) implements ClientboundPayload
{
    public static final Type<ClientboundTriggerStatePacket> TYPE = LTXIndustries.RESOURCES.packetType("client_trigger");
    public static final StreamCodec<ByteBuf, ClientboundTriggerStatePacket> STREAM_CODEC = StreamCodec.composite(
            LimaStreamCodecs.BYTE_INT, ClientboundTriggerStatePacket::slot,
            ByteBufCodecs.BOOL, ClientboundTriggerStatePacket::holding,
            ClientboundTriggerStatePacket::new);

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