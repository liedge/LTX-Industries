package liedge.ltxindustries.item.weapon;

import liedge.ltxindustries.lib.weapons.AbstractWeaponControls;
import liedge.ltxindustries.registry.game.LTXIItems;
import liedge.ltxindustries.registry.game.LTXISounds;
import liedge.ltxindustries.util.config.LTXIWeaponsConfig;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class LinearFusionWeaponItem extends FullAutoWeaponItem
{
    public LinearFusionWeaponItem(Properties properties)
    {
        super(properties, 5, 200, 40, LTXIItems.SPECIALIST_WEAPON_ENERGY, 2, 0.33d);
    }

    @Override
    public void triggerHoldingTick(ItemStack heldItem, Player player, AbstractWeaponControls input)
    {
        int triggerTicks = input.getTicksHoldingTrigger();
        if (triggerTicks == 1)
        {
            Level level = player.level();
            level.playSound(player, player, LTXISounds.LINEAR_FUSION_CHARGE.get(), SoundSource.PLAYERS, 1f, 0.95f + (0.1f * level.random.nextFloat()));
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
        traceLightfrag(heldItem, player, level, LTXIWeaponsConfig.LFR_BASE_DAMAGE.getAsDouble(), 0d, 0.125d);
        level.playSound(player, player, LTXISounds.LINEAR_FUSION_FIRE.get(), SoundSource.PLAYERS, 2f, 0.9f + (level.random.nextFloat() * 0.125f));
    }

    @Override
    public int getFireRate(ItemStack stack)
    {
        return 3;
    }

    @Override
    public int getBaseEnergyCapacity(ItemStack stack)
    {
        return LTXIWeaponsConfig.LFR_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyUsage(ItemStack stack)
    {
        return LTXIWeaponsConfig.LFR_ENERGY_AMMO_COST.getAsInt();
    }
}