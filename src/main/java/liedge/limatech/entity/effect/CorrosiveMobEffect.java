package liedge.limatech.entity.effect;

import liedge.limatech.LimaTechConstants;
import liedge.limatech.registry.game.LimaTechParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class CorrosiveMobEffect extends MobEffect
{
    public CorrosiveMobEffect(ResourceLocation id)
    {
        super(MobEffectCategory.HARMFUL, LimaTechConstants.ACID_GREEN.packedRGB());
        addAttributeModifier(Attributes.ARMOR, id.withSuffix(".armor_strip"), -0.33d, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        addAttributeModifier(Attributes.ARMOR_TOUGHNESS, id.withSuffix(".armor_toughness_strip"), -0.33d, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    @Override
    public ParticleOptions createParticleOptions(MobEffectInstance instance)
    {
        return LimaTechParticles.CORROSIVE_DRIP.get();
    }
}