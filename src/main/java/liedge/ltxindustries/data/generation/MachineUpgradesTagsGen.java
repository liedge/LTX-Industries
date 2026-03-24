package liedge.ltxindustries.data.generation;

import liedge.limacore.data.generation.LimaTagsProvider;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.lib.upgrades.machine.MachineUpgrade;
import liedge.ltxindustries.registry.LTXIRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

import static liedge.ltxindustries.LTXITags.MachineUpgrades.*;
import static liedge.ltxindustries.registry.bootstrap.LTXIMachineUpgrades.*;

class MachineUpgradesTagsGen extends LimaTagsProvider<MachineUpgrade>
{
    MachineUpgradesTagsGen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries)
    {
        super(packOutput, LTXIRegistries.Keys.MACHINE_UPGRADES, LTXIndustries.MODID, registries);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        buildTag(MACHINE_TIER).add(STANDARD_MACHINE_SYSTEMS, ULTIMATE_MACHINE_SYSTEMS);
        buildTag(PARALLEL_OPS_UPGRADES).add(GPM_PARALLEL, GEO_SYNTHESIZER_PARALLEL);
        buildTag(TARGET_PREDICATES).add(NEUTRAL_ENEMY_TARGETING, HOSTILE_TARGETING);
    }
}