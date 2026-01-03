package liedge.ltxindustries.data.generation;

import liedge.limacore.client.renderer.LimaCoreRenderTypes;
import liedge.limacore.data.generation.LimaItemModelProvider;
import liedge.limacore.lib.ModResources;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.registry.game.LTXIFluids;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder;
import net.neoforged.neoforge.client.model.generators.loaders.ItemLayerModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static liedge.limacore.util.LimaRegistryUtil.getItemName;
import static liedge.ltxindustries.client.LTXIItemOverrides.BRUSH_BRUSHING;
import static liedge.ltxindustries.client.LTXIItemOverrides.FISHING_ROD_CAST;
import static liedge.ltxindustries.registry.game.LTXIItems.*;

class ItemModelGen extends LimaItemModelProvider
{
    ItemModelGen(PackOutput output, ExistingFileHelper helper)
    {
        super(output, LTXIndustries.RESOURCES, helper);
    }

    @Override
    protected void registerModels()
    {
        // Standard (generated) models
        generated(RAW_TITANIUM,
                TITANIUM_INGOT,
                TITANIUM_NUGGET,
                RAW_NIOBIUM,
                NIOBIUM_INGOT,
                NIOBIUM_NUGGET,
                SPARK_FRUIT,
                VITRIOL_BERRIES,
                GLOOM_SHROOM,
                TITANIUM_GEAR,
                SLATESTEEL_GEAR,
                CIRCUIT_BOARD,
                T1_CIRCUIT,
                T2_CIRCUIT,
                T3_CIRCUIT,
                T4_CIRCUIT,
                T5_CIRCUIT,
                WONDERLAND_HEAD,
                WONDERLAND_BODY,
                WONDERLAND_LEGS,
                WONDERLAND_FEET,
                CARBON_DUST,
                RESINOUS_BIOMASS,
                ACIDIC_BIOMASS,
                DEEPSLATE_DUST,
                ELECTRIC_CHEMICAL,
                MONOMER_CHEMICAL,
                VIRIDIC_WEAPON_CHEMICAL,
                CHORUS_CHEMICAL,
                SCULK_CHEMICAL,
                NEURO_CHEMICAL,
                SLATESTEEL_INGOT,
                POLYMER_INGOT,
                SLATESTEEL_NUGGET,
                EMPTY_UPGRADE_MODULE,
                EQUIPMENT_UPGRADE_MODULE,
                MACHINE_UPGRADE_MODULE,
                EMPTY_FABRICATION_BLUEPRINT,
                FABRICATION_BLUEPRINT,
                ITEMS_IO_CONFIG_CARD,
                ENERGY_IO_CONFIG_CARD,
                FLUIDS_IO_CONFIG_CARD,
                LIGHTWEIGHT_WEAPON_ENERGY,
                SPECIALIST_WEAPON_ENERGY,
                EXPLOSIVES_WEAPON_ENERGY,
                HEAVY_WEAPON_ENERGY);

        // Buckets
        bucket(VIRIDIC_ACID_BUCKET, LTXIFluids.VIRIDIC_ACID);
        bucket(HYDROGEN_BUCKET, LTXIFluids.HYDROGEN).applyTint(true);
        bucket(OXYGEN_BUCKET, LTXIFluids.OXYGEN).applyTint(true);

        pigments(LTX_LIME_PIGMENT, ENERGY_BLUE_PIGMENT, ELECTRIC_CHARTREUSE_PIGMENT, VIRIDIC_GREEN_PIGMENT, NEURO_BLUE_PIGMENT);
        orePebbles(COAL_ORE_PEBBLES, COPPER_ORE_PEBBLES, IRON_ORE_PEBBLES, LAPIS_ORE_PEBBLES, REDSTONE_ORE_PEBBLES, GOLD_ORE_PEBBLES, DIAMOND_ORE_PEBBLES, EMERALD_ORE_PEBBLES, QUARTZ_ORE_PEBBLES, NETHERITE_ORE_PEBBLES, TITANIUM_ORE_PEBBLES, NIOBIUM_ORE_PEBBLES,
                TIN_ORE_PEBBLES, OSMIUM_ORE_PEBBLES, NICKEL_ORE_PEBBLES, LEAD_ORE_PEBBLES, SILVER_ORE_PEBBLES, URANIUM_ORE_PEBBLES);

        generated(itemFolderLocation("tech_salvage"), EXPLOSIVES_WEAPON_TECH_SALVAGE, TARGETING_TECH_SALVAGE);

        emissiveBiLayerModels(generatedModel, LTX_SHEARS, LTX_LIGHTER);
        emissiveBiLayerModels(handheldModel, LTX_DRILL, LTX_SWORD, LTX_SHOVEL, LTX_AXE, LTX_HOE, LTX_WRENCH);

        // Fishing rod
        final String fishingRodName = getItemName(LTX_FISHING_ROD);
        ModelFile handheldRod = existingModel(itemFolderLocation(ModResources.MC, "handheld_rod"));
        ModelFile castFishingRod = emissiveBiLayer(fishingRodName + "_cast", handheldRod);
        emissiveBiLayer(fishingRodName, handheldRod).override().model(castFishingRod).predicate(FISHING_ROD_CAST, 1f).end();

        // Brush
        final String brushName = getItemName(LTX_BRUSH);
        ResourceLocation brushBaseTex = itemFolderLocation(brushName + "_base");
        ResourceLocation brushEmissiveTex = itemFolderLocation(brushName + "_emissive");
        ModelFile brushing0 = emissiveBiLayer(brushName + "_brushing_0", existingModel(itemFolderLocation(ModResources.MC, "brush_brushing_0")), brushBaseTex, brushEmissiveTex);
        ModelFile brushing1 = emissiveBiLayer(brushName + "_brushing_1", existingModel(itemFolderLocation(ModResources.MC, "brush_brushing_1")), brushBaseTex, brushEmissiveTex);
        ModelFile brushing2 = emissiveBiLayer(brushName + "_brushing_2", existingModel(itemFolderLocation(ModResources.MC, "brush_brushing_2")), brushBaseTex, brushEmissiveTex);
        emissiveBiLayer(brushName, existingModel(itemFolderLocation(ModResources.MC, "brush")), brushBaseTex, brushEmissiveTex)
                .override().model(brushing0).predicate(BRUSH_BRUSHING, 0.25f).end()
                .override().model(brushing1).predicate(BRUSH_BRUSHING, 0.5f).end()
                .override().model(brushing2).predicate(BRUSH_BRUSHING, 0.75f).end();
    }

