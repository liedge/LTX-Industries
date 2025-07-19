package liedge.ltxindustries.network.packet;

import liedge.limacore.lib.TickTimer;
import liedge.ltxindustries.item.ScrollModeSwitchItem;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.lib.weapons.AbstractWeaponControls;
import liedge.ltxindustries.registry.game.LTXIAttachmentTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

final class LTXIServerPacketHandler
{
    private LTXIServerPacketHandler() {}

    public static void handleWeaponControlsPacket(ServerboundWeaponControlsPacket packet, IPayloadContext context, ServerPlayer sender)
    {
        ItemStack heldItem = sender.getMainHandItem();
        if (heldItem.is(packet.weaponItem()))
        {
            sender.getData(LTXIAttachmentTypes.WEAPON_CONTROLS).asServerControls().handleClientAction(heldItem, sender, packet.weaponItem(), packet.action());
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