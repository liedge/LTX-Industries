package liedge.limatech.data.generation;

import liedge.limacore.data.generation.LimaTagsProvider;
import liedge.limatech.LimaTech;
import liedge.limatech.lib.upgrades.machine.MachineUpgrade;
import liedge.limatech.registry.LimaTechRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static liedge.limatech.LimaTechTags.MachineUpgrades.MACHINE_TIER;
import static liedge.limatech.registry.bootstrap.LimaTechMachineUpgrades.ALPHA_MACHINE_SYSTEMS;
import static liedge.limatech.registry.bootstrap.LimaTechMachineUpgrades.EPSILON_MACHINE_SYSTEMS;

class MachineUpgradesTagsGen extends LimaTagsProvider<MachineUpgrade>
{
    MachineUpgradesTagsGen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries, @Nullable ExistingFileHelper helper)
    {
        super(packOutput, LimaTechRegistries.Keys.MACHINE_UPGRADES, LimaTech.MODID, registries, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        buildTag(MACHINE_TIER).add(ALPHA_MACHINE_SYSTEMS, EPSILON_MACHINE_SYSTEMS);
    }
}