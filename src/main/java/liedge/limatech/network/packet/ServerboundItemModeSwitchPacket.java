package liedge.limatech.network.packet;

import liedge.limacore.lib.TickTimer;
import liedge.limacore.network.packet.LimaPlayPacket;
import liedge.limatech.LimaTech;
import liedge.limatech.item.ScrollModeSwitchItem;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.registry.LimaTechAttachmentTypes;
import liedge.limatech.lib.weapons.AbstractWeaponControls;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundItemModeSwitchPacket(int slot, int delta) implements LimaPlayPacket.ServerboundOnly
{
    static final PacketSpec<ServerboundItemModeSwitchPacket> PACKET_SPEC = LimaTech.RESOURCES.packetSpec(PacketFlow.SERVERBOUND, "mode_switch", StreamCodec.composite(
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
                if (heldItem.getItem() instanceof WeaponItem)
                {
                    AbstractWeaponControls controls = sender.getData(LimaTechAttachmentTypes.WEAPON_CONTROLS);

                    if (controls.getModeSwitchCooldownTimer().getTimerState() == TickTimer.State.STOPPED)
                    {
                        controls.getModeSwitchCooldownTimer().startTimer(5);
                        item.switchItemMode(heldItem, sender, delta);
                    }
                }
                else
                {
                    item.switchItemMode(heldItem, sender, delta);
                }
            }
        }
    }

    @Override
    public PacketSpec<?> getPacketSpec()
    {
        return PACKET_SPEC;
    }
}