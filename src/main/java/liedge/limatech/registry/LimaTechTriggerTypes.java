package liedge.limatech.registry;

import liedge.limatech.LimaTech;
import liedge.limatech.advancement.KilledWithWeaponTrigger;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LimaTechTriggerTypes
{
    private LimaTechTriggerTypes() {}

    private static final DeferredRegister<CriterionTrigger<?>> TRIGGER_TYPES = LimaTech.RESOURCES.deferredRegister(Registries.TRIGGER_TYPE);

    public static void initRegister(IEventBus bus)
    {
        TRIGGER_TYPES.register(bus);
    }

    public static final DeferredHolder<CriterionTrigger<?>, KilledWithWeaponTrigger> KILLED_WITH_WEAPON = TRIGGER_TYPES.register("killed_with_weapon", KilledWithWeaponTrigger::new);
}