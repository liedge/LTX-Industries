package liedge.limatech.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;

public interface BubbleShieldUser
{
    float MAX_SHIELD_HEALTH = 1000;

    float getShieldHealth();

    void setShieldHealth(float shieldHealth);

    void modifyShieldHealth(float amount, float minShield, float maxShield);

    default void addShieldHealth(float amount, float maxShield)
    {
        modifyShieldHealth(amount, getShieldHealth(), maxShield);
    }

    default void removeShieldHealth(float amount, float minShield)
    {
        modifyShieldHealth(-amount, minShield, getShieldHealth());
    }

    boolean blockDamage(Level level, DamageSource source, float amount);
}