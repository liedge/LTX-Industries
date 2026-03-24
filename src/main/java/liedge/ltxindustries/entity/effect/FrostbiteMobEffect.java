package liedge.ltxindustries.entity.effect;

import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.registry.game.LTXIParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class FrostbiteMobEffect extends MobEffect
{
    public FrostbiteMobEffect(Identifier id)
    {
        super(MobEffectCategory.HARMFUL, LTXIConstants.CRYO_LIGHT_BLUE.argb32());
        addAttributeModifier(Attributes.ATTACK_SPEED, id.withSuffix(".attack_speed"), -0.25d, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        addAttributeModifier(Attributes.MOVEMENT_SPEED, id.withSuffix(".move_speed"), -0.25d, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        addAttributeModifier(Attributes.FLYING_SPEED, id.withSuffix(".fly_speed"), -0.25d, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        addAttributeModifier(Attributes.BLOCK_BREAK_SPEED, id.withSuffix(".dig_speed"), -0.25d, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier)
    {
        if (entity.canFreeze())
        {
            int ticksFrozen = entity.getTicksRequiredToFreeze() + 10;
            entity.setTicksFrozen(ticksFrozen);
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
        return LTXIParticles.CRYO_SNOWFLAKE.get();
    }
}