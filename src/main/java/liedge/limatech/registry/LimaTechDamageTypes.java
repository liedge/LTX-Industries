package liedge.limatech.registry;

import liedge.limatech.LimaTech;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public final class LimaTechDamageTypes
{
    private LimaTechDamageTypes() {}

    private static ResourceKey<DamageType> key(String name)
    {
        return LimaTech.RESOURCES.resourceKey(Registries.DAMAGE_TYPE, name);
    }

    public static final ResourceKey<DamageType> WEAPON_DAMAGE = key("weapon_damage");
    public static final ResourceKey<DamageType> LIGHTFRAG = key("lightfrag");
    public static final ResourceKey<DamageType> ROCKET_TURRET = key("rocket_turret");
}