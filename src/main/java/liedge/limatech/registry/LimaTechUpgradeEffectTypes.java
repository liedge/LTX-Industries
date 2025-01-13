package liedge.limatech.registry;

import liedge.limatech.lib.upgradesystem.effect.UpgradeEffectType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import static liedge.limatech.LimaTech.RESOURCES;

public final class LimaTechUpgradeEffectTypes
{
    private static final DeferredRegister<UpgradeEffectType<?>> TYPES = RESOURCES.deferredRegister(LimaTechRegistries.UPGRADE_EFFECT_TYPE_KEY);

    private LimaTechUpgradeEffectTypes() {}

    public static void initRegister(IEventBus bus)
    {
        TYPES.register(bus);
    }
}