package liedge.ltxindustries.entity.damage;

import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrades;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class WeaponDamageSource extends UpgradablePlayerDamageSource
{
    public static WeaponDamageSource handheldInstantDamage(ResourceKey<DamageType> typeKey, Entity shooter, WeaponItem weaponItem, EquipmentUpgrades upgrades)
    {
        return new WeaponDamageSource(typeKey, shooter, shooter, weaponItem, upgrades);
    }

    public static WeaponDamageSource projectileDamage(ResourceKey<DamageType> typeKey, Entity projectile, @Nullable Entity shooter, WeaponItem weaponItem, EquipmentUpgrades upgrades)
    {
        return new WeaponDamageSource(typeKey, projectile, shooter, weaponItem, upgrades);
    }

    private final WeaponItem weaponItem;

    private WeaponDamageSource(ResourceKey<DamageType> typeKey, Entity directEntity, @Nullable Entity causingEntity, WeaponItem weaponItem, EquipmentUpgrades upgrades)
    {
        super(directEntity.level().registryAccess().holderOrThrow(typeKey), directEntity, causingEntity, null, upgrades);
        this.weaponItem = weaponItem;
    }

    public WeaponItem weaponItem()
    {
        return weaponItem;
    }
}