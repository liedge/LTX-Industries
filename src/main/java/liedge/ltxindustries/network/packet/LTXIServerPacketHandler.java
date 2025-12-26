package liedge.ltxindustries.network.packet;

import liedge.ltxindustries.item.ScrollModeSwitchItem;
import liedge.ltxindustries.lib.weapons.ServerExtendedInput;
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
            ServerExtendedInput.of(sender).handleClientAction(heldItem, sender, packet.weaponItem(), packet.action());
        }
    }

    static void handleModeSwitchPacket(ServerboundItemModeSwitchPacket packet, ServerPlayer sender)
    {
        if (sender.getInventory().selected == packet.slot())
        {
            ItemStack heldItem = sender.getMainHandItem();

            if (heldItem.getItem() instanceof ScrollModeSwitchItem item)
            {
                item.switchItemMode(sender, heldItem, packet.forward());
            }
        }
    }
}