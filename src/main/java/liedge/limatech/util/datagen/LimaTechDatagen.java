package liedge.limatech.util.datagen;

import liedge.limatech.LimaTech;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = LimaTech.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class LimaTechDatagen
{
    private LimaTechDatagen() {}

    @SubscribeEvent
    public static void runDataGeneration(final GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> registries = event.getLookupProvider();

        final boolean runServer = event.includeServer();
        final boolean runClient = event.includeClient();

        BlockTagsGen blockTags = new BlockTagsGen(output, registries, helper);
        AdvancementsGen advancements = new AdvancementsGen(helper);
        RegistriesDatapackGen dataPack = new RegistriesDatapackGen(helper);

        // Server data
        generator.addProvider(runServer, dataPack.buildProvider(output, registries, LimaTech.MODID));
        generator.addProvider(runServer, advancements.buildProvider(output, registries));
        generator.addProvider(runServer, blockTags);
        generator.addProvider(runServer, new ItemTagsGen(output, registries, blockTags.contentsGetter(), helper));
        generator.addProvider(runServer, new DamageTagsGen(output, registries, helper));
        generator.addProvider(runServer, new EntityTagsGen(output, registries, helper));
        generator.addProvider(runServer, new LootTablesGen(output, registries));
        generator.addProvider(runServer, new LootModifiersGen(output, registries));
        generator.addProvider(runServer, new RecipesGen(output, registries));

        // Client assets
        generator.addProvider(runClient, new LanguageGen(output));
        generator.addProvider(runClient, new BlockStatesGen(output, helper));
        generator.addProvider(runClient, new ItemModelGen(output, helper));
        generator.addProvider(runClient, new ParticlesGen(output, helper));
        generator.addProvider(runClient, new SoundsGen(output, helper));
    }
}