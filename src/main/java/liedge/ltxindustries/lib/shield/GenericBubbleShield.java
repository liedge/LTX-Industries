package liedge.ltxindustries.lib.shield;

import liedge.ltxindustries.util.config.LTXIServerConfig;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public final class GenericBubbleShield implements EntityBubbleShield
{
    public static final GenericBubbleShield INSTANCE = new GenericBubbleShield();

    private GenericBubbleShield() {}

    @Override
    public float blockDamage(LivingEntity entity, Level level, DamageSource source, float original)
    {
        if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY))
        {
            float shield = getShieldHealth(entity);
            reduceShieldHealth(entity, original);

            return Math.max(original - shield, 0);
        }

        return original;
    }

    @Override
    public boolean blockMobEffect(LivingEntity entity, Level level, MobEffectInstance effectInstance)
    {
        float shield = getShieldHealth(entity);
        if (shield > 0)
        {
            reduceShieldHealth(entity, (float) LTXIServerConfig.SHIELD_EFFECT_BLOCK_COST.getAsDouble());
            return true;
        }

        return false;
    }
}