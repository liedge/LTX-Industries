package liedge.limatech.lib.weapons;

import liedge.limacore.lib.Translatable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DeathMessageType;
import net.minecraft.world.entity.Entity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.common.damagesource.IDeathMessageProvider;

import java.util.Objects;

import static liedge.limatech.LimaTech.RESOURCES;

public final class WeaponDeathMessageType
{
    public static final Translatable INVALID_SOURCE_MESSAGE = RESOURCES.translationHolder("death.attack.{}.unknown_weapon");
    public static final Translatable UNKNOWN_OWNER_PROJECTILE_MESSAGE = RESOURCES.translationHolder("death.attack.{}.unknown_weapon_projectile");

    public static final EnumProxy<DeathMessageType> WEAPON_DEATH_MESSAGE_TYPE = new EnumProxy<>(DeathMessageType.class, "limatech:weapon_damage", (IDeathMessageProvider) (killedEntity, lastEntry, sigFall) -> {
        DamageSource source = lastEntry.source();
        if (source instanceof WeaponDamageSource weaponDamageSource)
        {
            Entity causingEntity = source.getEntity();
            String translationKey = "death.attack." + source.getMsgId();

            if (causingEntity != null)
            {
                return Component.translatable(translationKey, killedEntity.getDisplayName(), causingEntity.getDisplayName(), weaponDamageSource.getKillerWeapon().getDescription());
            }
            else
            {
                return UNKNOWN_OWNER_PROJECTILE_MESSAGE.translateArgs(killedEntity.getDisplayName(), Objects.requireNonNull(source.getDirectEntity()).getDisplayName());
            }
        }
        else
        {
            return INVALID_SOURCE_MESSAGE.translateArgs(killedEntity.getDisplayName());
        }
    });

    public static DeathMessageType getDeathMessageType()
    {
        return WEAPON_DEATH_MESSAGE_TYPE.getValue();
    }
}