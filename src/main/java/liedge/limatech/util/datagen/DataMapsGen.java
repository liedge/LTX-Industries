package liedge.limatech.util.datagen;

import liedge.limatech.LimaTechTags;
import liedge.limatech.lib.CompoundValueOperation;
import liedge.limatech.lib.weapons.GlobalWeaponDamageModifiers;
import liedge.limatech.registry.LimaTechGameEvents;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.neoforged.neoforge.registries.datamaps.builtin.VibrationFrequency;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static liedge.limatech.lib.weapons.GlobalWeaponDamageModifiers.WeaponDamageModifier.create;
import static liedge.limatech.registry.LimaTechItems.*;

class DataMapsGen extends DataMapProvider
{
    DataMapsGen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider)
    {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather()
    {
        // Vibration frequencies
        builder(NeoForgeDataMaps.VIBRATION_FREQUENCIES)
                .add(LimaTechGameEvents.WEAPON_FIRED, new VibrationFrequency(3), false)
                .add(LimaTechGameEvents.PROJECTILE_EXPLODED, new VibrationFrequency(15), false);

        // Weapon damage modifiers
        builder(GlobalWeaponDamageModifiers.DATA_MAP_TYPE)
                .add(LimaTechTags.EntityTypes.HIGH_THREAT_LEVEL, List.of(
                        create(SUBMACHINE_GUN, -0.8f, CompoundValueOperation.ADD_MULTIPLIED_TOTAL),
                        create(SHOTGUN, -0.45f, CompoundValueOperation.ADD_MULTIPLIED_TOTAL)
                ), false)
                .add(LimaTechTags.EntityTypes.MEDIUM_THREAT_LEVEL, List.of(
                        create(SUBMACHINE_GUN, -0.5f, CompoundValueOperation.ADD_MULTIPLIED_TOTAL),
                        create(SHOTGUN, -0.4f, CompoundValueOperation.ADD_MULTIPLIED_TOTAL)
                ), false);
    }
}