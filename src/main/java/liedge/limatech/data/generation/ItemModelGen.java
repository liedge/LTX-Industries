package liedge.limatech.data.generation;

import liedge.limacore.data.generation.LimaItemModelProvider;
import liedge.limacore.lib.ModResources;
import liedge.limatech.LimaTech;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.loaders.ItemLayerModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Arrays;
import java.util.stream.Stream;

import static liedge.limacore.util.LimaRegistryUtil.getItemName;
import static liedge.limatech.client.LimaTechItemOverrides.BRUSH_BRUSHING;
import static liedge.limatech.client.LimaTechItemOverrides.FISHING_ROD_CAST;
import static liedge.limatech.registry.game.LimaTechItems.*;

class ItemModelGen extends LimaItemModelProvider
{
    ItemModelGen(PackOutput output, ExistingFileHelper helper)
    {
        super(output, LimaTech.RESOURCES, helper);
    }

    @Override
    protected void registerModels()
    {
        generated(RAW_TITANIUM, TITANIUM_INGOT, TITANIUM_NUGGET, RAW_NIOBIUM, NIOBIUM_INGOT, NIOBIUM_NUGGET,
                WHITE_PIGMENT, LIGHT_BLUE_PIGMENT, LIME_PIGMENT);

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

        generated(DEEPSLATE_POWDER, SLATE_ALLOY_INGOT, BEDROCK_ALLOY_INGOT, SLATE_ALLOY_NUGGET, COPPER_CIRCUIT, GOLD_CIRCUIT, NIOBIUM_CIRCUIT, AUTO_AMMO_CANISTER, SPECIALIST_AMMO_CANISTER, EXPLOSIVES_AMMO_CANISTER, ROCKET_LAUNCHER_AMMO, MAGNUM_AMMO_CANISTER);
        generated(EMPTY_UPGRADE_MODULE, EQUIPMENT_UPGRADE_MODULE, MACHINE_UPGRADE_MODULE);

        // Patchouli guidebook model
        getBuilder("guidebook").parent(generatedModel).texture("layer0", itemFolderLocation("guidebook"));
    }

    private ItemModelBuilder emissiveBiLayer(String path, ModelFile parent, ResourceLocation baseTexture, ResourceLocation emissiveTexture)
    {
        return getBuilder(path)
                .parent(parent)
                .texture("layer0", baseTexture)
                .texture("layer1", emissiveTexture)
                .customLoader(ItemLayerModelBuilder::begin)
                .emissive(15, 15, 1)
                .renderType("neoforge:item_unlit", 1).end();
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
}