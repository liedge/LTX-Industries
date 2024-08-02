package liedge.limatech.lib.weapons;

import liedge.limacore.lib.TickTimer;
import liedge.limacore.util.LimaCoreUtil;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.network.packet.ClientboundWeaponPacket;
import liedge.limatech.network.packet.ServerboundWeaponPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ServerWeaponInput extends AbstractWeaponInput
{
    private boolean reloadFlag;

    public ServerWeaponInput()
    {
        getReloadTimer().setOnStoppedCallback(b -> reloadFlag = b);
    }

    private boolean consumeAmmoForReload(Player player, WeaponItem weaponItem)
    {
        if (player.isCreative())
        {
            return true;
        }
        else
        {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++)
            {
                ItemStack invItem = player.getInventory().getItem(i);
                if (invItem.is(weaponItem.getAmmoItem()))
                {
                    player.getInventory().removeItem(i, 1);
                    return true;
                }
            }
        }

        return false;
    }

    public void handleClientAction(ItemStack heldItem, ServerPlayer player, WeaponItem weaponItem, byte clientAction)
    {
        switch (clientAction)
        {
            case ServerboundWeaponPacket.TRIGGER_PRESS -> pressTrigger(heldItem, player, weaponItem);
            case ServerboundWeaponPacket.TRIGGER_RELEASE -> releaseTrigger(heldItem, weaponItem, player, true);
            case ServerboundWeaponPacket.RELOAD_PRESS ->
            {
                if (canReloadWeapon(heldItem, player, weaponItem))
                {
                    // Instantly reload weapons in creative, start the reload process otherwise
                    if (player.isCreative())
                    {
                        setWeaponAmmo(heldItem, weaponItem.getAmmoCapacity(heldItem));
                    }
                    else
                    {
                        getReloadTimer().startTimer(weaponItem.getReloadSpeed(heldItem), false);
                        player.connection.send(new ClientboundWeaponPacket(weaponItem, ClientboundWeaponPacket.RELOAD_START));
                    }
                }
            }
        }
    }

    @Override
    public void shootWeapon(ItemStack heldItem, Player player, WeaponItem weaponItem, boolean sendClientUpdate)
    {
        super.shootWeapon(heldItem, player, weaponItem, sendClientUpdate);

        if (!player.isCreative())
        {
            int ammo = getWeaponAmmo(heldItem);
            setWeaponAmmo(heldItem, ammo - 1);
        }

        if (sendClientUpdate)
        {
            LimaCoreUtil.castOrThrow(ServerPlayer.class, player).connection.send(new ClientboundWeaponPacket(weaponItem, ClientboundWeaponPacket.WEAPON_SHOOT));
        }
    }

    @Override
    public void tickInput(Player player, ItemStack heldItem, @Nullable WeaponItem weaponItem)
    {
        super.tickInput(player, heldItem, weaponItem);

        // Do reload logic
        if (weaponItem != null && getReloadTimer().getTimerState() == TickTimer.State.STOPPED && reloadFlag)
        {
            if (consumeAmmoForReload(player, weaponItem))
            {
                setWeaponAmmo(heldItem, weaponItem.getAmmoCapacity(heldItem));
            }

            reloadFlag = false;
        }
    }
}