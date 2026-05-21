package liedge.ltxindustries.network.packet;

import io.netty.buffer.ByteBuf;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limacore.network.ServerboundPayload;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.lib.weapons.LTXIExtendedInput;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundWeaponSlotPacket(int slot) implements ServerboundPayload
{
    public static final Type<ServerboundWeaponSlotPacket> TYPE = LTXIndustries.RESOURCES.packetType("weapon_slot");
    public static final StreamCodec<ByteBuf, ServerboundWeaponSlotPacket> STREAM_CODEC = LimaStreamCodecs.BYTE_INT.map(ServerboundWeaponSlotPacket::new, ServerboundWeaponSlotPacket::slot);

    @Override
    public void handleServer(ServerPlayer sender, IPayloadContext context)
    {
        LTXIExtendedInput.of(sender).setSelectedSlot(sender, slot);
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}