    private DynamicFluidContainerModelBuilder<ItemModelBuilder> bucket(ItemLike bucket, Supplier<? extends BaseFlowingFluid.Source> sourceFluid)
    {
        return getBuilder(bucket)
                .parent(existingModel(itemFolderLocation(ModResources.NEOFORGE, "bucket")))
                .customLoader(DynamicFluidContainerModelBuilder::begin)
                .fluid(sourceFluid.get());
    }

    private ItemModelBuilder emissiveBiLayer(ItemModelBuilder root, ModelFile parent, ResourceLocation baseTexture, ResourceLocation emissiveTexture)
    {
        return layersBuilder(root, baseTexture, emissiveTexture).parent(parent).customLoader(ItemLayerModelBuilder::begin).renderType(LimaCoreRenderTypes.EMISSIVE_SOLID_ITEM_NAME, 1).end();
    }

    private ItemModelBuilder emissiveBiLayer(String path, ModelFile parent, ResourceLocation baseTexture, ResourceLocation emissiveTexture)
    {
        return emissiveBiLayer(getBuilder(path), parent, baseTexture, emissiveTexture);
    }

    private ItemModelBuilder emissiveBiLayer(String modelName, ModelFile parent)
    {
        ResourceLocation texBase = itemFolderLocation(modelName);
        return emissiveBiLayer(modelName, parent, texBase.withSuffix("_base"), texBase.withSuffix("_emissive"));
    }

    private void emissiveBiLayerModels(ModelFile parent, ItemLike... items)
    {
        for (ItemLike item : items)
        {
            emissiveBiLayer(getItemName(item.asItem()), parent);
        }
    }

    private void pigments(ItemLike... pigments)
    {
        for (ItemLike item : pigments)
        {
            String name = getItemName(item.asItem()).split("_pigment")[0];
            generated(item, itemFolderLocation("pigment/" + name));
        }
    }

    private void orePebbles(ItemLike... pebbles)
    {
        for (ItemLike item : pebbles)
        {
            String name = getItemName(item.asItem()).split("_")[0];
            generated(item, itemFolderLocation("ore_pebble/" + name));
        }
    }

    private void generated(ResourceLocation texture, ItemLike... items)
    {
        Stream.of(items).forEach(i -> generated(i, texture));
    }
}