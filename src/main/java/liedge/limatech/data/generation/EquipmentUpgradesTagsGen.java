package liedge.limatech.data.generation;

import liedge.limacore.data.generation.LimaTagsProvider;
import liedge.limatech.LimaTech;
import liedge.limatech.registry.LimaTechRegistries;
import liedge.limatech.lib.upgrades.equipment.EquipmentUpgrade;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static liedge.limatech.LimaTechTags.EquipmentUpgrades.*;
import static liedge.limatech.registry.bootstrap.LimaTechEquipmentUpgrades.*;

class EquipmentUpgradesTagsGen extends LimaTagsProvider<EquipmentUpgrade>
{
    EquipmentUpgradesTagsGen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries, @Nullable ExistingFileHelper helper)
    {
        super(packOutput, LimaTechRegistries.Keys.EQUIPMENT_UPGRADES, LimaTech.MODID, registries, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        buildTag(DRILL_MINING_UPGRADES).add(DRILL_DIAMOND_LEVEL, DRILL_NETHERITE_LEVEL);
        buildTag(MINING_DROPS_MODIFIERS).add(SILK_TOUCH_ENCHANT, FORTUNE_ENCHANTMENT);
        buildTag(AMMO_SOURCE_MODIFIERS).add(UNIVERSAL_ENERGY_AMMO, UNIVERSAL_INFINITE_AMMO);
    }
}