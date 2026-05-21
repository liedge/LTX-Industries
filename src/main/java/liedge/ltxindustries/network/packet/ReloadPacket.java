package liedge.ltxindustries.network.packet;

import io.netty.buffer.ByteBuf;
import liedge.limacore.network.ClientboundPayload;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limacore.network.ServerboundPayload;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.lib.weapons.LTXIExtendedInput;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ReloadPacket(int slot) implements ClientboundPayload, ServerboundPayload
{
    public static final Type<ReloadPacket> TYPE = LTXIndustries.RESOURCES.packetType("reload");
    public static final StreamCodec<ByteBuf, ReloadPacket> STREAM_CODEC = LimaStreamCodecs.BYTE_INT.map(ReloadPacket::new, ReloadPacket::slot);

    private void handle(Player player, boolean client)
    {
        LTXIExtendedInput input = LTXIExtendedInput.of(player);

        if (input.getSelectedSlot() == slot)
        {
            ItemStack stack = player.getInventory().getItem(slot);

            if (stack.getItem() instanceof WeaponItem weaponItem)
            {
                if (client)
                    input.getReloadTimer().startTimer(weaponItem.getReloadSpeed(stack));
                else
                    input.startReload(player, stack, weaponItem);
            }
        }
    }

    @Override
    public void handleClient(IPayloadContext context)
    {
        handle(context.player(), true);
    }

    @Override
    public void handleServer(ServerPlayer sender, IPayloadContext context)
    {
        handle(sender, false);
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}