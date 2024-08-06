package liedge.limatech.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;

public interface BubbleShieldUser
{
    float MAX_SHIELD_HEALTH = 1000;

    float getShieldHealth();

    void setShieldHealth(float shieldHealth);

    void restoreShieldHealth(float amount, float maxShield);

    boolean blockDamage(Level level, DamageSource source, float amount);
}