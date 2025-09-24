package liedge.ltxindustries.lib;

import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.entity.damage.UpgradableEquipmentDamageSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DeathMessageType;
import net.minecraft.world.entity.Entity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.common.damagesource.IDeathMessageProvider;

import java.util.Objects;

public final class LTXIDeathMessageTypes
{
    private LTXIDeathMessageTypes() {}

    public static final EnumProxy<DeathMessageType> NO_ITEM_CAUSING_ENTITY_ONLY = new EnumProxy<>(DeathMessageType.class, "ltxi:no_item_causing_entity_only", (IDeathMessageProvider) (killedEntity, lastEntry, sigFall) -> {
        DamageSource source = lastEntry.source();
        Entity causingEntity = source.getEntity();
        String translationKey = "death.attack." + source.getMsgId();

        if (causingEntity != null)
        {
            return Component.translatable(translationKey, killedEntity.getDisplayName(), causingEntity.getDisplayName());
        }
        else
        {
            return Component.translatable(translationKey + ".unowned", killedEntity.getDisplayName());
        }
    });

    public static final EnumProxy<DeathMessageType> WEAPON_DEATH_MESSAGE_TYPE = new EnumProxy<>(DeathMessageType.class, "ltxi:weapon", (IDeathMessageProvider) (killedEntity, lastEntry, sigFall) -> {
        DamageSource source = lastEntry.source();
        if (source instanceof UpgradableEquipmentDamageSource upgradableSource)
        {
            Entity causingEntity = source.getEntity();
            String translationKey = "death.attack." + source.getMsgId();

            if (causingEntity != null)
            {
                return Component.translatable(translationKey, killedEntity.getDisplayName(), causingEntity.getDisplayName(), upgradableSource.getWeaponItem().getItem().getDescription());
            }
            else
            {
                return LTXILangKeys.STRAY_PROJECTILE_DEATH_MESSAGE.translateArgs(killedEntity.getDisplayName(), Objects.requireNonNull(source.getDirectEntity()).getDisplayName());
            }
        }
        else
        {
            return LTXILangKeys.INVALID_WEAPON_DEATH_MESSAGE.translateArgs(killedEntity.getDisplayName());
        }
    });
}