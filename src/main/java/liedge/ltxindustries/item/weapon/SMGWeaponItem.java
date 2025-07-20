package liedge.ltxindustries.item.weapon;

import liedge.limacore.util.LimaEntityUtil;
import liedge.ltxindustries.entity.CompoundHitResult;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrades;
import liedge.ltxindustries.lib.weapons.AbstractWeaponControls;
import liedge.ltxindustries.registry.game.LTXIGameEvents;
import liedge.ltxindustries.registry.game.LTXIItems;
import liedge.ltxindustries.util.config.LTXIWeaponsConfig;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SMGWeaponItem extends FullAutoWeaponItem
{
    public SMGWeaponItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public boolean isOneHanded(ItemStack stack)
    {
        return true;
    }

    @Override
    public void weaponFired(ItemStack heldItem, Player player, Level level, AbstractWeaponControls controls)
    {
        if (!level.isClientSide())
        {
            double inaccuracy = LimaEntityUtil.isEntityUsingItem(player, InteractionHand.MAIN_HAND) ? 0.25d : 4d;
            CompoundHitResult hitResult = CompoundHitResult.tracePath(level, player, 12, inaccuracy, 0.2d, 1);
            EquipmentUpgrades upgrades = getUpgrades(heldItem);

            hitResult.entityHits().forEach(hit -> causeInstantDamage(upgrades, player, hit.getEntity(), LTXIWeaponsConfig.SMG_BASE_DAMAGE.getAsDouble()));
            level.gameEvent(player, LTXIGameEvents.WEAPON_FIRED, player.getEyePosition());

            if (level.random.nextFloat() <= 0.6f) sendTracerParticle(level, hitResult.origin(), hitResult.impactLocation());
        }
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
    public Item getAmmoItem(ItemStack stack)
    {
        return LTXIItems.LIGHTWEIGHT_WEAPON_ENERGY.asItem();
    }

    @Override
    public int getAmmoCapacity(ItemStack stack)
    {
        return 45;
    }

    @Override
    public int getFireRate(ItemStack stack)
    {
        return 0;
    }

    @Override
    public int getReloadSpeed(ItemStack stack)
    {
        return 20;
    }
}