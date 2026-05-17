package liedge.ltxindustries.lib.weapons;

import liedge.limacore.lib.TickTimer;
import liedge.limacore.util.LimaCoreObjects;
import liedge.ltxindustries.client.item.WeaponClientItem;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.network.packet.ReloadPacket;
import liedge.ltxindustries.network.packet.ServerboundTriggerPacket;
import liedge.ltxindustries.network.packet.ServerboundWeaponSlotPacket;
import liedge.ltxindustries.registry.game.LTXIAttachmentTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jspecify.annotations.Nullable;

public final class ClientExtendedInput extends LTXIExtendedInput
{
    public static ClientExtendedInput of(Player player)
    {
        LTXIExtendedInput input = player.getData(LTXIAttachmentTypes.INPUT_EXTENSIONS);
        return LimaCoreObjects.cast(ClientExtendedInput.class, input, "Accessed client extended input on server");
    }

    private final TickTimer animationTimerA = new TickTimer();
    private final TickTimer animationTimerB = new TickTimer();
    private final TickTimer modeSwitchTimer = new TickTimer();
    public float spinAnimation0;
    public float spinAnimation;

    private boolean previousLeftInput;

    public ClientExtendedInput() {}

    public TickTimer getAnimationTimerA()
    {
        return animationTimerA;
    }

    public TickTimer getAnimationTimerB()
    {
        return animationTimerB;
    }

    public TickTimer getModeSwitchTimer()
    {
        return modeSwitchTimer;
    }

    @Override
    public boolean setSelectedSlot(Player player, int selectedSlot)
    {
        boolean result = super.setSelectedSlot(player, selectedSlot);

        if (result)
        {
            ClientPacketDistributor.sendToServer(new ServerboundWeaponSlotPacket(selectedSlot));
        }

        return result;
    }

    @Override
    protected void selectedSlotChanged(Player player)
    {
        super.selectedSlotChanged(player);

        animationTimerA.stopTimer();
        animationTimerB.stopTimer();
        modeSwitchTimer.stopTimer();
    }

    @Override
    public void startReload(Player player, ItemStack stack, WeaponItem weaponItem)
    {
        if (canReloadWeapon(stack, player, weaponItem))
        {
            setSelectedSlot(player, player.getInventory().getSelectedSlot());
            ClientPacketDistributor.sendToServer(new ReloadPacket(getSelectedSlot()));
        }
    }

    @Override
    protected void tickTimers()
    {
        super.tickTimers();
        animationTimerA.tickTimer();
        animationTimerB.tickTimer();
        modeSwitchTimer.tickTimer();
    }

    @Override
    public void shootWeapon(ItemStack heldItem, Player player, WeaponItem weaponItem)
    {
        super.shootWeapon(heldItem, player, weaponItem);

        WeaponClientItem clientItem = WeaponClientItem.of(weaponItem);
        if (clientItem != null) clientItem.onWeaponFired(heldItem, player, weaponItem, this);
    }

    @Override
    protected void triggerLogicTick(Player player, ItemStack selectedItem, int selectedSlot, @Nullable WeaponItem weaponItem)
    {
        super.triggerLogicTick(player, selectedItem, selectedSlot, weaponItem);

        // Check for slot changes
        setSelectedSlot(player, player.getInventory().getSelectedSlot());

        boolean leftInput = Minecraft.getInstance().options.keyAttack.isDown() && !player.isSpectator();
        if (this.previousLeftInput != leftInput)
        {
            if (leftInput)
            {
                if (weaponItem != null)
                {
                    pressTrigger(player, selectedItem, weaponItem);
                    ClientPacketDistributor.sendToServer(new ServerboundTriggerPacket(selectedSlot, false));
                }
            }
            else
            {
                stopTriggerHold(player);
                ClientPacketDistributor.sendToServer(new ServerboundTriggerPacket(selectedSlot, true));
            }

            this.previousLeftInput = leftInput;
        }

        if (weaponItem != null)
        {
            WeaponClientItem clientItem = WeaponClientItem.of(weaponItem);
            if (clientItem != null) clientItem.onMainHandTick(selectedItem, weaponItem, this);
        }
    }
}