package liedge.ltxindustries.data.generation;

import liedge.limacore.data.generation.LimaItemModelProvider;
import liedge.limacore.lib.ModResources;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.model.baked.EmissiveBiLayerGeometry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Arrays;
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
                VITRIOL_BERRIES,
                WHITE_PIGMENT,
                LIGHT_BLUE_PIGMENT,
                LIME_PIGMENT,
                CIRCUIT_BOARD,
                T1_CIRCUIT,
                T2_CIRCUIT,
                T3_CIRCUIT,
                T4_CIRCUIT,
                T5_CIRCUIT,
                DEEPSLATE_POWDER,
                SLATE_ALLOY_INGOT,
                SLATE_ALLOY_NUGGET,
                BEDROCK_ALLOY_INGOT,
                EMPTY_UPGRADE_MODULE,
                EQUIPMENT_UPGRADE_MODULE,
                MACHINE_UPGRADE_MODULE,
                EMPTY_FABRICATION_BLUEPRINT,
                FABRICATION_BLUEPRINT,
                LIGHTWEIGHT_WEAPON_ENERGY,
                SPECIALIST_WEAPON_ENERGY,
                EXPLOSIVES_WEAPON_ENERGY,
                HEAVY_WEAPON_ENERGY);

        orePebbles(COAL_ORE_PEBBLES, COPPER_ORE_PEBBLES, IRON_ORE_PEBBLES, LAPIS_ORE_PEBBLES, REDSTONE_ORE_PEBBLES, GOLD_ORE_PEBBLES, DIAMOND_ORE_PEBBLES, EMERALD_ORE_PEBBLES, QUARTZ_ORE_PEBBLES, NETHERITE_ORE_PEBBLES, TITANIUM_ORE_PEBBLES, NIOBIUM_ORE_PEBBLES);

        generated(itemFolderLocation("tech_salvage"), EXPLOSIVES_WEAPON_TECH_SALVAGE, TARGETING_TECH_SALVAGE);

        emissiveBiLayerModels(generatedModel, LTX_SHEARS, LTX_LIGHTER);
        emissiveBiLayerModels(handheldModel, LTX_DRILL, LTX_SWORD, LTX_SHOVEL, LTX_AXE, LTX_HOE, LTX_WRENCH);

        // Fishing rod
        ModelFile rodModel = new ModelFile.UncheckedModelFile("item/handheld_rod");
        ModelFile ltxCastFishingRod = emissiveBiLayer("ltx_fishing_rod_cast", rodModel, itemFolderLocation("ltx_fishing_rod_cast_base"), itemFolderLocation("ltx_fishing_rod_emissive"));
        emissiveBiLayer("ltx_fishing_rod", rodModel, itemFolderLocation("ltx_fishing_rod_base"), itemFolderLocation("ltx_fishing_rod_emissive"))
                .override().model(ltxCastFishingRod).predicate(FISHING_ROD_CAST, 1f).end();

        // Brush
        ResourceLocation brushBaseTex = itemFolderLocation("ltx_brush_base");
        ResourceLocation brushEmissiveTex = itemFolderLocation("ltx_brush_emissive");
        ModelFile brushing0 = emissiveBiLayer("ltx_brush_brushing_0", existingModel(itemFolderLocation(ModResources.MC, "brush_brushing_0")), brushBaseTex, brushEmissiveTex);
        ModelFile brushing1 = emissiveBiLayer("ltx_brush_brushing_1", existingModel(itemFolderLocation(ModResources.MC, "brush_brushing_1")), brushBaseTex, brushEmissiveTex);
        ModelFile brushing2 = emissiveBiLayer("ltx_brush_brushing_2", existingModel(itemFolderLocation(ModResources.MC, "brush_brushing_2")), brushBaseTex, brushEmissiveTex);
        emissiveBiLayer("ltx_brush", existingModel(itemFolderLocation(ModResources.MC, "brush")), brushBaseTex, brushEmissiveTex)
                .override().model(brushing0).predicate(BRUSH_BRUSHING, 0.25f).end()
                .override().model(brushing1).predicate(BRUSH_BRUSHING, 0.5f).end()
                .override().model(brushing2).predicate(BRUSH_BRUSHING, 0.75f).end();
    }

    private ItemModelBuilder emissiveBiLayer(String path, ModelFile parent, ResourceLocation baseTexture, ResourceLocation emissiveTexture)
    {
        return getBuilder(path)
                .parent(parent)
                .texture("layer0", baseTexture)
                .texture("layer1", emissiveTexture)
                .customLoader(BiLayerBuilder::new).end();
    }

    private void emissiveBiLayerModels(ModelFile parent, ItemLike... items)
    {
        Arrays.stream(items).forEach(i -> emissiveBiLayerModel(i, parent));
    }

    private void emissiveBiLayerModel(ItemLike itemLike, ModelFile parent)
    {
        String name = getItemName(itemLike.asItem());
        emissiveBiLayer(name, parent, itemFolderLocation(name + "_base"), itemFolderLocation(name + "_emissive"));
    }

    private void orePebbles(ItemLike... pebbles)
    {
        for (ItemLike item : pebbles)
        {
            String name = getItemName(item.asItem()).split("_")[0];
            generated(item, itemFolderLocation("ore_pebbles/" + name));
        }
    }

    private void generated(ResourceLocation texture, ItemLike... items)
    {
        Stream.of(items).forEach(i -> generated(i, texture));
    }

    private static class BiLayerBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T>
    {
        private BiLayerBuilder(T parent, ExistingFileHelper existingFileHelper)
        {
            super(EmissiveBiLayerGeometry.LOADER_ID, parent, existingFileHelper, false);
        }
    }
}