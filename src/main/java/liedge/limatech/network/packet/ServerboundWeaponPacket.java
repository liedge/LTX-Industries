package liedge.limatech.network.packet;

import liedge.limacore.network.packet.LimaPlayPacket;
import liedge.limatech.LimaTech;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.registry.LimaTechAttachmentTypes;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundWeaponPacket(byte action) implements LimaPlayPacket.ServerboundOnly
{
    static final PacketSpec<ServerboundWeaponPacket> PACKET_SPEC = LimaTech.RESOURCES.packetSpec("server_input", ByteBufCodecs.BYTE.map(ServerboundWeaponPacket::new, ServerboundWeaponPacket::action));

    public static final byte TRIGGER_PRESS = 0;
    public static final byte TRIGGER_RELEASE = 1;
    public static final byte RELOAD_PRESS = 2;

    @Override
    public void onReceivedByServer(IPayloadContext context, ServerPlayer sender)
    {
        ItemStack heldItem = sender.getMainHandItem();
        if (heldItem.getItem() instanceof WeaponItem weaponItem)
        {
            sender.getData(LimaTechAttachmentTypes.WEAPON_INPUT).handleClientAction(heldItem, sender, weaponItem, action);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return PACKET_SPEC.type();
    }
}