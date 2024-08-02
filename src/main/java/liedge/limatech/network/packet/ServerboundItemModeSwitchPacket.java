package liedge.limatech.network.packet;

import liedge.limacore.network.packet.LimaPlayPacket;
import liedge.limatech.LimaTech;
import liedge.limatech.item.ScrollModeSwitchItem;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundItemModeSwitchPacket(int slot, int delta) implements LimaPlayPacket.ServerboundOnly
{
    static final PacketSpec<ServerboundItemModeSwitchPacket> PACKET_SPEC = LimaTech.RESOURCES.packetSpec("mode_switch", StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ServerboundItemModeSwitchPacket::slot,
            ByteBufCodecs.VAR_INT, ServerboundItemModeSwitchPacket::delta,
            ServerboundItemModeSwitchPacket::new));

    @Override
    public void onReceivedByServer(IPayloadContext context, ServerPlayer sender)
    {
        if (sender.getInventory().selected == slot)
        {
            ItemStack heldItem = sender.getMainHandItem();
            if (heldItem.getItem() instanceof ScrollModeSwitchItem item)
            {
                item.switchItemMode(heldItem, sender, delta);
            }
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return PACKET_SPEC.type();
    }
}