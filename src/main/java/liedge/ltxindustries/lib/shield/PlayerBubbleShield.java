package liedge.ltxindustries.lib.shield;

import liedge.ltxindustries.registry.game.LTXIAttributes;
import liedge.ltxindustries.util.config.LTXIServerConfig;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public final class PlayerBubbleShield implements EntityBubbleShield
{
    private int invulnerabilityTicks;
    private int rechargeDelay;

    public void tick(Player player)
    {
        if (player.level().isClientSide()) return;

        if (invulnerabilityTicks > 0) invulnerabilityTicks--;

        if (rechargeDelay > 0)
        {
            rechargeDelay--;
        }
        else if (getShieldHealth(player) < getShieldCapacity(player))
        {
            float recharge = Math.max(2f, getShieldCapacity(player) / 100f);
            addShieldHealth(player, recharge, 0f);
        }
    }

    @Override
    public float getShieldCapacity(LivingEntity entity)
    {
        return (float) entity.getAttributeValue(LTXIAttributes.SHIELD_CAPACITY);
    }

    @Override
    public float blockDamage(LivingEntity entity, Level level, DamageSource source, float original)
    {
        if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && hurtShield(entity, original))
        {
            return 0;
        }
        else
        {
            return original;
        }
    }

    @Override
    public boolean blockMobEffect(LivingEntity entity, Level level, MobEffectInstance effectInstance)
    {
        return hurtShield(entity, (float) LTXIServerConfig.SHIELD_EFFECT_BLOCK_COST.getAsDouble());
    }

    @Override
    public void onShieldBreak(LivingEntity entity)
    {
        EntityBubbleShield.super.onShieldBreak(entity);
        entity.invulnerableTime = 60;
    }

    private boolean hurtShield(LivingEntity entity, float damage)
    {
        float shield = getShieldHealth(entity);
        if (shield > 0)
        {
            rechargeDelay = 100;

            if (invulnerabilityTicks == 0)
            {
                reduceShieldHealth(entity, damage);
                invulnerabilityTicks = 20;
            }

            return true;
        }

        return false;
    }
}