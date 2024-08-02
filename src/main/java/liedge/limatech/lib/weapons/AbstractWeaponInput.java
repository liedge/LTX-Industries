package liedge.limatech.lib.weapons;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import liedge.limacore.lib.TickTimer;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.registry.LimaTechDataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractWeaponInput
{
    private final Object2ObjectMap<WeaponItem, TickTimer> triggerTimers = new Object2ObjectOpenHashMap<>();
    private final TickTimer reloadTimer = new TickTimer();

    private int previousSelectedSlot;
    private boolean triggerHeld;
    private int ticksHoldingTrigger;

    // #region Weapon item functions
    protected void setWeaponAmmo(ItemStack heldItem, int ammo)
    {
        heldItem.set(LimaTechDataComponents.WEAPON_AMMO, ammo);
    }

    protected int getWeaponAmmo(ItemStack heldItem)
    {
        return heldItem.getOrDefault(LimaTechDataComponents.WEAPON_AMMO, 0);
    }

    protected boolean hasAmmo(ItemStack heldItem, Player player)
    {
        return getWeaponAmmo(heldItem) > 0 || player.isCreative();
    }
    //#endregion

    private boolean checkNotReloading()
    {
        return reloadTimer.getTimerState() == TickTimer.State.STOPPED;
    }

    protected boolean canReloadWeapon(ItemStack heldItem, Player player, WeaponItem weaponItem)
    {
        if (getWeaponAmmo(heldItem) < weaponItem.getAmmoCapacity(heldItem) && !isTriggerHeld() && checkNotReloading())
        {
            if (player.isCreative()) return true;

            for (int i = 0; i < player.getInventory().getContainerSize(); i++)
            {
                ItemStack stack = player.getInventory().getItem(i);
                if (stack.is(weaponItem.getAmmoItem()))
                {
                    return true;
                }
            }
        }

        return false;
    }

    protected void tickTimers()
    {
        ObjectIterator<Object2ObjectMap.Entry<WeaponItem, TickTimer>> iterator = triggerTimers.object2ObjectEntrySet().iterator();
        while (iterator.hasNext())
        {
            Object2ObjectMap.Entry<WeaponItem, TickTimer> e = iterator.next();
            TickTimer timer = e.getValue();
            timer.tickTimer();
            if (timer.getTimerState() == TickTimer.State.STOPPED)
            {
                iterator.remove();
            }
        }

        reloadTimer.tickTimer();
    }

    protected void triggerTick(ItemStack heldItem, Player player, WeaponItem weaponItem)
    {
        if (isTriggerHeld())
        {
            if (weaponItem.canContinueHoldingTrigger(heldItem, player, this))
            {
                weaponItem.triggerHoldingTick(heldItem, player, this);
                ticksHoldingTrigger++;
            }
            else
            {
                releaseTrigger(heldItem, weaponItem, player, false);
            }
        }
    }

    protected void onSelectedSlotChanged(Player player, ItemStack oldItem, ItemStack newItem)
    {
        reloadTimer.stopTimer();

        if (oldItem.getItem() instanceof WeaponItem oldWeapon)
        {
            releaseTrigger(oldItem, oldWeapon, player, false);
        }
    }

    protected void pressTrigger(ItemStack heldItem, Player player, WeaponItem weaponItem)
    {
        if (checkNotReloading()) weaponItem.triggerPressed(heldItem, player, this);
    }

    protected void releaseTrigger(ItemStack heldItem, WeaponItem weaponItem, Player player, boolean releasedByPlayer)
    {
        if (isTriggerHeld())
        {
            triggerHeld = false;
            ticksHoldingTrigger = 0;
            weaponItem.triggerRelease(heldItem, player, this, releasedByPlayer);
        }
    }

    // Timer functions
    public TickTimer.State getTriggerState(WeaponItem weaponItem)
    {
        if (triggerTimers.containsKey(weaponItem))
        {
            return triggerTimers.get(weaponItem).getTimerState();
        }
        else
        {
            return TickTimer.State.STOPPED;
        }
    }

    protected void startTriggerTimer(WeaponItem weaponItem, int duration)
    {
        TickTimer timer = new TickTimer();
        timer.startTimer(duration);
        triggerTimers.put(weaponItem, timer);
    }

    public float lerpTriggerTimer(WeaponItem weaponItem, float partialTick)
    {
        if (triggerTimers.containsKey(weaponItem))
        {
            return triggerTimers.get(weaponItem).lerpProgressNotPaused(partialTick);
        }
        else
        {
            return 0f;
        }
    }

    // Public access properties
    public boolean canShootWeapon(ItemStack heldItem, Player player, WeaponItem weaponItem)
    {
        return getTriggerState(weaponItem) == TickTimer.State.STOPPED && checkNotReloading() && hasAmmo(heldItem, player);
    }

    public void shootWeapon(ItemStack heldItem, Player player, WeaponItem weaponItem, boolean sendClientUpdate)
    {
        weaponItem.weaponFired(heldItem, player, player.level());
        startTriggerTimer(weaponItem, weaponItem.getFireRate(heldItem));
    }

    public TickTimer getReloadTimer()
    {
        return reloadTimer;
    }

    public boolean isTriggerHeld()
    {
        return triggerHeld;
    }

    public void setTriggerHeld(boolean triggerHeld)
    {
        this.triggerHeld = triggerHeld;
    }

    public int getTicksHoldingTrigger()
    {
        return ticksHoldingTrigger;
    }

    public void tickInput(Player player, ItemStack heldItem, @Nullable WeaponItem weaponItem)
    {
        tickTimers();

        // Check for slot change
        int selectedSlot = player.getInventory().selected;
        if (previousSelectedSlot != selectedSlot)
        {
            onSelectedSlotChanged(player, player.getInventory().getItem(previousSelectedSlot), heldItem);
            previousSelectedSlot = selectedSlot;
        }

        // Do trigger logic
        if (weaponItem != null)
        {
            triggerTick(heldItem, player, weaponItem);
        }
    }
}