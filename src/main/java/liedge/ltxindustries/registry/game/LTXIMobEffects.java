package liedge.ltxindustries.registry.game;

import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.entity.effect.CorrosiveMobEffect;
import liedge.ltxindustries.entity.effect.FrostbiteMobEffect;
import liedge.ltxindustries.entity.effect.GloomMobEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
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
    public static final DeferredHolder<MobEffect, MobEffect> GLOOM = MOB_EFFECTS.register("gloom", GloomMobEffect::new);
}