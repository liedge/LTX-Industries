package liedge.limatech.data.generation;

import liedge.limacore.data.generation.LimaAdvancementGenerator;
import liedge.limatech.LimaTech;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = LimaTech.MODID, bus = EventBusSubscriber.Bus.MOD)
final class LimaTechDatagen
{
    private LimaTechDatagen() {}

    @SubscribeEvent
    public static void runDataGeneration(final GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> baseRegistries = event.getLookupProvider();

        final boolean runServer = event.includeServer();
        final boolean runClient = event.includeClient();

        BlockTagsGen blockTags = new BlockTagsGen(output, baseRegistries, helper);

        // Patched registries w/ boostrap objects
        DatapackBuiltinEntriesProvider dataRegistriesProvider = LimaTechBootstrap.create(output, baseRegistries);
        CompletableFuture<HolderLookup.Provider> patchedRegistries = dataRegistriesProvider.getRegistryProvider();

        // Server data
        generator.addProvider(runServer, new EquipmentUpgradesTagsGen(output, patchedRegistries, helper));
        generator.addProvider(runServer, dataRegistriesProvider);
        generator.addProvider(runServer, LimaAdvancementGenerator.createDataProvider(output, helper, patchedRegistries, AdvancementsGen::new));
        generator.addProvider(runServer, blockTags);
        generator.addProvider(runServer, new ItemTagsGen(output, patchedRegistries, blockTags.contentsGetter(), helper));
        generator.addProvider(runServer, new DamageTagsGen(output, patchedRegistries, helper));
        generator.addProvider(runServer, new EntityTagsGen(output, patchedRegistries, helper));
        generator.addProvider(runServer, new GameEventsTagsGen(output, patchedRegistries, helper));
        generator.addProvider(runServer, new BlockEntityTagsGen(output, patchedRegistries, helper));
        generator.addProvider(runServer, new LootTablesGen(output, patchedRegistries));
        generator.addProvider(runServer, new LootModifiersGen(output, patchedRegistries));
        generator.addProvider(runServer, new RecipesGen(output, patchedRegistries));
        generator.addProvider(runServer, new DataMapsGen(output, patchedRegistries));

        // Client assets
        generator.addProvider(runClient, new LanguageGen(output));
        generator.addProvider(runClient, new BlockStatesGen(output, helper));
        generator.addProvider(runClient, new ItemModelGen(output, helper));
        generator.addProvider(runClient, new ParticlesGen(output, helper));
        generator.addProvider(runClient, new SoundsGen(output, helper));
    }
}