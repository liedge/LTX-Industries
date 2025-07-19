package liedge.ltxindustries.registry.game;

import liedge.limacore.lib.SimpleMobEffect;
import liedge.limacore.registry.game.LimaCoreAttributes;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.entity.effect.CorrosiveMobEffect;
import liedge.ltxindustries.entity.effect.FrostbiteMobEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LTXIMobEffects
{
    private LTXIMobEffects() {}

    private static final DeferredRegister<MobEffect> MOB_EFFECTS = LTXIndustries.RESOURCES.deferredRegister(Registries.MOB_EFFECT);

    public static void register(IEventBus bus)
    {
        MOB_EFFECTS.register(bus);
    }

    public static final DeferredHolder<MobEffect, MobEffect> FROSTBITE = MOB_EFFECTS.register("frostbite", FrostbiteMobEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> CORROSIVE = MOB_EFFECTS.register("corrosive", CorrosiveMobEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> NEURO_SUPPRESSED = MOB_EFFECTS.register("neuro_suppressed", id -> new SimpleMobEffect(MobEffectCategory.HARMFUL, LTXIConstants.NEURO_BLUE.argb32())
            .addAttributeModifier(LimaCoreAttributes.DAMAGE_MULTIPLIER, id.withSuffix(".damage_multiplier"), -0.25d, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
}