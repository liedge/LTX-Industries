package liedge.limatech.lib.weapons;

import liedge.limacore.capability.energy.LimaEnergyUtil;
import liedge.limacore.lib.TickTimer;
import liedge.limacore.util.LimaCoreUtil;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.network.packet.ClientboundWeaponControlsPacket;
import liedge.limatech.network.packet.ServerboundWeaponControlsPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public class ServerWeaponControls extends AbstractWeaponControls
{
    private boolean reloadFlag;

    public ServerWeaponControls()
    {
        getReloadTimer().withStopCallback(success -> reloadFlag = success);
    }

    private void sendPacketToClient(Player player, WeaponItem weaponItem, byte action)
    {
        LimaCoreUtil.castOrThrow(ServerPlayer.class, player).connection.send(new ClientboundWeaponControlsPacket(player.getId(), weaponItem, action));
    }

    /**
     * Finishes the reload process. Ammo item or energy unit consumption happens here. This is only ran on the server side.
     * @param heldItem The item stack of the weapon item
     * @param player The player
     * @param weaponItem The weapon item
     * @return Whether the reload can finish. If true, weapon ammo will be reloaded
     */
    private boolean finalizeReload(ItemStack heldItem, Player player, WeaponItem weaponItem)
    {
        WeaponAmmoSource source = WeaponItem.getAmmoSourceFromItem(heldItem);
        if (source == WeaponAmmoSource.INFINITE)
        {
            return true;
        }
        else if (source == WeaponAmmoSource.COMMON_ENERGY_UNIT)
        {
            IEnergyStorage weaponEnergy = heldItem.getCapability(Capabilities.EnergyStorage.ITEM);
            return weaponEnergy != null && LimaEnergyUtil.consumeEnergy(weaponEnergy, weaponItem.getEnergyReloadCost(heldItem), true);
        }
        else
        {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++)
            {
                ItemStack invItem = player.getInventory().getItem(i);
                if (invItem.is(weaponItem.getAmmoItem(heldItem)))
                {
                    player.getInventory().removeItem(i, 1);
                    return true;
                }
            }

            return false;
        }
    }

    public void handleClientAction(ItemStack heldItem, ServerPlayer player, WeaponItem weaponItem, byte clientAction)
    {
        switch (clientAction)
        {
            case ServerboundWeaponControlsPacket.TRIGGER_PRESS -> pressTrigger(heldItem, player, weaponItem);
            case ServerboundWeaponControlsPacket.TRIGGER_RELEASE -> releaseTrigger(heldItem, weaponItem, player, true);
            case ServerboundWeaponControlsPacket.RELOAD_PRESS ->
            {
                if (canReloadWeapon(heldItem, player, weaponItem))
                {
                    // Instantly reload weapons in creative, start the reload process otherwise
                    if (player.isCreative())
                    {
                        weaponItem.setAmmoLoaded(heldItem, weaponItem.getAmmoCapacity(heldItem));
                    }
                    else
                    {
                        getReloadTimer().startTimer(weaponItem.getReloadSpeed(heldItem), false);
                        sendPacketToClient(player, weaponItem, ClientboundWeaponControlsPacket.RELOAD_START);
                    }
                }
            }
        }
    }

    @Override
    public void shootWeapon(ItemStack heldItem, Player player, WeaponItem weaponItem, boolean sendClientUpdate)
    {
        super.shootWeapon(heldItem, player, weaponItem, sendClientUpdate);

        if (!isInfiniteAmmo(heldItem, player))
        {
            int ammo = weaponItem.getAmmoLoaded(heldItem);
            weaponItem.setAmmoLoaded(heldItem, ammo - 1);
        }

        if (sendClientUpdate)
        {
            sendPacketToClient(player, weaponItem, ClientboundWeaponControlsPacket.WEAPON_SHOOT);
        }
    }

    @Override
    public void tickInput(Player player, ItemStack heldItem, @Nullable WeaponItem weaponItem)
    {
        super.tickInput(player, heldItem, weaponItem);

        // Do reload logic
        if (weaponItem != null && getReloadTimer().getTimerState() == TickTimer.State.STOPPED && reloadFlag)
        {
            if (finalizeReload(heldItem, player, weaponItem)) weaponItem.setAmmoLoaded(heldItem, weaponItem.getAmmoCapacity(heldItem));
            reloadFlag = false;
        }
    }

    @Override
    public void startHoldingTrigger(ItemStack heldItem, Player player, WeaponItem weaponItem)
    {
        super.startHoldingTrigger(heldItem, player, weaponItem);
        sendPacketToClient(player, weaponItem, ClientboundWeaponControlsPacket.START_TRIGGER_HOLD);
    }

    @Override
    public void stopHoldingTrigger(ItemStack heldItem, Player player, WeaponItem weaponItem, boolean releasedByPlayer, boolean serverAction)
    {
        super.stopHoldingTrigger(heldItem, player, weaponItem, releasedByPlayer, serverAction);
        sendPacketToClient(player, weaponItem, ClientboundWeaponControlsPacket.STOP_TRIGGER_HOLD);
    }
}