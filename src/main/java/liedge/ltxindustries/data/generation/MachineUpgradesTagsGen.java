package liedge.ltxindustries.data.generation;

import liedge.limacore.data.generation.LimaTagsProvider;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.lib.upgrades.machine.MachineUpgrade;
import liedge.ltxindustries.registry.LTXIRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static liedge.ltxindustries.LTXITags.MachineUpgrades.MACHINE_TIER;
import static liedge.ltxindustries.registry.bootstrap.LTXIMachineUpgrades.STANDARD_MACHINE_SYSTEMS;
import static liedge.ltxindustries.registry.bootstrap.LTXIMachineUpgrades.ULTIMATE_MACHINE_SYSTEMS;

class MachineUpgradesTagsGen extends LimaTagsProvider<MachineUpgrade>
{
    MachineUpgradesTagsGen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries, @Nullable ExistingFileHelper helper)
    {
        super(packOutput, LTXIRegistries.Keys.MACHINE_UPGRADES, LTXIndustries.MODID, registries, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        buildTag(MACHINE_TIER).add(STANDARD_MACHINE_SYSTEMS, ULTIMATE_MACHINE_SYSTEMS);
    }
}