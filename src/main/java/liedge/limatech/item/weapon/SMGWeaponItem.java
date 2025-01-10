package liedge.limatech.item.weapon;

import liedge.limacore.util.LimaEntityUtil;
import liedge.limacore.util.LimaNetworkUtil;
import liedge.limatech.entity.CompoundHitResult;
import liedge.limatech.registry.LimaTechDamageTypes;
import liedge.limatech.registry.LimaTechEquipmentUpgrades;
import liedge.limatech.registry.LimaTechItems;
import liedge.limatech.registry.LimaTechParticles;
import liedge.limatech.lib.upgradesystem.equipment.EquipmentUpgrades;
import liedge.limatech.util.config.LimaTechWeaponsConfig;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;

public class SMGWeaponItem extends FullAutoWeaponItem
{
    public SMGWeaponItem(Properties properties)
    {
        super(properties);
    }

    @Override
    protected EquipmentUpgrades getDefaultUpgrades(HolderLookup.Provider registries)
    {
        return EquipmentUpgrades.builder()
            .add(registries.holderOrThrow(LimaTechEquipmentUpgrades.SMG_BUILT_IN))
            .build();
    }

    @Override
    public void weaponFired(ItemStack heldItem, Player player, Level level)
    {
        if (!level.isClientSide())
        {
            double inaccuracy = LimaEntityUtil.isEntityUsingItem(player, InteractionHand.MAIN_HAND) ? 0.25d : 4d;
            CompoundHitResult hitResult = CompoundHitResult.tracePath(level, player, 12d, inaccuracy, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, e -> 0.2d, 1);
            EquipmentUpgrades upgrades = getUpgrades(heldItem);

            hitResult.entityHits().forEach(hit -> causeInstantDamage(upgrades, player, LimaTechDamageTypes.LIGHTFRAG, hit.getEntity(), LimaTechWeaponsConfig.SMG_BASE_DAMAGE.getAsDouble()));
            postWeaponFiredGameEvent(upgrades, level, player);
            LimaNetworkUtil.spawnAlwaysVisibleParticle(level, LimaTechParticles.LIGHTFRAG_TRACER, hitResult.origin(), hitResult.impact().getLocation());
        }
    }

    @Override
    public int getEnergyCapacity(ItemStack stack)
    {
        return LimaTechWeaponsConfig.SMG_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getEnergyReloadCost(ItemStack stack)
    {
        return LimaTechWeaponsConfig.SMG_ENERGY_AMMO_COST.getAsInt();
    }

    @Override
    public Item getAmmoItem(ItemStack stack)
    {
        return LimaTechItems.AUTO_AMMO_CANISTER.asItem();
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