package liedge.ltxindustries.registry.game;

import liedge.limacore.registry.LimaDeferredAttributes;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.lib.shield.EntityBubbleShield;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class LTXIAttributes
{
    private LTXIAttributes() {}

    private static final LimaDeferredAttributes ATTRIBUTES = LTXIndustries.RESOURCES.deferredAttributes();

    public static void register(IEventBus bus)
    {
        ATTRIBUTES.register(bus);
    }

    public static final DeferredHolder<Attribute, RangedAttribute> SHIELD_CAPACITY = ATTRIBUTES.registerAttribute("shield_capacity", 0, 0, EntityBubbleShield.GLOBAL_MAX_SHIELD, true, Attribute.Sentiment.POSITIVE);
}