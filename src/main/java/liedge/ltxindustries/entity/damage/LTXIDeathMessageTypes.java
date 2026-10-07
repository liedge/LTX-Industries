package liedge.ltxindustries.entity.damage;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DeathMessageType;
import net.minecraft.world.entity.Entity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.common.damagesource.IDeathMessageProvider;

public final class LTXIDeathMessageTypes
{
    private LTXIDeathMessageTypes() {}

    public static final EnumProxy<DeathMessageType> NO_ITEM = new EnumProxy<>(DeathMessageType.class, "ltxi:no_weapon_item", (IDeathMessageProvider) (victim, lastEntry, _) -> {
        DamageSource source = lastEntry.source();
        String langKey = "death.attack." + source.getMsgId();

        Entity causingEntity = source.getEntity();
        Entity directEntity = source.getDirectEntity();

        if (causingEntity == null && directEntity == null)
        {
            return Component.translatable(langKey + ".noAttacker", victim.getDisplayName());
        }
        else
        {
            Component attackerName = causingEntity != null ? causingEntity.getDisplayName() : directEntity.getDisplayName();
            return Component.translatable(langKey, victim.getDisplayName(), attackerName);
        }
    });

    public static final EnumProxy<DeathMessageType> NO_ITEM_OR_DIRECT_ENTITY = new EnumProxy<>(DeathMessageType.class, "ltxi:no_item_or_direct_entity", (IDeathMessageProvider) (victim, lastEntry, _) -> {
        DamageSource source = lastEntry.source();
        String langKey = "death.attack." + source.getMsgId();

        Entity causingEntity = source.getEntity();

        if (causingEntity != null)
        {
            return Component.translatable(langKey, victim.getDisplayName(), causingEntity.getDisplayName());
        }
        else
        {
            return Component.translatable(langKey + ".noAttacker", victim.getDisplayName());
        }
    });
}