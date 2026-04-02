package liedge.ltxindustries.data.generation;

import liedge.limacore.data.generation.LimaBootstrapUtil;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.registry.LTXIRegistries;
import liedge.ltxindustries.registry.bootstrap.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = LTXIndustries.MODID)
final class LTXIDatagen
{
    private LTXIDatagen() {}

    @SubscribeEvent
    public static void runDataGeneration(final GatherDataEvent.Client event)
    {
        PackOutput output = event.getGenerator().getPackOutput();

        // Patched registries w/ bootstrap objects
        DatapackBuiltinEntriesProvider dataRegistriesProvider = LimaBootstrapUtil.createDataPackProvider(output, event.getLookupProvider(), LTXIndustries.MODID, builder -> builder
                .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, LTXIBiomeModifiers::bootstrap)
                .add(Registries.CONFIGURED_FEATURE, LTXIConfiguredFeatures::bootstrap)
                .add(Registries.DAMAGE_TYPE, LTXIDamageTypes::bootstrap)
                .add(Registries.ENCHANTMENT, LTXIEnchantments::bootstrap)
                .add(LTXIRegistries.Keys.UPGRADES, context -> {
                    LTXIEquipmentUpgrades.bootstrap(context);
                    LTXIMachineUpgrades.bootstrap(context);
                })
                .add(Registries.PLACED_FEATURE, LTXIPlacedFeatures::bootstrap)
                .add(LTXIRegistries.Keys.RECIPE_MODES, LTXIRecipeModes::bootstrap));
        CompletableFuture<HolderLookup.Provider> registries = dataRegistriesProvider.getRegistryProvider();

        // Bootstrap registries and block tags
        BlockTagsGen blockTags = new BlockTagsGen(output, registries);
        event.addProvider(dataRegistriesProvider);

        // Providers
        event.addProvider(new BlockEntityTagsGen(output, registries));
        event.addProvider(blockTags);
        event.addProvider(new DamageModsGen(output, registries));
        event.addProvider(new DamageTagsGen(output, registries));
        event.addProvider(new DataMapsGen(output, registries));
        event.addProvider(new EnchantmentTagsGen(output, registries));
        event.addProvider(new EntityTagsGen(output, registries));
        event.addProvider(new FluidTagsGen(output, registries));
        event.addProvider(new GameEventsTagsGen(output, registries));
        event.addProvider(new ItemTagsGen(output, registries, blockTags.contentsGetter()));
        event.addProvider(new LanguageGen(output));
        event.addProvider(new LootModifiersGen(output, registries));
        event.addProvider(new LootTablesGen(output, registries));
        event.addProvider(new MobEffectTagsGen(output, registries));
        event.addProvider(new ModelsGen(output, LTXIndustries.RESOURCES));
        event.addProvider(new ParticlesGen(output));
        event.addProvider(new RecipesGen.Runner(output, registries));
        event.addProvider(new SoundsGen(output));
        event.addProvider(new SpriteSourcesGen(output, registries));
        event.addProvider(new UpgradeTagsGen(output, registries));
    }
}