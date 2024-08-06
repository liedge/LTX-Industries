package liedge.limatech.lib.weapons;

import liedge.limacore.lib.LimaExtendedDamageSource;
import liedge.limatech.item.weapon.WeaponItem;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class WeaponDamageSource extends LimaExtendedDamageSource
{
    public static WeaponDamageSource handheldInstantDamage(ResourceKey<DamageType> typeKey, Entity shooter, WeaponItem weaponItem)
    {
        return new WeaponDamageSource(typeKey, shooter, shooter, weaponItem);
    }

    public static WeaponDamageSource projectileDamage(ResourceKey<DamageType> typeKey, Entity projectile, @Nullable Entity shooter, WeaponItem weaponItem)
    {
        return new WeaponDamageSource(typeKey, projectile, shooter, weaponItem);
    }

    private final WeaponItem weaponItem;
    private float armorReduction = 0f;

    private WeaponDamageSource(ResourceKey<DamageType> typeKey, Entity directEntity, @Nullable Entity causingEntity, WeaponItem weaponItem)
    {
        super(directEntity.level().registryAccess().holderOrThrow(typeKey), directEntity, causingEntity);
        this.weaponItem = weaponItem;
    }

    public WeaponItem weaponItem()
    {
        return weaponItem;
    }

    public void setArmorReduction(float armorReduction)
    {
        this.armorReduction = armorReduction;
    }

    @Override
    public float modifyAppliedArmor(LivingEntity armorWearer, float armor)
    {
        return armor - armorReduction;
    }
}