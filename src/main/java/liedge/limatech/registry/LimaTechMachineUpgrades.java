package liedge.limatech.registry;

import liedge.limatech.lib.upgrades.machine.MachineUpgrade;
import net.minecraft.resources.ResourceKey;

import static liedge.limatech.LimaTech.RESOURCES;

public final class LimaTechMachineUpgrades
{
    private LimaTechMachineUpgrades() {}

    // Built-in upgrade resource keys
    public static final ResourceKey<MachineUpgrade> ESA_CAPACITY_UPGRADE = key("esa_capacity");
    public static final ResourceKey<MachineUpgrade> GENERAL_PROCESSING_UPGRADE = key("general_processing");
    public static final ResourceKey<MachineUpgrade> FABRICATOR_UPGRADE = key("fabricator_upgrade");

    private static ResourceKey<MachineUpgrade> key(String name)
    {
        return RESOURCES.resourceKey(LimaTechRegistries.MACHINE_UPGRADES_KEY, name);
    }
}