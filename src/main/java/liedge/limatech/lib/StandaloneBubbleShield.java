package liedge.limatech.lib;

import liedge.limatech.entity.BubbleShieldUser;
import liedge.limatech.network.packet.ClientboundEntityShieldPacket;
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
    private float shieldHealth;
    private int shieldInvulnerableCooldown;
    private boolean changed = false;

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

    @Override
    public void restoreShieldHealth(float amount, float maxShield)
    {
        if (shieldHealth < maxShield)
        {
            float newShield = Math.min(shieldHealth + amount, maxShield);
            setShieldHealth(newShield);
        }
    }

    public void tickShield(LivingEntity shieldedEntity)
    {
        if (shieldInvulnerableCooldown > 0) shieldInvulnerableCooldown--;

        if (changed && !shieldedEntity.level().isClientSide())
        {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(shieldedEntity, new ClientboundEntityShieldPacket(shieldedEntity.getId(), shieldHealth));
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