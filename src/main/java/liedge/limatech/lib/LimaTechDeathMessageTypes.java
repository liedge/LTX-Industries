package liedge.limatech.lib;

import liedge.limatech.client.LimaTechLang;
import liedge.limatech.lib.weapons.WeaponDamageSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DeathMessageType;
import net.minecraft.world.entity.Entity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.common.damagesource.IDeathMessageProvider;

import java.util.Objects;

public final class LimaTechDeathMessageTypes
{
    private LimaTechDeathMessageTypes() {}

    public static final EnumProxy<DeathMessageType> TRACEABLE_PROJECTILE_MESSAGE_TYPE = new EnumProxy<>(DeathMessageType.class, "limatech:traceable_entity", (IDeathMessageProvider) (killedEntity, lastEntry, sigFall) -> {
        DamageSource source = lastEntry.source();
        Entity causingEntity = source.getEntity();
        String translationKey = "death.attack." + source.getMsgId();

        if (causingEntity != null)
        {
            return Component.translatable(translationKey + ".entity", killedEntity.getDisplayName(), causingEntity.getDisplayName());
        }
        else
        {
            return Component.translatable(translationKey, killedEntity.getDisplayName());
        }
    });

    public static final EnumProxy<DeathMessageType> WEAPON_DEATH_MESSAGE_TYPE = new EnumProxy<>(DeathMessageType.class, "limatech:weapon", (IDeathMessageProvider) (killedEntity, lastEntry, sigFall) -> {
        DamageSource source = lastEntry.source();
        if (source instanceof WeaponDamageSource weaponDamageSource)
        {
            Entity causingEntity = source.getEntity();
            String translationKey = "death.attack." + source.getMsgId();

            if (causingEntity != null)
            {
                return Component.translatable(translationKey, killedEntity.getDisplayName(), causingEntity.getDisplayName(), weaponDamageSource.weaponItem().getDescription());
            }
            else
            {
                return LimaTechLang.STRAY_PROJECTILE_DEATH_MESSAGE.translateArgs(killedEntity.getDisplayName(), Objects.requireNonNull(source.getDirectEntity()).getDisplayName());
            }
        }
        else
        {
            return LimaTechLang.INVALID_WEAPON_DEATH_MESSAGE.translateArgs(killedEntity.getDisplayName());
        }
    });
}