package liedge.limatech.item.weapon;

import liedge.limacore.lib.TickTimer;
import liedge.limatech.entity.CompoundHitResult;
import liedge.limatech.lib.upgrades.equipment.EquipmentUpgrades;
import liedge.limatech.lib.weapons.AbstractWeaponControls;
import liedge.limatech.registry.game.LimaTechGameEvents;
import liedge.limatech.registry.game.LimaTechItems;
import liedge.limatech.registry.game.LimaTechSounds;
import liedge.limatech.util.config.LimaTechWeaponsConfig;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class LinearFusionWeaponItem extends FullAutoWeaponItem
{
    public LinearFusionWeaponItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public boolean canFocusReticle(ItemStack heldItem, Player player, AbstractWeaponControls controls)
    {
        return controls.getReloadTimer().getTimerState() == TickTimer.State.STOPPED;
    }

    @Override
    public void triggerHoldingTick(ItemStack heldItem, Player player, AbstractWeaponControls input)
    {
        int triggerTicks = input.getTicksHoldingTrigger();
        if (triggerTicks == 1)
        {
            Level level = player.level();
            level.playSound(player, player, LimaTechSounds.LINEAR_FUSION_CHARGE.get(), SoundSource.PLAYERS, 1f, 0.95f + (0.1f * level.random.nextFloat()));
        }

        if (!player.level().isClientSide() && input.canStartShootingWeapon(heldItem, player, this) && triggerTicks >= 10)
        {
            input.shootWeapon(heldItem, player, this, true);
        }
    }

    @Override
    public boolean canContinueHoldingTrigger(ItemStack heldItem, Player player, AbstractWeaponControls input)
    {
        if (player.level().isClientSide())
        {
            return true;
        }
        else
        {
            return input.canStartShootingWeapon(heldItem, player, this);
        }
    }

    @Override
    public void weaponFired(ItemStack heldItem, Player player, Level level, AbstractWeaponControls controls)
    {
        if (!level.isClientSide())
        {
            CompoundHitResult hitResult = CompoundHitResult.tracePath(level, player, 200d, 0d, 0.175d, 2);
            EquipmentUpgrades upgrades = getUpgrades(heldItem);

            hitResult.entityHits().forEach(hit -> causeInstantDamage(upgrades, player, hit.getEntity(), LimaTechWeaponsConfig.LFR_BASE_DAMAGE.getAsDouble()));
            level.gameEvent(player, LimaTechGameEvents.WEAPON_FIRED, player.getEyePosition());

            sendTracerParticle(level, hitResult.origin(), hitResult.impactLocation());
        }

        level.playSound(player, player, LimaTechSounds.LINEAR_FUSION_FIRE.get(), SoundSource.PLAYERS, 2f, 0.9f + (level.random.nextFloat() * 0.125f));
    }

    @Override
    public Item getAmmoItem(ItemStack stack)
    {
        return LimaTechItems.SPECIALIST_AMMO_CANISTER.get();
    }

    @Override
    public int getAmmoCapacity(ItemStack stack)
    {
        return 4;
    }

    @Override
    public int getFireRate(ItemStack stack)
    {
        return 5;
    }

    @Override
    public int getReloadSpeed(ItemStack stack)
    {
        return 30;
    }

    @Override
    public int getBaseEnergyCapacity(ItemStack stack)
    {
        return LimaTechWeaponsConfig.LFR_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyUsage(ItemStack stack)
    {
        return LimaTechWeaponsConfig.LFR_ENERGY_AMMO_COST.getAsInt();
    }
}