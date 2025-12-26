package liedge.ltxindustries.item.weapon;

import liedge.ltxindustries.entity.GlowstickProjectileEntity;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrade;
import liedge.ltxindustries.lib.weapons.LTXIExtendedInput;
import liedge.ltxindustries.registry.bootstrap.LTXIEquipmentUpgrades;
import liedge.ltxindustries.registry.game.LTXIGameEvents;
import liedge.ltxindustries.registry.game.LTXIItems;
import liedge.ltxindustries.registry.game.LTXISounds;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class GlowstickLauncherItem extends SemiAutoWeaponItem
{
    public GlowstickLauncherItem(Properties properties)
    {
        super(properties, 10, MAX_PROJECTILE_SPEED, 10, LTXIItems.LIGHTWEIGHT_WEAPON_ENERGY, 1, 0);
    }

    @Override
    public @Nullable ResourceKey<EquipmentUpgrade> getDefaultUpgradeKey()
    {
        return LTXIEquipmentUpgrades.GLOWSTICK_LAUNCHER_DEFAULT;
    }

    @Override
    public boolean isOneHanded(ItemStack stack)
    {
        return true;
    }

    @Override
    public boolean canFocusReticle(ItemStack heldItem, Player player, LTXIExtendedInput controls)
    {
        return false;
    }

    @Override
    public void weaponFired(ItemStack heldItem, Player player, Level level, LTXIExtendedInput controls)
    {
        if (!level.isClientSide())
        {
            GlowstickProjectileEntity glowstick = new GlowstickProjectileEntity(level);
            glowstick.setOwner(player);
            glowstick.aimAndSetPosFromShooter(player, getProjectileWeaponRange(heldItem), 0d);

            level.addFreshEntity(glowstick);
            level.gameEvent(player, LTXIGameEvents.WEAPON_FIRED, player.getEyePosition());
        }

        level.playSound(player, player, LTXISounds.GLOWSTICK_LAUNCHER_FIRE.get(), SoundSource.PLAYERS, 2f, 1f + (level.random.nextFloat() * 0.15f));
    }

    @Override
    public int getFireRate(ItemStack stack)
    {
        return 10;
    }
}