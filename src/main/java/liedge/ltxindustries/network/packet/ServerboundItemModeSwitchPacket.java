package liedge.ltxindustries.network.packet;

import io.netty.buffer.ByteBuf;
import liedge.limacore.network.ServerboundPayload;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.item.ScrollModeSwitchItem;
import liedge.ltxindustries.registry.game.LTXISounds;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
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
        if (sender.getInventory().getSelectedSlot() == slot)
        {
            ItemStack heldItem = sender.getMainHandItem();

            if (heldItem.getItem() instanceof ScrollModeSwitchItem item)
            {
                item.switchItemMode(sender, heldItem, forward);
                sender.playSound(LTXISounds.EQUIPMENT_MODE_SWITCH.get(), 1f, 1f);
            }
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}