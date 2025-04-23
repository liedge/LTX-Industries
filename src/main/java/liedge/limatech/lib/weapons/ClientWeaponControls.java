package liedge.limatech.lib.weapons;

import liedge.limacore.lib.TickTimer;
import liedge.limacore.util.LimaCoreUtil;
import liedge.limatech.client.AutomaticWeaponSoundInstance;
import liedge.limatech.client.renderer.item.WeaponRenderProperties;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.network.packet.ClientboundWeaponControlsPacket;
import liedge.limatech.network.packet.ServerboundWeaponControlsPacket;
import liedge.limatech.registry.game.LimaTechAttachmentTypes;
import liedge.limatech.registry.game.LimaTechItems;
import liedge.limatech.registry.game.LimaTechSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class ClientWeaponControls extends AbstractWeaponControls
{
    public static ClientWeaponControls of(Player player)
    {
        AbstractWeaponControls controls = player.getData(LimaTechAttachmentTypes.WEAPON_CONTROLS);
        return LimaCoreUtil.castOrThrow(ClientWeaponControls.class, controls, "Attempted to access client weapon controls on server.");
    }

    private final TickTimer animationTimerA = new TickTimer();
    private final TickTimer animationTimerB = new TickTimer();
    private boolean previousLeftInput;

    public ClientWeaponControls() {}

    private void sendPacketToServer(WeaponItem weaponItem, byte action)
    {
        PacketDistributor.sendToServer(new ServerboundWeaponControlsPacket(weaponItem, action));
    }

    public TickTimer getAnimationTimerA()
    {
        return animationTimerA;
    }

    public TickTimer getAnimationTimerB()
    {
        return animationTimerB;
    }

    public void handleServerAction(ItemStack heldItem, Player player, WeaponItem weaponItem, byte serverAction)
    {
        switch (serverAction)
        {
            case ClientboundWeaponControlsPacket.RELOAD_START -> getReloadTimer().startTimer(weaponItem.getReloadSpeed(heldItem));
            case ClientboundWeaponControlsPacket.WEAPON_SHOOT -> shootWeapon(heldItem, player, weaponItem, false);
            case ClientboundWeaponControlsPacket.START_TRIGGER_HOLD -> startHoldingTrigger(heldItem, player, weaponItem);
            case ClientboundWeaponControlsPacket.STOP_TRIGGER_HOLD -> stopHoldingTrigger(heldItem, player, weaponItem, true, true);
        }
    }

    public void handleReloadInput(Player player, ItemStack heldItem, WeaponItem weaponItem)
    {
        if (canReloadWeapon(heldItem, player, weaponItem)) sendPacketToServer(weaponItem, ServerboundWeaponControlsPacket.RELOAD_PRESS);
    }

    @Override
    protected void onSelectedSlotChanged(Player player, ItemStack oldItem, ItemStack newItem)
    {
        super.onSelectedSlotChanged(player, oldItem, newItem);
        animationTimerA.stopTimer();
        animationTimerB.stopTimer();
    }

    @Override
    protected void tickTimers()
    {
        super.tickTimers();
        animationTimerA.tickTimer();
        animationTimerB.tickTimer();
    }

    @Override
    public void shootWeapon(ItemStack heldItem, Player player, WeaponItem weaponItem, boolean sendClientUpdate)
    {
        super.shootWeapon(heldItem, player, weaponItem, sendClientUpdate);
        WeaponRenderProperties.fromItem(weaponItem).onWeaponFired(heldItem, weaponItem, this);
    }

    @Override
    public void startHoldingTrigger(ItemStack heldItem, Player player, WeaponItem weaponItem)
    {
        super.startHoldingTrigger(heldItem, player, weaponItem);

        if (weaponItem == LimaTechItems.SUBMACHINE_GUN.get())
        {
            Minecraft.getInstance().getSoundManager().play(new AutomaticWeaponSoundInstance(LimaTechSounds.SUBMACHINE_GUN_LOOP, player));
        }
    }

    @Override
    protected void triggerTick(ItemStack heldItem, Player player, WeaponItem weaponItem)
    {
        final boolean leftInput = Minecraft.getInstance().options.keyAttack.isDown() && !player.isSpectator();

        if (previousLeftInput != leftInput)
        {
            if (leftInput)
            {
                pressTrigger(heldItem, player, weaponItem);
                sendPacketToServer(weaponItem, ServerboundWeaponControlsPacket.TRIGGER_PRESS);
            }
            else
            {
                releaseTrigger(heldItem, weaponItem, player, true);
                sendPacketToServer(weaponItem, ServerboundWeaponControlsPacket.TRIGGER_RELEASE);
            }

            previousLeftInput = leftInput;
        }

        super.triggerTick(heldItem, player, weaponItem);
    }
}