package liedge.limatech.registry;

import liedge.limatech.LimaTech;
import liedge.limatech.entity.effect.CorrosiveMobEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LimaTechMobEffects
{
    private LimaTechMobEffects() {}

    private static final DeferredRegister<MobEffect> MOB_EFFECTS = LimaTech.RESOURCES.deferredRegister(Registries.MOB_EFFECT);

    public static void initRegister(IEventBus bus)
    {
        MOB_EFFECTS.register(bus);
    }

    public static final DeferredHolder<MobEffect, MobEffect> CORROSIVE = MOB_EFFECTS.register("corrosive", CorrosiveMobEffect::new);
}