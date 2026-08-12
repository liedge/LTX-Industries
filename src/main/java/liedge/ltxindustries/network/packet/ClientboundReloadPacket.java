package liedge.ltxindustries.network.packet;

import io.netty.buffer.ByteBuf;
import liedge.limacore.network.ClientboundPayload;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.lib.weapons.LTXIExtendedInput;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundReloadPacket(int slot, int duration) implements ClientboundPayload
{
    public static final Type<ClientboundReloadPacket> TYPE = LTXIndustries.RESOURCES.packetType("client_reload");
    public static final StreamCodec<ByteBuf, ClientboundReloadPacket> STREAM_CODEC = StreamCodec.composite(
            LimaStreamCodecs.BYTE_INT, ClientboundReloadPacket::slot,
            LimaStreamCodecs.NON_NEGATIVE_VAR_INT, ClientboundReloadPacket::duration,
            ClientboundReloadPacket::new);

    @Override
    public void handleClient(IPayloadContext context)
    {
        Player player = context.player();
        LTXIExtendedInput input = LTXIExtendedInput.of(player);

        if (input.getSelectedSlot() == slot)
        {
            input.getReloadTimer().startTimer(duration);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}