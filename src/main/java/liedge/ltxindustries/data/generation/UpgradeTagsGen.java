package liedge.ltxindustries.data.generation;

import liedge.limacore.data.generation.LimaTagsProvider;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.lib.upgrades.Upgrade;
import liedge.ltxindustries.registry.LTXIRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

import static liedge.ltxindustries.LTXITags.Upgrades.*;
import static liedge.ltxindustries.registry.bootstrap.LTXIUpgrades.*;

class UpgradeTagsGen extends LimaTagsProvider<Upgrade>
{
    UpgradeTagsGen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries)
    {
        super(packOutput, LTXIRegistries.Keys.UPGRADES, LTXIndustries.MODID, registries);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries)
    {
        buildTag(MINING_LEVELS).add(TOOL_NETHERITE_LEVEL);
        buildTag(MINING_DROPS_MODIFIERS).add(SILK_TOUCH_ENCHANTMENT, FORTUNE_ENCHANTMENT);
        buildTag(RELOAD_SOURCE_MODIFIERS).add(LIGHTWEIGHT_ENERGY_ADAPTER, SPECIALIST_ENERGY_ADAPTER, EXPLOSIVES_ENERGY_ADAPTER, HEAVY_ENERGY_ADAPTER, INFINITE_AMMO);

        buildTag(MACHINE_TIER).add(STANDARD_MACHINE_SYSTEMS, ULTIMATE_MACHINE_SYSTEMS);
        buildTag(PARALLEL_OPS_UPGRADES).add(GPM_PARALLEL, GEO_SYNTHESIZER_PARALLEL);
        buildTag(TARGET_PREDICATES).add(ALL_ENTITIES_TARGETING, NEUTRAL_ENEMY_TARGETING, HOSTILE_TARGETING);
    }
}