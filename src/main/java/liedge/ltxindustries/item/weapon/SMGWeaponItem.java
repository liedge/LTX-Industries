package liedge.ltxindustries.item.weapon;

import liedge.limacore.util.LimaEntityUtil;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrade;
import liedge.ltxindustries.lib.weapons.AbstractWeaponControls;
import liedge.ltxindustries.registry.bootstrap.LTXIEquipmentUpgrades;
import liedge.ltxindustries.registry.game.LTXIItems;
import liedge.ltxindustries.util.config.LTXIWeaponsConfig;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class SMGWeaponItem extends FullAutoWeaponItem
{
    public SMGWeaponItem(Properties properties)
    {
        super(properties, 45, 12.5d, 20, LTXIItems.LIGHTWEIGHT_WEAPON_ENERGY, 1, 0);
    }

    @Override
    public @Nullable ResourceKey<EquipmentUpgrade> getDefaultUpgradeKey()
    {
        return LTXIEquipmentUpgrades.SUBMACHINE_GUN_DEFAULT;
    }

    @Override
    public boolean isOneHanded(ItemStack stack)
    {
        return true;
    }

    @Override
    public void weaponFired(ItemStack heldItem, Player player, Level level, AbstractWeaponControls controls)
    {
        double inaccuracy = LimaEntityUtil.isEntityUsingItem(player, InteractionHand.MAIN_HAND) ? 0.25d : 4d;
        traceLightfrag(heldItem, player, level, LTXIWeaponsConfig.SMG_BASE_DAMAGE.getAsDouble(), inaccuracy, 0.2d);
    }

    @Override
    public int getBaseEnergyCapacity(ItemStack stack)
    {
        return LTXIWeaponsConfig.SMG_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyUsage(ItemStack stack)
    {
        return LTXIWeaponsConfig.SMG_ENERGY_AMMO_COST.getAsInt();
    }

    @Override
    public int getFireRate(ItemStack stack)
    {
        return 0;
    }
}