package liedge.ltxindustries.network.packet;

import io.netty.buffer.ByteBuf;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limacore.network.ServerboundPayload;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.lib.weapons.LTXIExtendedInput;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundTriggerPacket(int slot, boolean isRelease) implements ServerboundPayload
{
    public static final Type<ServerboundTriggerPacket> TYPE = LTXIndustries.RESOURCES.packetType("server_trigger");
    public static final StreamCodec<ByteBuf, ServerboundTriggerPacket> STREAM_CODEC = StreamCodec.composite(
            LimaStreamCodecs.BYTE_INT, ServerboundTriggerPacket::slot,
            ByteBufCodecs.BOOL, ServerboundTriggerPacket::isRelease,
            ServerboundTriggerPacket::new);

    @Override
    public void handleServer(ServerPlayer sender, IPayloadContext context)
    {
        LTXIExtendedInput input = LTXIExtendedInput.of(sender);

        if (input.getSelectedSlot() == slot)
        {
            if (isRelease)
            {
                input.stopTriggerHold(sender);
            }
            else
            {
                ItemStack stack = sender.getInventory().getItem(slot);
                if (stack.getItem() instanceof WeaponItem weaponItem)
                {
                    input.pressTrigger(sender, stack, weaponItem);
                    // Always sync trigger state on server trigger press
                    sender.connection.send(new ClientboundTriggerPacket(slot, input.isTriggerHeld()));
                }
            }
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}