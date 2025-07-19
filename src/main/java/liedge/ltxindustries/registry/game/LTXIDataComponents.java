package liedge.ltxindustries.registry.game;

import com.mojang.serialization.Codec;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgradeEntry;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrades;
import liedge.ltxindustries.lib.upgrades.machine.MachineUpgradeEntry;
import liedge.ltxindustries.lib.upgrades.machine.MachineUpgrades;
import liedge.ltxindustries.lib.weapons.GrenadeType;
import liedge.ltxindustries.lib.weapons.WeaponAmmoSource;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LTXIDataComponents
{
    private LTXIDataComponents() {}

    private static final DeferredRegister.DataComponents TYPES = LTXIndustries.RESOURCES.deferredDataComponents();

    public static void register(IEventBus bus)
    {
        TYPES.register(bus);
    }

    // Normal data components
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<EquipmentUpgrades>> EQUIPMENT_UPGRADES = TYPES.registerComponentType("equipment_upgrades", builder -> builder.persistent(EquipmentUpgrades.CODEC).networkSynchronized(EquipmentUpgrades.STREAM_CODEC).cacheEncoding());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MachineUpgrades>> MACHINE_UPGRADES = TYPES.registerComponentType("machine_upgrades", builder -> builder.persistent(MachineUpgrades.CODEC).networkSynchronized(MachineUpgrades.STREAM_CODEC).cacheEncoding());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<EquipmentUpgradeEntry>> EQUIPMENT_UPGRADE_ENTRY = TYPES.registerComponentType("equipment_upgrade", builder -> builder.persistent(EquipmentUpgradeEntry.CODEC).networkSynchronized(EquipmentUpgradeEntry.STREAM_CODEC).cacheEncoding());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MachineUpgradeEntry>> MACHINE_UPGRADE_ENTRY = TYPES.registerComponentType("machine_upgrade", builder -> builder.persistent(MachineUpgradeEntry.CODEC).networkSynchronized(MachineUpgradeEntry.STREAM_CODEC).cacheEncoding());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> LAST_EQUIPMENT_HASH = TYPES.registerComponentType("last_equipment_hash", builder -> builder.persistent(Codec.INT).cacheEncoding()); // No network sync, unnecessary
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> BLUEPRINT_RECIPE = TYPES.registerComponentType("blueprint_recipe", builder -> builder.persistent(ResourceLocation.CODEC).networkSynchronized(ResourceLocation.STREAM_CODEC).cacheEncoding());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> WEAPON_AMMO = TYPES.registerComponentType("weapon_ammo", builder -> builder.persistent(ExtraCodecs.intRange(0, 1000)).networkSynchronized(ByteBufCodecs.VAR_INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WeaponAmmoSource>> WEAPON_AMMO_SOURCE = TYPES.registerComponentType("ammo_source", builder -> builder.persistent(WeaponAmmoSource.CODEC).networkSynchronized(WeaponAmmoSource.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GrenadeType>> GRENADE_TYPE = TYPES.registerComponentType("grenade_type", builder -> builder.persistent(GrenadeType.CODEC).networkSynchronized(GrenadeType.STREAM_CODEC));
}