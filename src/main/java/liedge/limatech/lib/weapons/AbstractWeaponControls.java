package liedge.limatech.lib.weapons;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import liedge.limacore.lib.TickTimer;
import liedge.limacore.util.LimaCoreUtil;
import liedge.limatech.item.weapon.WeaponItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public abstract class AbstractWeaponControls
{
    private final Object2ObjectMap<WeaponItem, TickTimer> triggerTimers = new Object2ObjectOpenHashMap<>();
    private final TickTimer reloadTimer = new TickTimer();
    private final TickTimer modeSwitchCooldownTimer = new TickTimer();

    private int previousSelectedSlot;
    private boolean triggerHeld;
    private int ticksHoldingTrigger;

    private @Nullable Entity focusedTarget;

    // #region Weapon item functions
    protected boolean isInfiniteAmmo(Player player, WeaponAmmoSource ammoSource)
    {
        return player.isCreative() || ammoSource == WeaponAmmoSource.INFINITE;
    }

    protected boolean isInfiniteAmmo(ItemStack heldItem, Player player)
    {
        return isInfiniteAmmo(player, WeaponItem.getAmmoSourceFromItem(heldItem));
    }

    protected boolean hasAmmoRemaining(ItemStack heldItem, Player player, WeaponItem weaponItem)
    {
        return isInfiniteAmmo(heldItem, player) || weaponItem.getAmmoLoaded(heldItem) > 0;
    }
    //#endregion

    private boolean checkNotReloading()
    {
        return reloadTimer.getTimerState() == TickTimer.State.STOPPED;
    }

    protected Stream<ItemStack> inventoryAndOffhandStream(Player player)
    {
        Inventory inventory = player.getInventory();
        return Stream.concat(inventory.items.stream(), inventory.offhand.stream());
    }

    protected boolean canReloadWeapon(ItemStack heldItem, Player player, WeaponItem weaponItem)
    {
        if (weaponItem.getAmmoLoaded(heldItem) < weaponItem.getAmmoCapacity(heldItem) && !isTriggerHeld() && checkNotReloading())
        {
            WeaponAmmoSource ammoSource = WeaponItem.getAmmoSourceFromItem(heldItem);

            if (isInfiniteAmmo(player, ammoSource))
            {
                return true;
            }
            else if (ammoSource == WeaponAmmoSource.COMMON_ENERGY_UNIT)
            {
                return weaponItem.getEnergyStored(heldItem) >= weaponItem.getEnergyReloadCost(heldItem);
            }
            else
            {
                return inventoryAndOffhandStream(player).anyMatch(stack -> stack.is(weaponItem.getAmmoItem(heldItem)));
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
        modeSwitchCooldownTimer.tickTimer();
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
            stopHoldingTrigger(heldItem, player, weaponItem, releasedByPlayer, false);
            ticksHoldingTrigger = 0;
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
    public boolean canContinueShootingWeapon(ItemStack heldItem, Player player, WeaponItem weaponItem)
    {
        return checkNotReloading() && hasAmmoRemaining(heldItem, player, weaponItem);
    }

    public boolean canStartShootingWeapon(ItemStack heldItem, Player player, WeaponItem weaponItem)
    {
        return canContinueShootingWeapon(heldItem, player, weaponItem) && getTriggerState(weaponItem) == TickTimer.State.STOPPED;
    }

    public ServerWeaponControls asServerControls()
    {
        return LimaCoreUtil.castOrThrow(ServerWeaponControls.class, this, () -> new ClassCastException("Attempted to access server weapon controls on client."));
    }

    public void shootWeapon(ItemStack heldItem, Player player, WeaponItem weaponItem, boolean sendClientUpdate)
    {
        weaponItem.weaponFired(heldItem, player, player.level());
        startTriggerTimer(weaponItem, weaponItem.getFireRate(heldItem));
    }

    public void startHoldingTrigger(ItemStack heldItem, Player player, WeaponItem weaponItem)
    {
        if (!triggerHeld)
        {
            this.triggerHeld = true;
            weaponItem.onStartedHoldingTrigger(heldItem, player, this);
        }
    }

    public void stopHoldingTrigger(ItemStack heldItem, Player player, WeaponItem weaponItem, boolean releasedByPlayer, boolean serverAction)
    {
        if (triggerHeld)
        {
            this.triggerHeld = false;
            weaponItem.onStoppedHoldingTrigger(heldItem, player, this, releasedByPlayer, serverAction);
        }
    }

    public TickTimer getReloadTimer()
    {
        return reloadTimer;
    }

    public TickTimer getModeSwitchCooldownTimer()
    {
        return modeSwitchCooldownTimer;
    }

    public boolean isTriggerHeld()
    {
        return triggerHeld;
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