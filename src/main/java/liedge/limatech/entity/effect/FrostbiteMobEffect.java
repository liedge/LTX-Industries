package liedge.limatech.entity.effect;

import liedge.limatech.LimaTechConstants;
import liedge.limatech.registry.game.LimaTechParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class FrostbiteMobEffect extends MobEffect
{
    public FrostbiteMobEffect(ResourceLocation id)
    {
        super(MobEffectCategory.HARMFUL, LimaTechConstants.CRYO_LIGHT_BLUE.argb32());
        addAttributeModifier(Attributes.ATTACK_SPEED, id.withSuffix(".attack_speed"), -0.25d, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        addAttributeModifier(Attributes.MOVEMENT_SPEED, id.withSuffix(".move_speed"), -0.25d, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        addAttributeModifier(Attributes.FLYING_SPEED, id.withSuffix(".fly_speed"), -0.25d, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        addAttributeModifier(Attributes.BLOCK_BREAK_SPEED, id.withSuffix(".dig_speed"), -0.25d, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier)
    {
        if (livingEntity.canFreeze())
        {
            int ticksFrozen = livingEntity.getTicksRequiredToFreeze() + 10;
            livingEntity.setTicksFrozen(ticksFrozen);
        }

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier)
    {
        return true;
    }

    @Override
    public ParticleOptions createParticleOptions(MobEffectInstance effect)
    {
        return LimaTechParticles.CRYO_SNOWFLAKE.get();
    }
}