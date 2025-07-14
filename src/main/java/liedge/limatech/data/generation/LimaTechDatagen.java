package liedge.limatech.data.generation;

import liedge.limacore.data.generation.LimaAdvancementGenerator;
import liedge.limacore.data.generation.LimaBootstrapUtil;
import liedge.limatech.LimaTech;
import liedge.limatech.registry.LimaTechRegistries;
import liedge.limatech.registry.bootstrap.*;
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

@EventBusSubscriber(modid = LimaTech.MODID)
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
        DatapackBuiltinEntriesProvider dataRegistriesProvider = LimaBootstrapUtil.createDataPackProvider(output, baseRegistries, LimaTech.MODID, builder -> builder
                .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, LimaTechBiomeModifiers::bootstrap)
                .add(Registries.CONFIGURED_FEATURE, LimaTechConfiguredFeatures::bootstrap)
                .add(Registries.DAMAGE_TYPE, LimaTechDamageTypes::bootstrap)
                .add(Registries.ENCHANTMENT, LimaTechEnchantments::bootstrap)
                .add(LimaTechRegistries.Keys.EQUIPMENT_UPGRADES, LimaTechEquipmentUpgrades::bootstrap)
                .add(LimaTechRegistries.Keys.MACHINE_UPGRADES, LimaTechMachineUpgrades::bootstrap)
                .add(Registries.PLACED_FEATURE, LimaTechPlacedFeatures::bootstrap));
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
        generator.addProvider(runServer, new MachineUpgradesTagsGen(output, patchedRegistries, helper));
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
        generator.addProvider(runClient, new SpriteSourcesGen(output, patchedRegistries, helper));
    }
}