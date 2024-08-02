package liedge.limatech.lib.weapons;

import liedge.limacore.lib.TickTimer;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.network.packet.ClientboundWeaponPacket;
import liedge.limatech.network.packet.ServerboundWeaponPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public final class LocalWeaponInput extends AbstractWeaponInput
{
    public static final LocalWeaponInput LOCAL_WEAPON_INPUT = new LocalWeaponInput();

    private final TickTimer recoilTimerA = new TickTimer();
    private final TickTimer recoilTimerB = new TickTimer();
    private boolean previousLeftInput;

    private LocalWeaponInput() {}

    private void sendPacketToServer(byte action)
    {
        PacketDistributor.sendToServer(new ServerboundWeaponPacket(action));
    }

    public TickTimer getRecoilTimerA()
    {
        return recoilTimerA;
    }

    public TickTimer getRecoilTimerB()
    {
        return recoilTimerB;
    }

    public void handleServerAction(ItemStack heldItem, Player player, WeaponItem weaponItem, byte serverAction)
    {
        if (serverAction == ClientboundWeaponPacket.RELOAD_START)
        {
            getReloadTimer().startTimer(weaponItem.getReloadSpeed(heldItem));
        }
        else if (serverAction == ClientboundWeaponPacket.WEAPON_SHOOT)
        {
            shootWeapon(heldItem, player, weaponItem, false);
        }
    }

    public void handleReloadInput(Player player, ItemStack heldItem, WeaponItem weaponItem)
    {
        if (canReloadWeapon(heldItem, player, weaponItem)) sendPacketToServer(ServerboundWeaponPacket.RELOAD_PRESS);
    }

    @Override
    protected void onSelectedSlotChanged(Player player, ItemStack oldItem, ItemStack newItem)
    {
        super.onSelectedSlotChanged(player, oldItem, newItem);
        recoilTimerA.stopTimer();
        recoilTimerB.stopTimer();
    }

    @Override
    protected void tickTimers()
    {
        super.tickTimers();
        recoilTimerA.tickTimer();
        recoilTimerB.tickTimer();
    }

    @Override
    public void shootWeapon(ItemStack heldItem, Player player, WeaponItem weaponItem, boolean sendClientUpdate)
    {
        super.shootWeapon(heldItem, player, weaponItem, sendClientUpdate);

        if (!recoilTimerA.isRunningClient()) recoilTimerA.startTimer(weaponItem.getRecoilA(heldItem));
        if (!recoilTimerB.isRunningClient()) recoilTimerB.startTimer(weaponItem.getRecoilB(heldItem));
    }

    @Override
    protected void triggerTick(ItemStack heldItem, Player player, WeaponItem weaponItem)
    {
        final boolean leftInput = Minecraft.getInstance().options.keyAttack.isDown();

        if (previousLeftInput != leftInput)
        {
            if (leftInput)
            {
                pressTrigger(heldItem, player, weaponItem);
                sendPacketToServer(ServerboundWeaponPacket.TRIGGER_PRESS);
            }
            else
            {
                releaseTrigger(heldItem, weaponItem, player, true);
                sendPacketToServer(ServerboundWeaponPacket.TRIGGER_RELEASE);
            }

            previousLeftInput = leftInput;
        }

        super.triggerTick(heldItem, player, weaponItem);
    }
}