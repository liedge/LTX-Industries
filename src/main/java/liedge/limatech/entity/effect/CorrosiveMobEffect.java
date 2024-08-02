package liedge.limatech.entity.effect;

import liedge.limatech.lib.weapons.OrbGrenadeElement;
import liedge.limatech.registry.LimaTechParticles;
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
        super(MobEffectCategory.HARMFUL, OrbGrenadeElement.ACID.getColor().rgb());
        addAttributeModifier(Attributes.ARMOR, id.withSuffix(".armor_strip"), -0.3d, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    @Override
    public ParticleOptions createParticleOptions(MobEffectInstance instance)
    {
        return LimaTechParticles.ACID_FALL_AMBIENT.get();
    }
}