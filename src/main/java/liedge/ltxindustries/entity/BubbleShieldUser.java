package liedge.ltxindustries.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public interface BubbleShieldUser
{
    float MAX_SHIELD_HEALTH = 1000;

    float getShieldHealth();

    void setShieldHealth(LivingEntity entity, float shieldHealth);

    void addShieldHealth(LivingEntity entity, float amount, float maxShield);

    void reduceShieldHealth(LivingEntity entity, float amount, float minShield);

    boolean blockDamage(LivingEntity entity, Level level, DamageSource source, float amount);
}