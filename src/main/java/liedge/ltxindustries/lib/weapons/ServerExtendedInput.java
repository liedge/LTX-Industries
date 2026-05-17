package liedge.ltxindustries.lib.weapons;

import liedge.limacore.lib.TickTimer;
import liedge.limacore.util.LimaCoreObjects;
import liedge.limacore.util.LimaEntityUtil;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.network.packet.ClientboundFocusTargetPacket;
import liedge.ltxindustries.network.packet.ReloadPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jspecify.annotations.Nullable;

public final class ServerExtendedInput extends LTXIExtendedInput
{
    private boolean reloadFlag;

    public ServerExtendedInput()
    {
        getReloadTimer().withStopCallback(success -> reloadFlag = success);
    }

    @Override
    public void startReload(Player player, ItemStack stack, WeaponItem weaponItem)
    {
        if (canReloadWeapon(stack, player, weaponItem))
        {
            if (isInfiniteAmmo(stack, player, weaponItem))
            {
                weaponItem.setAmmoLoadedMax(stack);
            }
            else
            {
                getReloadTimer().startTimer(weaponItem.getReloadSpeed(stack));
                LimaCoreObjects.cast(ServerPlayer.class, player).connection.send(new ReloadPacket(getSelectedSlot()));
            }
        }
    }

    @Override
    public void setFocusedTarget(Player player, @Nullable LivingEntity focusedTarget)
    {
        super.setFocusedTarget(player, focusedTarget);
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new ClientboundFocusTargetPacket(player.getId(), LimaEntityUtil.getEntityId(focusedTarget)));
    }

    @Override
    public void shootWeapon(ItemStack heldItem, Player player, WeaponItem weaponItem)
    {
        super.shootWeapon(heldItem, player, weaponItem);

        if (!isInfiniteAmmo(heldItem, player, weaponItem))
        {
            int ammo = weaponItem.getAmmoLoaded(heldItem);
            weaponItem.setAmmoLoaded(heldItem, ammo - 1);
        }
    }

    @Override
    protected void triggerLogicTick(Player player, ItemStack selectedItem, int selectedSlot, @Nullable WeaponItem weaponItem)
    {
        super.triggerLogicTick(player, selectedItem, selectedSlot, weaponItem);

        if (getReloadTimer().getTimerState() == TickTimer.State.STOPPED && reloadFlag)
        {
            if (weaponItem != null && weaponItem.getReloadSource(selectedItem).performReload(selectedItem, player, weaponItem))
            {
                weaponItem.setAmmoLoadedMax(selectedItem);
            }

            reloadFlag = false;
        }
    }
}