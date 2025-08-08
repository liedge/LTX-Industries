package liedge.ltxindustries.network.packet;

import liedge.limacore.lib.TickTimer;
import liedge.ltxindustries.item.ScrollModeSwitchItem;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.lib.weapons.AbstractWeaponControls;
import liedge.ltxindustries.registry.game.LTXIAttachmentTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

final class LTXIServerPacketHandler
{
    private LTXIServerPacketHandler() {}

    static void handleWeaponControlsPacket(ServerboundWeaponControlsPacket packet, ServerPlayer sender)
    {
        ItemStack heldItem = sender.getMainHandItem();
        if (heldItem.is(packet.weaponItem()))
        {
            sender.getData(LTXIAttachmentTypes.WEAPON_CONTROLS).asServerControls().handleClientAction(heldItem, sender, packet.weaponItem(), packet.action());
        }
    }

    static void handleModeSwitchPacket(ServerboundItemModeSwitchPacket packet, ServerPlayer sender)
    {
        if (sender.getInventory().selected == packet.slot())
        {
            ItemStack heldItem = sender.getMainHandItem();

            if (heldItem.getItem() instanceof ScrollModeSwitchItem item)
            {
                if (heldItem.getItem() instanceof WeaponItem)
                {
                    AbstractWeaponControls controls = sender.getData(LTXIAttachmentTypes.WEAPON_CONTROLS);

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