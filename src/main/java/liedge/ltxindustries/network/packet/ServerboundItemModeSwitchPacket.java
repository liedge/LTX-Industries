package liedge.ltxindustries.network.packet;

import io.netty.buffer.ByteBuf;
import liedge.limacore.network.ServerboundPayload;
import liedge.ltxindustries.LTXIndustries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundItemModeSwitchPacket(int slot, boolean forward) implements ServerboundPayload
{
    public static final Type<ServerboundItemModeSwitchPacket> TYPE = LTXIndustries.RESOURCES.packetType("mode_switch");
    public static final StreamCodec<ByteBuf, ServerboundItemModeSwitchPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ServerboundItemModeSwitchPacket::slot,
            ByteBufCodecs.BOOL, ServerboundItemModeSwitchPacket::forward,
            ServerboundItemModeSwitchPacket::new);

    @Override
    public void handleServer(ServerPlayer sender, IPayloadContext context)
    {
        LTXIServerPacketHandler.handleModeSwitchPacket(this, sender);
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}