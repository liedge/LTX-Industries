package liedge.ltxindustries.lib.shield;

import liedge.limacore.client.particle.ColorSizeParticleOptions;
import liedge.limacore.util.LimaEntityUtil;
import liedge.limacore.util.LimaNetworkUtil;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.registry.game.LTXIAttachmentTypes;
import liedge.ltxindustries.registry.game.LTXIParticles;
import liedge.ltxindustries.registry.game.LTXISounds;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public interface EntityBubbleShield
{
    float GLOBAL_MAX_SHIELD = 1000;

    default float getShieldHealth(LivingEntity entity)
    {
        return entity.getData(LTXIAttachmentTypes.BUBBLE_SHIELD_HEALTH);
    }

    default float getShieldCapacity(LivingEntity entity)
    {
        return GLOBAL_MAX_SHIELD;
    }

    default void setShieldHealth(LivingEntity entity, float shieldHealth)
    {
        entity.setData(LTXIAttachmentTypes.BUBBLE_SHIELD_HEALTH, Mth.clamp(shieldHealth, 0f, GLOBAL_MAX_SHIELD));
    }

    default void addShieldHealth(LivingEntity entity, float amount, float maxOvercharge)
    {
        float upperLimit = Math.min(getShieldCapacity(entity) + Math.max(0, maxOvercharge), GLOBAL_MAX_SHIELD);
        float newHealth = Mth.clamp(getShieldHealth(entity) + Math.abs(amount), 0f, upperLimit);

        entity.setData(LTXIAttachmentTypes.BUBBLE_SHIELD_HEALTH, newHealth);
    }

    default void reduceShieldHealth(LivingEntity entity, float amount)
    {
        float current = getShieldHealth(entity);
        float newShield = current - Math.abs(amount);

        if (current > 0 && newShield <= 0) onShieldBreak(entity);

        setShieldHealth(entity, newShield);
    }

    default void onShieldBreak(LivingEntity entity)
    {
        Vec3 center = entity.getBoundingBox().getCenter();

        ParticleOptions particle = new ColorSizeParticleOptions(LTXIParticles.SHIELD_BREAK, LTXIConstants.BUBBLE_SHIELD_BLUE, (float) LimaEntityUtil.getLargestBBDimension(entity));
        LimaNetworkUtil.sendParticle(entity.level(), particle, LimaNetworkUtil.LONG_PARTICLE_DIST, center);

        entity.level().playSound(null, center.x, center.y, center.z, LTXISounds.BUBBLE_SHIELD_BREAK, SoundSource.NEUTRAL, 2f, 0.95f);
    }

    float blockDamage(LivingEntity entity, Level level, DamageSource source, float original);

    boolean blockMobEffect(LivingEntity entity, Level level, MobEffectInstance effectInstance);
}