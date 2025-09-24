package liedge.ltxindustries.data.generation;

import liedge.limacore.lib.math.MathOperation;
import liedge.ltxindustries.LTXITags;
import liedge.ltxindustries.lib.weapons.GlobalWeaponDamageModifiers;
import liedge.ltxindustries.registry.game.LTXIGameEvents;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.neoforged.neoforge.registries.datamaps.builtin.VibrationFrequency;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static liedge.ltxindustries.lib.weapons.GlobalWeaponDamageModifiers.WeaponDamageModifier.modifier;
import static liedge.ltxindustries.registry.game.LTXIItems.SHOTGUN;
import static liedge.ltxindustries.registry.game.LTXIItems.SUBMACHINE_GUN;

class DataMapsGen extends DataMapProvider
{
    DataMapsGen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider)
    {
        super(packOutput, lookupProvider);
    }

    private List<GlobalWeaponDamageModifiers.WeaponDamageModifier> makeModifierList(GlobalWeaponDamageModifiers.WeaponDamageModifier.Builder... builders)
    {
        return Stream.of(builders).map(GlobalWeaponDamageModifiers.WeaponDamageModifier.Builder::build).toList();
    }

    @Override
    protected void gather(HolderLookup.Provider registries)
    {
        // Vibration frequencies
        builder(NeoForgeDataMaps.VIBRATION_FREQUENCIES)
                .add(LTXIGameEvents.WEAPON_FIRED, new VibrationFrequency(3), false)
                .add(LTXIGameEvents.PROJECTILE_IMPACT, new VibrationFrequency(2), false);

        // Weapon damage modifiers
        builder(GlobalWeaponDamageModifiers.DATA_MAP_TYPE)
                .add(LTXITags.EntityTypes.HIGH_THREAT_TARGETS, makeModifierList(
                        modifier(MathOperation.ADD_TOTAL_PERCENT).forWeapon(SUBMACHINE_GUN).withConstantAmount(-0.8f),
                        modifier(MathOperation.ADD_TOTAL_PERCENT).forWeapon(SHOTGUN).withConstantAmount(-0.45f)
                ), false)
                .add(LTXITags.EntityTypes.MEDIUM_THREAT_TARGETS, makeModifierList(
                        modifier(MathOperation.ADD_TOTAL_PERCENT).forWeapon(SUBMACHINE_GUN).withConstantAmount(-0.5f),
                        modifier(MathOperation.ADD_TOTAL_PERCENT).forWeapon(SHOTGUN).withConstantAmount(-0.4f)
                ), false);
    }
}