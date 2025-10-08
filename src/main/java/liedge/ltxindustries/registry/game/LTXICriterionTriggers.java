package liedge.ltxindustries.registry.game;

import liedge.limacore.advancement.ItemBrokenTrigger;
import liedge.ltxindustries.LTXIndustries;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LTXICriterionTriggers
{
    private LTXICriterionTriggers() {}

    private static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = LTXIndustries.RESOURCES.deferredRegister(Registries.TRIGGER_TYPE);

    public static void register(IEventBus bus)
    {
        TRIGGERS.register(bus);
    }

    public static final DeferredHolder<CriterionTrigger<?>, ItemBrokenTrigger> ITEM_BROKEN_BY_ACID = TRIGGERS.register("item_broken_by_acid", ItemBrokenTrigger::new);
}