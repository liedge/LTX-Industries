package liedge.limatech.registry;

import liedge.limatech.LimaTech;
import liedge.limatech.lib.weapons.OrbGrenadeElement;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LimaTechDataComponents
{
    private LimaTechDataComponents() {}

    private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = LimaTech.RESOURCES.deferredRegister(Registries.DATA_COMPONENT_TYPE);

    public static void initRegister(IEventBus bus)
    {
        DATA_COMPONENT_TYPES.register(bus);
    }

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<OrbGrenadeElement>> GRENADE_ELEMENT = DATA_COMPONENT_TYPES.register("grenade_element",
            () -> DataComponentType.<OrbGrenadeElement>builder().persistent(OrbGrenadeElement.CODEC).networkSynchronized(OrbGrenadeElement.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> WEAPON_AMMO = DATA_COMPONENT_TYPES.register("weapon_ammo",
            () -> DataComponentType.<Integer>builder().persistent(ExtraCodecs.intRange(0, 1000)).networkSynchronized(ByteBufCodecs.VAR_INT).build());
}