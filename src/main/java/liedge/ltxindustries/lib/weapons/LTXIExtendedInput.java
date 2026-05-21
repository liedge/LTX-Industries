package liedge.ltxindustries.lib.weapons;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import liedge.limacore.lib.TickTimer;
import liedge.limacore.util.LimaCoreObjects;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.registry.game.LTXIAttachmentTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public abstract class LTXIExtendedInput
{
    public static LTXIExtendedInput of(Player player)
    {
        return player.getData(LTXIAttachmentTypes.INPUT_EXTENSIONS);
    }

    // Timers
    private final Object2ObjectMap<WeaponItem, TickTimer> triggerTimers = new Object2ObjectOpenHashMap<>();
    private final TickTimer reloadTimer = new TickTimer();

    // Trigger properties
    private int selectedSlot;
    private boolean triggerHeld;
    private int triggerTicks0;
    private int triggerTicks;

    private @Nullable LivingEntity focusedTarget;
    private int targetTicks;
    private int targetTicks0;

    LTXIExtendedInput() { }

    //#region Trigger functions

    public int getSelectedSlot()
    {
        return selectedSlot;
    }

    public boolean setSelectedSlot(Player player, int selectedSlot)
    {
        if (this.selectedSlot != selectedSlot)
        {
            setSelectedSlot(selectedSlot);
            selectedSlotChanged(player);

            return true;
        }

        return false;
    }

    protected void setSelectedSlot(int selectedSlot)
    {
        this.selectedSlot = selectedSlot;
    }

    public boolean isTriggerHeld()
    {
        return triggerHeld;
    }

    public int getTicksHoldingTrigger()
    {
        return triggerTicks;
    }

    public float lerpTicksHoldingTrigger(float partialTick)
    {
        return Mth.lerp(partialTick, triggerTicks0, triggerTicks);
    }

    protected void selectedSlotChanged(Player player)
    {
        reloadTimer.stopTimer();
        stopTriggerHold(player);
    }

    public void pressTrigger(Player player, ItemStack stack, WeaponItem weaponItem)
    {
        boolean handCheck = player.getOffhandItem().isEmpty() || weaponItem.isOneHanded(stack);
        if (checkNotReloading() && handCheck)
        {
            weaponItem.triggerPressed(stack, player, this);
        }
    }

    public void startTriggerHold(Player player)
    {
        if (!this.triggerHeld)
        {
            this.triggerHeld = true;

            ItemStack stack = player.getInventory().getItem(selectedSlot);
            if (stack.getItem() instanceof WeaponItem weaponItem)
            {
                weaponItem.onStartedHoldingTrigger(stack, player, this);
            }
        }
    }

    public void stopTriggerHold(Player player)
    {
        if (this.triggerHeld)
        {
            this.triggerHeld = false;
            this.triggerTicks = 0;
            this.triggerTicks0 = 0;

            ItemStack stack = player.getInventory().getItem(selectedSlot);
            if (stack.getItem() instanceof WeaponItem weaponItem)
            {
                weaponItem.onStoppedHoldingTrigger(stack, player, this);
            }
        }
    }

    protected void triggerLogicTick(Player player, ItemStack selectedItem, int selectedSlot, @Nullable WeaponItem weaponItem)
    {
        if (triggerHeld)
        {
            if (weaponItem != null && weaponItem.canContinueHoldingTrigger(selectedItem, player, this))
            {
                weaponItem.triggerHoldingTick(selectedItem, player, this);
                triggerTicks0 = triggerTicks;
                triggerTicks++;
            }
            else
            {
                stopTriggerHold(player);
            }
        }
    }

    //#endregion

    //#region Weapon item functions

    public abstract void startReload(Player player, ItemStack stack, WeaponItem weaponItem);

    protected boolean isInfiniteAmmo(ItemStack heldItem, Player player, WeaponItem weaponItem)
    {
        return player.isCreative() || weaponItem.getReloadSource(heldItem).getType() == WeaponReloadSource.Type.INFINITE;
    }

    protected boolean hasAmmoRemaining(ItemStack heldItem, Player player, WeaponItem weaponItem)
    {
        return isInfiniteAmmo(heldItem, player, weaponItem) || weaponItem.getAmmoLoaded(heldItem) > 0;
    }
    //#endregion

    private boolean checkNotReloading()
    {
        return reloadTimer.getTimerState() == TickTimer.State.STOPPED;
    }

    protected boolean canReloadWeapon(ItemStack heldItem, Player player, WeaponItem weaponItem)
    {
        if (weaponItem.getAmmoLoaded(heldItem) < weaponItem.getAmmoCapacity(heldItem) && !isTriggerHeld() && checkNotReloading())
        {
            return player.isCreative() || weaponItem.getReloadSource(heldItem).canReload(heldItem, player, weaponItem);
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

    // Focus target functions
    public @Nullable LivingEntity getFocusedTarget()
    {
        return focusedTarget;
    }

    public void setFocusedTarget(Player player, @Nullable LivingEntity focusedTarget)
    {
        this.focusedTarget = focusedTarget;
    }

    public int getTargetTicks()
    {
        return targetTicks;
    }

    public float lerpTargetTicks(float partialTick)
    {
        return Mth.lerp(partialTick, targetTicks0, targetTicks);
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

    public void shootWeapon(ItemStack heldItem, Player player, WeaponItem weaponItem)
    {
        weaponItem.weaponFired(heldItem, player, player.level(), this);

        int fireRate = weaponItem.getFireRate(heldItem);
        if (fireRate > 0) startTriggerTimer(weaponItem, fireRate);
    }

    public TickTimer getReloadTimer()
    {
        return reloadTimer;
    }

    public void tick(Player player)
    {
        // Tick timers
        tickTimers();

        // Tick trigger logic
        ItemStack selectedItem = player.getInventory().getItem(selectedSlot);
        triggerLogicTick(player, selectedItem, selectedSlot, LimaCoreObjects.tryCast(WeaponItem.class, selectedItem.getItem()));

        // Target focus logic
        if (focusedTarget != null)
        {
            targetTicks0 = targetTicks;
            targetTicks++;
        }
        else
        {
            targetTicks0 = 0;
            targetTicks = 0;
        }
    }
}