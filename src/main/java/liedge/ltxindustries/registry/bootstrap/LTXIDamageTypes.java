package liedge.ltxindustries.registry.bootstrap;

import liedge.ltxindustries.LTXIIdentifiers;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.entity.damage.LTXIDeathMessageTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DeathMessageType;

import static liedge.limacore.data.generation.LimaBootstrapUtil.registerDamageType;

public final class LTXIDamageTypes
{
    private LTXIDamageTypes() {}

    public static final ResourceKey<DamageType> LIGHTFRAG = key("lightfrag");
    public static final ResourceKey<DamageType> EXPLOSIVE = key("explosive");
    public static final ResourceKey<DamageType> FLAME = key("flame");
    public static final ResourceKey<DamageType> CRYO = key("cryo");
    public static final ResourceKey<DamageType> ELECTRIC = key("electric");
    public static final ResourceKey<DamageType> ACID = key("acid");
    public static final ResourceKey<DamageType> GLOOM_GAS = key("gloom_gas");

    public static final ResourceKey<DamageType> EXPLOSIVE_TURRET = key("explosive_turret");
    public static final ResourceKey<DamageType> ELECTRIC_TURRET = key("electric_turret");
    public static final ResourceKey<DamageType> RAILGUN_TURRET = key(LTXIIdentifiers.ID_RAILGUN_TURRET);

    private static ResourceKey<DamageType> key(String name)
    {
        return LTXIndustries.RESOURCES.resourceKey(Registries.DAMAGE_TYPE, name);
    }

    public static void bootstrap(BootstrapContext<DamageType> context)
    {
        DeathMessageType noItemMsg = LTXIDeathMessageTypes.NO_ITEM.getValue();
        DeathMessageType noItemOrDirectMsg = LTXIDeathMessageTypes.NO_ITEM_OR_DIRECT_ENTITY.getValue();

        registerDamageType(context, LIGHTFRAG, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, noItemMsg);
        registerDamageType(context, EXPLOSIVE, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, noItemMsg);
        registerDamageType(context, FLAME, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.BURNING, noItemMsg);
        registerDamageType(context, CRYO, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.FREEZING, noItemMsg);
        registerDamageType(context, ELECTRIC, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, noItemMsg);
        registerDamageType(context, ACID, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, noItemMsg);
        registerDamageType(context, GLOOM_GAS, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, noItemMsg);

        registerDamageType(context, EXPLOSIVE_TURRET, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, noItemOrDirectMsg);
        registerDamageType(context, ELECTRIC_TURRET, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, noItemOrDirectMsg);
        registerDamageType(context, RAILGUN_TURRET, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, noItemOrDirectMsg);
    }
}