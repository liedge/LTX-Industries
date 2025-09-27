package liedge.ltxindustries.data.generation;

import liedge.limacore.data.generation.LimaTagsProvider;
import liedge.ltxindustries.LTXIndustries;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static liedge.ltxindustries.LTXITags.Fluids.HYDROGEN_FLUIDS;
import static liedge.ltxindustries.LTXITags.Fluids.OXYGEN_FLUIDS;
import static liedge.ltxindustries.registry.game.LTXIFluids.*;

class FluidTagsGen extends LimaTagsProvider.RegistryTags<Fluid>
{
    FluidTagsGen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper helper)
    {
        super(packOutput, BuiltInRegistries.FLUID, LTXIndustries.MODID, lookupProvider, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        buildTag(HYDROGEN_FLUIDS).add(HYDROGEN, FLOWING_HYDROGEN);
        buildTag(OXYGEN_FLUIDS).add(OXYGEN, FLOWING_OXYGEN);
    }
}