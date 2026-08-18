package liedge.ltxindustries.network.packet;

import io.netty.buffer.ByteBuf;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limacore.network.ServerboundPayload;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.lib.weapons.LTXIExtendedInput;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundReloadPacket(int slot) implements ServerboundPayload
{
    public static final Type<ServerboundReloadPacket> TYPE = LTXIndustries.RESOURCES.packetType("server_reload");
    public static final StreamCodec<ByteBuf, ServerboundReloadPacket> STREAM_CODEC = LimaStreamCodecs.BYTE_INT.map(ServerboundReloadPacket::new, ServerboundReloadPacket::slot);

    @Override
    public void handleServer(ServerPlayer sender, IPayloadContext context)
    {
        LTXIExtendedInput input = LTXIExtendedInput.of(sender);
        if (input.getSelectedSlot() == this.slot)
        {
            ItemStack stack = sender.getInventory().getItem(slot);
            if (stack.getItem() instanceof WeaponItem weaponItem)
            {
                input.startReload(sender, stack, weaponItem);
            }
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}