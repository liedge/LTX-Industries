package liedge.limatech.data.generation;

import liedge.limacore.data.generation.LimaBootstrapUtil;
import liedge.limatech.LimaTech;
import liedge.limatech.registry.LimaTechRegistries;
import liedge.limatech.registry.bootstrap.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.concurrent.CompletableFuture;

public final class LimaTechBootstrap
{
    private LimaTechBootstrap() {}
    
    public static DatapackBuiltinEntriesProvider create(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> baseRegistries)
    {
        return LimaBootstrapUtil.createDataPackProvider(packOutput, baseRegistries, LimaTech.MODID, builder -> builder
                .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, LimaTechBiomeModifiers::bootstrap)
                .add(Registries.CONFIGURED_FEATURE, LimaTechConfiguredFeatures::bootstrap)
                .add(Registries.DAMAGE_TYPE, LimaTechDamageTypes::bootstrap)
                .add(Registries.ENCHANTMENT, LimaTechEnchantments::bootstrap)
                .add(LimaTechRegistries.Keys.EQUIPMENT_UPGRADES, LimaTechEquipmentUpgrades::bootstrap)
                .add(LimaTechRegistries.Keys.MACHINE_UPGRADES, LimaTechMachineUpgrades::bootstrap)
                .add(Registries.PLACED_FEATURE, LimaTechPlacedFeatures::bootstrap));
    }
}