package liedge.limatech.registry;

import liedge.limatech.LimaTech;
import liedge.limatech.lib.weapons.GrenadeType;
import liedge.limatech.lib.weapons.WeaponAmmoSource;
import liedge.limatech.upgradesystem.ItemEquipmentUpgrades;
import liedge.limatech.upgradesystem.EquipmentUpgradeEntry;
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

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemEquipmentUpgrades>> EQUIPMENT_UPGRADES = DATA_COMPONENT_TYPES.register("upgrades",
            () -> DataComponentType.<ItemEquipmentUpgrades>builder().persistent(ItemEquipmentUpgrades.CODEC).networkSynchronized(ItemEquipmentUpgrades.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<EquipmentUpgradeEntry>> UPGRADE_ITEM_DATA = DATA_COMPONENT_TYPES.register("upgrade_data",
            () -> DataComponentType.<EquipmentUpgradeEntry>builder().persistent(EquipmentUpgradeEntry.CODEC).networkSynchronized(EquipmentUpgradeEntry.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> WEAPON_AMMO = DATA_COMPONENT_TYPES.register("weapon_ammo",
            () -> DataComponentType.<Integer>builder().persistent(ExtraCodecs.intRange(0, 1000)).networkSynchronized(ByteBufCodecs.VAR_INT).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WeaponAmmoSource>> WEAPON_AMMO_SOURCE = DATA_COMPONENT_TYPES.register("ammo_source",
            () -> DataComponentType.<WeaponAmmoSource>builder().persistent(WeaponAmmoSource.CODEC).networkSynchronized(WeaponAmmoSource.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GrenadeType>> GRENADE_TYPE = DATA_COMPONENT_TYPES.register("grenade_type",
            () -> DataComponentType.<GrenadeType>builder().persistent(GrenadeType.CODEC).networkSynchronized(GrenadeType.STREAM_CODEC).build());
}