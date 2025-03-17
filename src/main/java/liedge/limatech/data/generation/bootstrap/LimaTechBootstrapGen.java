package liedge.limatech.data.generation.bootstrap;

import liedge.limacore.data.generation.RegistryBootstrapExtensions;
import liedge.limatech.LimaTech;
import liedge.limatech.registry.LimaTechRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.concurrent.CompletableFuture;

public final class LimaTechBootstrapGen
{
    private LimaTechBootstrapGen() {}

    public static DatapackBuiltinEntriesProvider create(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> baseRegistries)
    {
        return RegistryBootstrapExtensions.createDataPackProvider(packOutput, baseRegistries, LimaTech.MODID, builder -> builder
                .add(Registries.DAMAGE_TYPE, new DamageTypes())
                .add(Registries.CONFIGURED_FEATURE, new ConfiguredFeatures())
                .add(Registries.PLACED_FEATURE, new PlacedFeatures())
                .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, new BiomeModifiers())
                .add(Registries.ENCHANTMENT, new Enchantments())
                .add(LimaTechRegistries.Keys.EQUIPMENT_UPGRADES, new EquipmentUpgrades())
                .add(LimaTechRegistries.Keys.MACHINE_UPGRADES, new MachineUpgrades()));
    }
}