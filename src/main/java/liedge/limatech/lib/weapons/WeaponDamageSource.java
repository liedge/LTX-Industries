package liedge.limatech.lib.weapons;

import liedge.limacore.lib.DamageSourceExtensions;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.registry.LimaTechItems;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class WeaponDamageSource extends DamageSource implements DamageSourceExtensions
{
    public static WeaponDamageSource directEntityDamage(Entity directEntity, ResourceKey<DamageType> damageType, WeaponItem killerWeapon)
    {
        Level level = directEntity.level();
        Holder<DamageType> typeHolder = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(damageType);
        return new WeaponDamageSource(typeHolder, directEntity, killerWeapon);
    }

    public static WeaponDamageSource projectileEntityDamage(Level level, Entity projectile, @Nullable Entity owner, ResourceKey<DamageType> damageType, WeaponItem killerWeapon)
    {
        Holder<DamageType> holder = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(damageType);
        return new WeaponDamageSource(holder, projectile, owner, null, killerWeapon);
    }

    private final WeaponItem killerWeapon;

    private WeaponDamageSource(Holder<DamageType> typeHolder, @Nullable Entity directEntity, @Nullable Entity causingEntity, @Nullable Vec3 damageLocation, WeaponItem killerWeapon)
    {
        super(typeHolder, directEntity, causingEntity, damageLocation);
        this.killerWeapon = killerWeapon;
    }

    private WeaponDamageSource(Holder<DamageType> typeHolder, Entity directEntity, WeaponItem killerWeapon)
    {
        this(typeHolder, directEntity, directEntity, null, killerWeapon);
    }

    public WeaponItem getKillerWeapon()
    {
        return killerWeapon;
    }

    @Override
    public boolean avoidsAngering()
    {
        return killerWeapon.equals(LimaTechItems.SUBMACHINE_GUN.get());
    }

    @Override
    public float getKnockbackModifier()
    {
        return killerWeapon.equals(LimaTechItems.SUBMACHINE_GUN.get()) ? 0f : 1f;
    }
}