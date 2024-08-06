package liedge.limatech.registry;

import liedge.limatech.LimaTech;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static liedge.limacore.lib.ModResources.prefixIdTranslationKey;

public final class LimaTechAttributes
{
    private LimaTechAttributes() {}

    private static final DeferredRegister<Attribute> ATTRIBUTES = LimaTech.RESOURCES.deferredRegister(Registries.ATTRIBUTE);

    public static void initRegister(IEventBus bus)
    {
        ATTRIBUTES.register(bus);
    }

    public static final DeferredHolder<Attribute, RangedAttribute> UNIVERSAL_STRENGTH = ATTRIBUTES.register("universal_strength",
            id -> new RangedAttribute(prefixIdTranslationKey("generic.attribute", id), 1d, 0d, 1024d));
}