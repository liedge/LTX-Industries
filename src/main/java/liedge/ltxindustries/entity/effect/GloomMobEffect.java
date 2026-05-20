package liedge.ltxindustries.entity.effect;

import liedge.limacore.registry.game.LimaCoreAttributes;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.registry.game.LTXIParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class GloomMobEffect extends MobEffect
{
    public GloomMobEffect(Identifier id)
    {
        super(MobEffectCategory.HARMFUL, LTXIConstants.GLOOM_BLUE.argb32());

        addAttributeModifier(LimaCoreAttributes.DAMAGE_MULTIPLIER, id.withSuffix(".damage_weakness"), -0.25d, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    @Override
    public ParticleOptions createParticleOptions(MobEffectInstance instance)
    {
        return LTXIParticles.SMALL_GLOOM_PUFF.get();
    }
}