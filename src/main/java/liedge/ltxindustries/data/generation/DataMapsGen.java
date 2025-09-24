package liedge.ltxindustries.data.generation;

import liedge.ltxindustries.registry.game.LTXIGameEvents;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.neoforged.neoforge.registries.datamaps.builtin.VibrationFrequency;

import java.util.concurrent.CompletableFuture;

class DataMapsGen extends DataMapProvider
{
    DataMapsGen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider)
    {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider registries)
    {
        // Vibration frequencies
        builder(NeoForgeDataMaps.VIBRATION_FREQUENCIES)
                .add(LTXIGameEvents.WEAPON_FIRED, new VibrationFrequency(3), false)
                .add(LTXIGameEvents.PROJECTILE_IMPACT, new VibrationFrequency(2), false);
    }
}