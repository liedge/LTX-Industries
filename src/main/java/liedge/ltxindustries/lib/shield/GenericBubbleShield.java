package liedge.ltxindustries.lib.shield;

import liedge.ltxindustries.util.config.LTXIServerConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public final class GenericBubbleShield implements EntityBubbleShield
{
    public static final GenericBubbleShield INSTANCE = new GenericBubbleShield();

    private GenericBubbleShield() {}

    @Override
    public float blockDamage(LivingEntity entity, ServerLevel level, DamageSource source, float original)
    {
        if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY))
        {
            float shield = getShieldHealth(entity);
            damageShield(level, entity, original);

            return Math.max(original - shield, 0);
        }

        return original;
    }

    @Override
    public boolean blockMobEffect(LivingEntity entity, ServerLevel level, MobEffectInstance effectInstance)
    {
        float shield = getShieldHealth(entity);
        if (shield > 0)
        {
            damageShield(level, entity, (float) LTXIServerConfig.SHIELD_EFFECT_BLOCK_COST.getAsDouble());
            return true;
        }

        return false;
    }
}