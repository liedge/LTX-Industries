package liedge.limatech.network.packet;

import liedge.limacore.network.packet.LimaPlayPacket;
import liedge.limatech.LimaTech;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.registry.LimaTechAttachmentTypes;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundWeaponControlsPacket(byte action) implements LimaPlayPacket.ServerboundOnly
{
    static final PacketSpec<ServerboundWeaponControlsPacket> PACKET_SPEC = LimaTech.RESOURCES.packetSpec(PacketFlow.SERVERBOUND, "server_weapon_controls", ByteBufCodecs.BYTE.map(ServerboundWeaponControlsPacket::new, ServerboundWeaponControlsPacket::action));

    public static final byte TRIGGER_PRESS = 0;
    public static final byte TRIGGER_RELEASE = 1;
    public static final byte RELOAD_PRESS = 2;

    @Override
    public void onReceivedByServer(IPayloadContext context, ServerPlayer sender)
    {
        ItemStack heldItem = sender.getMainHandItem();
        if (heldItem.getItem() instanceof WeaponItem weaponItem)
        {
            sender.getData(LimaTechAttachmentTypes.WEAPON_CONTROLS).asServerControls().handleClientAction(heldItem, sender, weaponItem, action);
        }
    }

    @Override
    public PacketSpec<?> getPacketSpec()
    {
        return PACKET_SPEC;
    }
}