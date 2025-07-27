package liedge.ltxindustries.lib;

import liedge.ltxindustries.entity.BubbleShieldUser;
import liedge.ltxindustries.network.packet.ClientboundEntityShieldPacket;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.FloatTag;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.network.PacketDistributor;

public final class StandaloneBubbleShield implements INBTSerializable<FloatTag>, BubbleShieldUser
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
        this.shieldHealth = Mth.clamp(shieldHealth, 0f, MAX_SHIELD_HEALTH);
        changed = true;
    }

    @Override
    public void modifyShieldHealth(float amount, float minShield, float maxShield)
    {
        float newShield = Mth.clamp(shieldHealth + amount, minShield, maxShield);
        if (newShield != shieldHealth) setShieldHealth(newShield);
    }

    public void tickShield(LivingEntity shieldedEntity)
    {
        if (shieldInvulnerableCooldown > 0) shieldInvulnerableCooldown--;

        if (changed) // Level side checked in event
        {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(shieldedEntity, new ClientboundEntityShieldPacket(shieldedEntity.getId(), shieldHealth));
            changed = false;
        }
    }

    @Override
    public boolean blockDamage(Level level, DamageSource source, float amount)
    {
        // Serverside only, and doesn't block void damage
        if (!level.isClientSide() && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && shieldHealth > 0)
        {
            // Should bypass cooldown be allowed?
            if (shieldInvulnerableCooldown == 0 || source.is(DamageTypeTags.BYPASSES_COOLDOWN))
            {
                removeShieldHealth(amount, 0);
                shieldInvulnerableCooldown = 20;
            }

            // Block damage
            return true;
        }

        // Damage wasn't blocked
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