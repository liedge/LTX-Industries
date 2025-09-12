package liedge.ltxindustries.data.generation;

import liedge.limacore.data.generation.LimaTagsProvider;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.registry.LTXIRegistries;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrade;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static liedge.ltxindustries.LTXITags.EquipmentUpgrades.*;
import static liedge.ltxindustries.registry.bootstrap.LTXIEquipmentUpgrades.*;

class EquipmentUpgradesTagsGen extends LimaTagsProvider<EquipmentUpgrade>
{
    EquipmentUpgradesTagsGen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries, @Nullable ExistingFileHelper helper)
    {
        super(packOutput, LTXIRegistries.Keys.EQUIPMENT_UPGRADES, LTXIndustries.MODID, registries, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        buildTag(MINING_LEVEL_UPGRADES).add(TOOL_NETHERITE_LEVEL);
        buildTag(MINING_DROPS_MODIFIERS).add(SILK_TOUCH_ENCHANTMENT, FORTUNE_ENCHANTMENT);
        buildTag(AMMO_SOURCE_MODIFIERS).add(LIGHTWEIGHT_ENERGY_ADAPTER, SPECIALIST_ENERGY_ADAPTER, EXPLOSIVES_ENERGY_ADAPTER, HEAVY_ENERGY_ADAPTER, UNIVERSAL_INFINITE_AMMO);
    }
}