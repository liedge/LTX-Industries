package liedge.limatech.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;

public interface BubbleShieldUser
{
    float getShieldHealth();

    void setShieldHealth(float shieldHealth);

    boolean blockDamage(Level level, DamageSource source, float amount);
}