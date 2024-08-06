package liedge.limatech.util.datagen;

import liedge.limacore.data.generation.LimaTagsProvider;
import liedge.limatech.LimaTech;
import liedge.limatech.registry.LimaTechRegistries;
import liedge.limatech.upgradesystem.EquipmentUpgrade;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static liedge.limatech.LimaTechTags.EquipmentUpgrades.*;
import static liedge.limatech.registry.LimaTechEquipmentUpgrades.*;

class EquipmentUpgradesTagsGen extends LimaTagsProvider<EquipmentUpgrade>
{
    EquipmentUpgradesTagsGen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries, @Nullable ExistingFileHelper helper)
    {
        super(packOutput, LimaTechRegistries.EQUIPMENT_UPGRADES_KEY, LimaTech.MODID, registries, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        buildTag(AMMO_SOURCE_MODIFIERS).add(UNIVERSAL_ENERGY_AMMO, UNIVERSAL_INFINITE_AMMO);
    }
}