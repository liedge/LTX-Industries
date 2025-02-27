package liedge.limatech.network.packet;

import liedge.limacore.lib.TickTimer;
import liedge.limatech.item.ScrollModeSwitchItem;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.lib.weapons.AbstractWeaponControls;
import liedge.limatech.registry.LimaTechAttachmentTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

final class LimaTechServerPacketHandler
{
    private LimaTechServerPacketHandler() {}

    public static void handleWeaponControlsPacket(ServerboundWeaponControlsPacket packet, IPayloadContext context, ServerPlayer sender)
    {
        ItemStack heldItem = sender.getMainHandItem();
        if (heldItem.is(packet.weaponItem()))
        {
            sender.getData(LimaTechAttachmentTypes.WEAPON_CONTROLS).asServerControls().handleClientAction(heldItem, sender, packet.weaponItem(), packet.action());
        }
    }

    public static void handleModeSwitchPacket(ServerboundItemModeSwitchPacket packet, IPayloadContext context, ServerPlayer sender)
    {
        if (sender.getInventory().selected == packet.slot())
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
                        item.switchItemMode(heldItem, sender, packet.delta());
                    }
                }
                else
                {
                    item.switchItemMode(heldItem, sender, packet.delta());
                }
            }
        }
    }
}