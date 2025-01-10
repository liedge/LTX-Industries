package liedge.limatech.util.datagen;

import liedge.limatech.LimaTechTags;
import liedge.limatech.lib.LevelBasedDoubleValue;
import liedge.limatech.lib.upgradesystem.calculation.MultiplyBaseCalculation;
import liedge.limatech.lib.upgradesystem.calculation.OverrideBaseCalculation;
import liedge.limatech.lib.weapons.GlobalWeaponDamageModifiers;
import liedge.limatech.registry.LimaTechGameEvents;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.neoforged.neoforge.registries.datamaps.builtin.VibrationFrequency;

import java.util.concurrent.CompletableFuture;

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
                .add(LimaTechTags.EntityTypes.HIGH_THREAT_LEVEL, GlobalWeaponDamageModifiers.builder()
                        .add(SUBMACHINE_GUN, new OverrideBaseCalculation(LevelBasedDoubleValue.constant(0.5d)))
                        .add(SHOTGUN, new MultiplyBaseCalculation(LevelBasedDoubleValue.constant(-0.4d)))
                        .add(GRENADE_LAUNCHER, new MultiplyBaseCalculation(LevelBasedDoubleValue.constant(-0.2d)))
                        .build(), false)
                .add(LimaTechTags.EntityTypes.MEDIUM_THREAT_LEVEL, GlobalWeaponDamageModifiers.builder()
                        .add(SUBMACHINE_GUN, new MultiplyBaseCalculation(LevelBasedDoubleValue.constant(-0.4d)))
                        .add(SHOTGUN, new MultiplyBaseCalculation(LevelBasedDoubleValue.constant(-0.33d)))
                        .build(), false);
    }
}