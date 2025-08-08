package liedge.ltxindustries.lib;

import io.netty.buffer.ByteBuf;
import liedge.ltxindustries.entity.BubbleShieldUser;
import liedge.ltxindustries.registry.game.LTXIAttachmentTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.FloatTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.INBTSerializable;

public final class StandaloneBubbleShield implements INBTSerializable<FloatTag>, BubbleShieldUser
{
    public static final StreamCodec<ByteBuf, StandaloneBubbleShield> STREAM_CODEC = ByteBufCodecs.FLOAT.map(StandaloneBubbleShield::new, StandaloneBubbleShield::getShieldHealth);

    private float shieldHealth;
    private int shieldInvulnerableCooldown;

    private StandaloneBubbleShield(float shieldHealth)
    {
        this.shieldHealth = shieldHealth;
    }

    public StandaloneBubbleShield() {}

    @Override
    public float getShieldHealth()
    {
        return shieldHealth;
    }

    @Override
    public void setShieldHealth(LivingEntity entity, float shieldHealth)
    {
        this.shieldHealth = Mth.clamp(shieldHealth, 0f, MAX_SHIELD_HEALTH);
        entity.syncData(LTXIAttachmentTypes.BUBBLE_SHIELD);
    }

    @Override
    public void addShieldHealth(LivingEntity entity, float amount, float maxShield)
    {
        if (shieldHealth < maxShield)
        {
            float newShield = Math.min(shieldHealth + Math.max(amount, 0), maxShield);
            setShieldHealth(entity, newShield);
        }
    }

    @Override
    public void reduceShieldHealth(LivingEntity entity, float amount, float minShield)
    {
        if (shieldHealth > minShield)
        {
            float newShield = Math.max(shieldHealth - Math.max(amount, 0), minShield);
            setShieldHealth(entity, newShield);
        }
    }

    public void tickShield(LivingEntity shieldedEntity)
    {
        if (shieldInvulnerableCooldown > 0) shieldInvulnerableCooldown--;
    }

    @Override
    public boolean blockDamage(LivingEntity entity, Level level, DamageSource source, float amount)
    {
        // Serverside only, and doesn't block void damage
        if (!level.isClientSide() && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && shieldHealth > 0)
        {
            // Should bypass cooldown be allowed?
            if (shieldInvulnerableCooldown == 0 || source.is(DamageTypeTags.BYPASSES_COOLDOWN))
            {
                reduceShieldHealth(entity, amount, 0);
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