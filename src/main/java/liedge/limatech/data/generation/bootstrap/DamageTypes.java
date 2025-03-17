package liedge.limatech.data.generation.bootstrap;

import liedge.limacore.data.generation.RegistryBootstrapExtensions;
import liedge.limatech.lib.LimaTechDeathMessageTypes;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DeathMessageType;

import static liedge.limatech.registry.LimaTechDamageTypes.*;

class DamageTypes implements RegistryBootstrapExtensions<DamageType>
{
    @Override
    public void run(BootstrapContext<DamageType> context)
    {
        DeathMessageType weaponMsgType = LimaTechDeathMessageTypes.WEAPON_DEATH_MESSAGE_TYPE.getValue();
        DeathMessageType noItemCausedOnlyMsg = LimaTechDeathMessageTypes.NO_ITEM_CAUSING_ENTITY_ONLY.getValue();

        registerDamageType(context, LIGHTFRAG, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, weaponMsgType);
        registerDamageType(context, MAGNUM_LIGHTFRAG, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, weaponMsgType);
        registerDamageType(context, EXPLOSIVE_GRENADE, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, weaponMsgType);
        registerDamageType(context, FLAME_GRENADE, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.BURNING, weaponMsgType);
        registerDamageType(context, CRYO_GRENADE, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.FREEZING, weaponMsgType);
        registerDamageType(context, ELECTRIC_GRENADE, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, weaponMsgType);
        registerDamageType(context, ACID_GRENADE, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, weaponMsgType);
        registerDamageType(context, NEURO_GRENADE, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, weaponMsgType);
        registerDamageType(context, ROCKET_LAUNCHER, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, weaponMsgType);

        registerDamageType(context, STICKY_FLAME, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, noItemCausedOnlyMsg);
        registerDamageType(context, TURRET_ROCKET, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f, DamageEffects.HURT, noItemCausedOnlyMsg);
    }
}