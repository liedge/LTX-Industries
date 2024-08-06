package liedge.limatech.network.packet;

import liedge.limacore.network.LimaStreamCodecs;
import liedge.limacore.network.packet.LimaPlayPacket;
import liedge.limatech.LimaTech;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.lib.weapons.ClientWeaponControls;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

public record ClientboundWeaponControlsPacket(Optional<Player> player, WeaponItem weaponItem, byte action) implements LimaPlayPacket.ClientboundOnly
{
    static final PacketSpec<ClientboundWeaponControlsPacket> PACKET_SPEC = LimaTech.RESOURCES.packetSpec(PacketFlow.CLIENTBOUND, "client_weapon_controls", StreamCodec.composite(
            LimaStreamCodecs.remoteEntity(Player.class), ClientboundWeaponControlsPacket::player,
            LimaStreamCodecs.ITEM_DIRECT.apply(LimaStreamCodecs.classCastMap(WeaponItem.class)), ClientboundWeaponControlsPacket::weaponItem,
            ByteBufCodecs.BYTE, ClientboundWeaponControlsPacket::action,
            ClientboundWeaponControlsPacket::new));

    public static final byte RELOAD_START = 0;
    public static final byte WEAPON_SHOOT = 1;
    public static final byte START_TRIGGER_HOLD = 2;
    public static final byte STOP_TRIGGER_HOLD = 3;

    @Override
    public void onReceivedByClient(IPayloadContext context, Player ignored)
    {
        if (player.isPresent())
        {
            Player clientPlayer = player.get();
            ItemStack heldItem = clientPlayer.getMainHandItem();
            if (heldItem.is(weaponItem))
            {
                ClientWeaponControls.of(clientPlayer).handleServerAction(heldItem, clientPlayer, weaponItem, action);
            }
        }
    }

    @Override
    public PacketSpec<?> getPacketSpec()
    {
        return PACKET_SPEC;
    }
}