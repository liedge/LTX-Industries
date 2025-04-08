package liedge.limatech.registry.bootstrap;

import liedge.limatech.LimaTech;
import liedge.limatech.lib.LimaTechDeathMessageTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DeathMessageType;

import static liedge.limacore.data.generation.LimaBootstrapUtil.registerDamageType;

public final class LimaTechDamageTypes
{
    private LimaTechDamageTypes() {}

    // Weapons
    public static final ResourceKey<DamageType> LIGHTFRAG = key("lightfrag");
    public static final ResourceKey<DamageType> EXPLOSIVE_GRENADE = key("explosive_grenade");
    public static final ResourceKey<DamageType> FLAME_GRENADE = key("flame_grenade");
    public static final ResourceKey<DamageType> CRYO_GRENADE = key("cryo_grenade");
    public static final ResourceKey<DamageType> ELECTRIC_GRENADE = key("electric_grenade");
    public static final ResourceKey<DamageType> ACID_GRENADE = key("acid_grenade");
    public static final ResourceKey<DamageType> NEURO_GRENADE = key("neuro_grenade");
    public static final ResourceKey<DamageType> ROCKET_LAUNCHER = key("rocket_launcher");

    public static final ResourceKey<DamageType> STICKY_FLAME = key("sticky_flame");
    public static final ResourceKey<DamageType> TURRET_ROCKET = key("turret_rocket");
    public static final ResourceKey<DamageType> RAILGUN_TURRET = key("railgun_turret");

    private static ResourceKey<DamageType> key(String name)
    {
        return LimaTech.RESOURCES.resourceKey(Registries.DAMAGE_TYPE, name);
    }

    public static void bootstrap(BootstrapContext<DamageType> context)
    {
        DeathMessageType weaponMsgType = LimaTechDeathMessageTypes.WEAPON_DEATH_MESSAGE_TYPE.getValue();
        DeathMessageType noItemCausedOnlyMsg = LimaTechDeathMessageTypes.NO_ITEM_CAUSING_ENTITY_ONLY.getValue();

        registerDamageType(context, LIGHTFRAG, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, weaponMsgType);
        registerDamageType(context, EXPLOSIVE_GRENADE, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, weaponMsgType);
        registerDamageType(context, FLAME_GRENADE, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.BURNING, weaponMsgType);
        registerDamageType(context, CRYO_GRENADE, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.FREEZING, weaponMsgType);
        registerDamageType(context, ELECTRIC_GRENADE, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, weaponMsgType);
        registerDamageType(context, ACID_GRENADE, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, weaponMsgType);
        registerDamageType(context, NEURO_GRENADE, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, weaponMsgType);
        registerDamageType(context, ROCKET_LAUNCHER, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, weaponMsgType);

        registerDamageType(context, STICKY_FLAME, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, noItemCausedOnlyMsg);
        registerDamageType(context, TURRET_ROCKET, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, noItemCausedOnlyMsg);
        registerDamageType(context, RAILGUN_TURRET, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, noItemCausedOnlyMsg);
    }
}