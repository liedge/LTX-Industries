package liedge.limatech.util.datagen;

import liedge.limatech.LimaTechTags;
import liedge.limatech.lib.weapons.GlobalWeaponDamageModifiers;
import liedge.limatech.registry.LimaTechGameEvents;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.neoforged.neoforge.registries.datamaps.builtin.VibrationFrequency;

import java.util.concurrent.CompletableFuture;

import static liedge.limatech.lib.weapons.WeaponContextCalculation.multiplyBase;
import static liedge.limatech.lib.weapons.WeaponContextCalculation.overrideBase;
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
                        .add(SUBMACHINE_GUN, overrideBase(0.5f))
                        .add(SHOTGUN, multiplyBase(0.6f))
                        .add(GRENADE_LAUNCHER, multiplyBase(0.85f))
                        .build(), false)
                .add(LimaTechTags.EntityTypes.MEDIUM_THREAT_LEVEL, GlobalWeaponDamageModifiers.builder()
                        .add(SUBMACHINE_GUN, multiplyBase(0.4f))
                        .add(SHOTGUN, multiplyBase(0.8f))
                        .build(), false);
    }
}