package liedge.ltxindustries.lib.weapons;

import liedge.limacore.lib.TickTimer;
import liedge.limacore.util.LimaEntityUtil;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.network.packet.ClientboundFocusTargetPacket;
import liedge.ltxindustries.network.packet.ClientboundWeaponControlsPacket;
import liedge.ltxindustries.network.packet.ServerboundWeaponControlsPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class ServerWeaponControls extends AbstractWeaponControls
{
    private boolean reloadFlag;

    public ServerWeaponControls()
    {
        getReloadTimer().withStopCallback(success -> reloadFlag = success);
    }

    private void sendPacketToClient(Player player, CustomPacketPayload packet)
    {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, packet);
    }

    private void sendPacketToClient(Player player, WeaponItem weaponItem, byte action)
    {
        sendPacketToClient(player, new ClientboundWeaponControlsPacket(player.getId(), weaponItem, action));
    }

    public void handleClientAction(ItemStack heldItem, ServerPlayer player, WeaponItem weaponItem, byte clientAction)
    {
        switch (clientAction)
        {
            case ServerboundWeaponControlsPacket.TRIGGER_PRESS -> pressTrigger(heldItem, player, weaponItem);
            case ServerboundWeaponControlsPacket.TRIGGER_RELEASE -> stopHoldingTrigger(heldItem, player, weaponItem, true);
            case ServerboundWeaponControlsPacket.RELOAD_PRESS ->
            {
                if (canReloadWeapon(heldItem, player, weaponItem))
                {
                    // Instantly reload weapons if infinite ammo is available, start the reload process otherwise
                    if (isInfiniteAmmo(heldItem, player, weaponItem))
                    {
                        weaponItem.setAmmoLoadedMax(heldItem);
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

    public void setFocusedTargetAndNotify(Player player, @Nullable LivingEntity focusedTarget)
    {
        setFocusedTarget(focusedTarget);
        sendPacketToClient(player, new ClientboundFocusTargetPacket(player.getId(), LimaEntityUtil.getEntityId(focusedTarget)));
    }

    @Override
    public void shootWeapon(ItemStack heldItem, Player player, WeaponItem weaponItem, boolean sendClientUpdate)
    {
        super.shootWeapon(heldItem, player, weaponItem, sendClientUpdate);

        if (!isInfiniteAmmo(heldItem, player, weaponItem))
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
            if (weaponItem.getReloadSource(heldItem).performReload(heldItem, player, weaponItem))
            {
                weaponItem.setAmmoLoadedMax(heldItem);
            }

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
    public void stopHoldingTrigger(ItemStack heldItem, Player player, WeaponItem weaponItem, boolean releasedByPlayer)
    {
        super.stopHoldingTrigger(heldItem, player, weaponItem, releasedByPlayer);
        sendPacketToClient(player, weaponItem, ClientboundWeaponControlsPacket.STOP_TRIGGER_HOLD);
    }
}