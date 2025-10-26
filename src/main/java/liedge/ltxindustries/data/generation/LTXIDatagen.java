package liedge.ltxindustries.data.generation;

import liedge.limacore.data.generation.LimaAdvancementGenerator;
import liedge.limacore.data.generation.LimaBootstrapUtil;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.registry.LTXIRegistries;
import liedge.ltxindustries.registry.bootstrap.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = LTXIndustries.MODID)
final class LTXIDatagen
{
    private LTXIDatagen() {}

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
        DatapackBuiltinEntriesProvider dataRegistriesProvider = LimaBootstrapUtil.createDataPackProvider(output, baseRegistries, LTXIndustries.MODID, builder -> builder
                .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, LTXIBiomeModifiers::bootstrap)
                .add(Registries.CONFIGURED_FEATURE, LTXIConfiguredFeatures::bootstrap)
                .add(Registries.DAMAGE_TYPE, LTXIDamageTypes::bootstrap)
                .add(Registries.ENCHANTMENT, LTXIEnchantments::bootstrap)
                .add(LTXIRegistries.Keys.EQUIPMENT_UPGRADES, LTXIEquipmentUpgrades::bootstrap)
                .add(LTXIRegistries.Keys.MACHINE_UPGRADES, LTXIMachineUpgrades::bootstrap)
                .add(Registries.PLACED_FEATURE, LTXIPlacedFeatures::bootstrap)
                .add(LTXIRegistries.Keys.RECIPE_MODES, LTXIRecipeModes::bootstrap));
        CompletableFuture<HolderLookup.Provider> patchedRegistries = dataRegistriesProvider.getRegistryProvider();

        // Server data
        generator.addProvider(runServer, dataRegistriesProvider);
        generator.addProvider(runServer, LimaAdvancementGenerator.createDataProvider(output, helper, patchedRegistries, AdvancementsGen::new));
        generator.addProvider(runServer, blockTags);
        generator.addProvider(runServer, new ItemTagsGen(output, patchedRegistries, blockTags.contentsGetter(), helper));
        generator.addProvider(runServer, new DamageTagsGen(output, patchedRegistries, helper));
        generator.addProvider(runServer, new EnchantmentTagsGen(output, patchedRegistries, helper));
        generator.addProvider(runServer, new EntityTagsGen(output, patchedRegistries, helper));
        generator.addProvider(runServer, new GameEventsTagsGen(output, patchedRegistries, helper));
        generator.addProvider(runServer, new BlockEntityTagsGen(output, patchedRegistries, helper));
        generator.addProvider(runServer, new EquipmentUpgradesTagsGen(output, patchedRegistries, helper));
        generator.addProvider(runServer, new FluidTagsGen(output, patchedRegistries, helper));
        generator.addProvider(runServer, new MachineUpgradesTagsGen(output, patchedRegistries, helper));
        generator.addProvider(runServer, new LootTablesGen(output, patchedRegistries));
        generator.addProvider(runServer, new LootModifiersGen(output, patchedRegistries));
        generator.addProvider(runServer, new RecipesGen(output, patchedRegistries));
        generator.addProvider(runServer, new DataMapsGen(output, patchedRegistries));
        generator.addProvider(runServer, new DamageModsGen(output, patchedRegistries, helper));

        // Client assets
        generator.addProvider(runClient, new LanguageGen(output));
        generator.addProvider(runClient, new BlockStatesGen(output, helper));
        generator.addProvider(runClient, new ItemModelGen(output, helper));
        generator.addProvider(runClient, new ParticlesGen(output, helper));
        generator.addProvider(runClient, new SoundsGen(output, helper));
        generator.addProvider(runClient, new SpriteSourcesGen(output, patchedRegistries, helper));
    }
}