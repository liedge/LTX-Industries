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

    // Weapons
    public static final ResourceKey<DamageType> LIGHTFRAG = key("lightfrag");
    public static final ResourceKey<DamageType> MAGNUM_LIGHTFRAG = key("magnum_lightfrag");
    public static final ResourceKey<DamageType> EXPLOSIVE_GRENADE = key("explosive_grenade");
    public static final ResourceKey<DamageType> FLAME_GRENADE = key("flame_grenade");
    public static final ResourceKey<DamageType> FREEZE_GRENADE = key("freeze_grenade");
    public static final ResourceKey<DamageType> ELECTRIC_GRENADE = key("electric_grenade");
    public static final ResourceKey<DamageType> ACID_GRENADE = key("acid_grenade");
    public static final ResourceKey<DamageType> NEURO_GRENADE = key("neuro_grenade");
    public static final ResourceKey<DamageType> ROCKET_LAUNCHER = key("rocket_launcher");

    public static final ResourceKey<DamageType> STICKY_FLAME = key("sticky_flame");
    public static final ResourceKey<DamageType> TURRET_ROCKET = key("turret_rocket");
}