package liedge.limatech.lib;

import liedge.limatech.entity.BubbleShieldUser;
import liedge.limatech.network.packet.ClientboundBubbleShieldPacket;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.FloatTag;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.network.PacketDistributor;

public class StandaloneBubbleShield implements INBTSerializable<FloatTag>, BubbleShieldUser
{
    public static final float MAX_SHIELD_HEALTH = 1000;

    private float shieldHealth;
    private int shieldInvulnerableCooldown;
    private boolean changed = true;

    @Override
    public float getShieldHealth()
    {
        return shieldHealth;
    }

    @Override
    public void setShieldHealth(float shieldHealth)
    {
        this.shieldHealth = Math.min(shieldHealth, MAX_SHIELD_HEALTH);
        changed = true;
    }

    public void addShieldHealth(float amount, float max)
    {
        if (shieldHealth < max)
        {
            float newShield = Math.min(shieldHealth + amount, max);
            setShieldHealth(newShield);
        }
    }

    public void tickShield(LivingEntity shieldedEntity)
    {
        if (shieldInvulnerableCooldown > 0) shieldInvulnerableCooldown--;

        if (changed && !shieldedEntity.level().isClientSide())
        {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(shieldedEntity, new ClientboundBubbleShieldPacket(shieldedEntity, shieldHealth));
            changed = false;
        }
    }

    @Override
    public boolean blockDamage(Level level, DamageSource source, float amount)
    {
        if (!level.isClientSide())
        {
            if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && shieldHealth > 0)
            {
                if (shieldInvulnerableCooldown == 0 || source.is(DamageTypeTags.BYPASSES_COOLDOWN))
                {
                    float newShield = shieldHealth - Math.min(shieldHealth, amount);
                    setShieldHealth(newShield);
                    shieldInvulnerableCooldown = 20;
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public FloatTag serializeNBT(HolderLookup.Provider provider)
    {
        return FloatTag.valueOf(shieldHealth);
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, FloatTag floatTag)
    {
        shieldHealth = floatTag.getAsFloat();
    }
}