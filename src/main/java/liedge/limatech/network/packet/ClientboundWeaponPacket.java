package liedge.limatech.network.packet;

import liedge.limacore.network.LimaStreamCodecs;
import liedge.limacore.network.packet.LimaPlayPacket;
import liedge.limatech.LimaTech;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.lib.weapons.LocalWeaponInput;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundWeaponPacket(WeaponItem weaponItem, byte action) implements LimaPlayPacket.ClientboundOnly
{
    static final PacketSpec<ClientboundWeaponPacket> PACKET_SPEC = LimaTech.RESOURCES.packetSpec("client_input", StreamCodec.composite(
            LimaStreamCodecs.ITEM_DIRECT.apply(LimaStreamCodecs.classCastMap(WeaponItem.class)), ClientboundWeaponPacket::weaponItem,
            ByteBufCodecs.BYTE, ClientboundWeaponPacket::action,
            ClientboundWeaponPacket::new));

    public static final byte RELOAD_START = 0;
    public static final byte WEAPON_SHOOT = 1;

    @Override
    public void onReceivedByClient(IPayloadContext context, Player player)
    {
        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.getItem() == weaponItem)
        {
            LocalWeaponInput.LOCAL_WEAPON_INPUT.handleServerAction(heldItem, player, weaponItem, action);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return PACKET_SPEC.type();
    }
}