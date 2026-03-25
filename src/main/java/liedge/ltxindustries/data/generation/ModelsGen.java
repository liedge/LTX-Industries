package liedge.ltxindustries.data.generation;

import liedge.limacore.lib.ModResources;
import liedge.limacore.util.LimaRegistryUtil;
import liedge.ltxindustries.registry.game.LTXIFluids;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.properties.conditional.FishingRodCast;
import net.minecraft.client.renderer.item.properties.numeric.UseCycle;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;
import net.neoforged.neoforge.client.model.item.DynamicFluidContainerModel;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static liedge.ltxindustries.registry.game.LTXIItems.*;

class ModelsGen extends ModelProvider
{
    private static final ModelTemplate UNLIT_FLAT_ITEM = ExtendedModelTemplateBuilder.of(ModelTemplates.FLAT_ITEM).build();
    private static final ModelTemplate UNLIT_HANDHELD_FLAT_ITEM = ExtendedModelTemplateBuilder.of(ModelTemplates.FLAT_HANDHELD_ITEM).build();
    private static final ModelTemplate UNLIT_HANDHELD_ROD_ITEM = ExtendedModelTemplateBuilder.of(ModelTemplates.FLAT_HANDHELD_ROD_ITEM).build();

    private final ModResources resources;

    ModelsGen(PackOutput output, ModResources resources)
    {
        super(output, resources.modid());
        this.resources = resources;
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels)
    {
        registerBlockModels(blockModels);
        registerItemModels(itemModels);
    }

    private void registerItemModels(ItemModelGenerators models)
    {
        registerFlatModels(models, ModelTemplates.FLAT_ITEM,
                RAW_TITANIUM,
                TITANIUM_INGOT,
                TITANIUM_NUGGET,
                RAW_NIOBIUM,
                NIOBIUM_INGOT,
                NIOBIUM_NUGGET,
                SPARK_FRUIT,
                VITRIOL_BERRIES,
                GLOOM_SHROOM,
                LTX_LIME_PIGMENT,
                ENERGY_BLUE_PIGMENT,
                ELECTRIC_CHARTREUSE_PIGMENT,
                VIRIDIC_GREEN_PIGMENT,
                NEURO_BLUE_PIGMENT,
                COAL_ORE_PEBBLES,
                COPPER_ORE_PEBBLES,
                IRON_ORE_PEBBLES,
                LAPIS_ORE_PEBBLES,
                REDSTONE_ORE_PEBBLES,
                GOLD_ORE_PEBBLES,
                DIAMOND_ORE_PEBBLES,
                EMERALD_ORE_PEBBLES,
                QUARTZ_ORE_PEBBLES,
                NETHERITE_ORE_PEBBLES,
                TITANIUM_ORE_PEBBLES,
                NIOBIUM_ORE_PEBBLES,
                TIN_ORE_PEBBLES,
                OSMIUM_ORE_PEBBLES,
                NICKEL_ORE_PEBBLES,
                LEAD_ORE_PEBBLES,
                SILVER_ORE_PEBBLES,
                URANIUM_ORE_PEBBLES,
                TITANIUM_GEAR,
                SLATESTEEL_GEAR,
                CIRCUIT_BOARD,
                T1_CIRCUIT,
                T2_CIRCUIT,
                T3_CIRCUIT,
                T4_CIRCUIT,
                T5_CIRCUIT,
                OPTICAL_TECH_PART,
                IMPULSE_TECH_PART,
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

        emissiveHandheldFlatItem(models, LTX_DRILL);
        emissiveHandheldFlatItem(models, LTX_SWORD);
        emissiveHandheldFlatItem(models, LTX_SHOVEL);
        emissiveHandheldFlatItem(models, LTX_AXE);
        emissiveHandheldFlatItem(models, LTX_HOE);
        emissiveHandheldFlatItem(models, LTX_WRENCH);
        emissiveFlatItem(models, LTX_SHEARS);
        emissiveBrush(models);
        emissiveFishingRod(models);
        emissiveFlatItem(models, LTX_LIGHTER);

        bucket(models, VIRIDIC_ACID_BUCKET, LTXIFluids.VIRIDIC_ACID);
        bucket(models, HYDROGEN_BUCKET, LTXIFluids.HYDROGEN);
        bucket(models, OXYGEN_BUCKET, LTXIFluids.OXYGEN);
    }

    private void registerBlockModels(BlockModelGenerators models)
    {
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks()
    {
        return Stream.empty();
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems()
    {
        return Stream.empty();
    }

    // Helpers
    private void registerFlatModels(ItemModelGenerators models, ModelTemplate template, ItemLike... items)
    {
        for (ItemLike item : items)
        {
            models.generateFlatItem(item.asItem(), template);
        }
    }

    private ItemModel.Unbaked emissiveFlatModel(Identifier path,
                                                ModelTemplate baseTemplate, @Nullable Identifier baseTexture,
                                                ModelTemplate emissiveTemplate, @Nullable Identifier emissiveTexture,
                                                BiConsumer<Identifier, ModelInstance> output)
    {
        Identifier basePath = path.withPath(s -> "item/" + s + "_base");
        Identifier emissivePath = path.withPath(s -> "item/" + s + "_emissive");

        if (baseTexture == null) baseTexture = basePath;
        if (emissiveTexture == null) emissiveTexture = emissivePath;

        ItemModel.Unbaked baseModel = ItemModelUtils.plainModel(baseTemplate.create(basePath, TextureMapping.layer0(new Material(baseTexture)), output));
        ItemModel.Unbaked emissiveModel = ItemModelUtils.plainModel(emissiveTemplate.create(emissivePath, TextureMapping.layer0(new Material(emissiveTexture)), output));

        return ItemModelUtils.composite(baseModel, emissiveModel);
    }

    private void emissiveFlatItem(ItemModelGenerators models, ItemLike holder, ModelTemplate baseTemplate, ModelTemplate emissiveTemplate)
    {
        Item item = holder.asItem();
        Identifier id = LimaRegistryUtil.getItemId(item);

        models.itemModelOutput.accept(item, emissiveFlatModel(id, baseTemplate, null, emissiveTemplate, null, models.modelOutput));
    }

    private void emissiveFlatItem(ItemModelGenerators models, ItemLike holder)
    {
        emissiveFlatItem(models, holder, ModelTemplates.FLAT_ITEM, UNLIT_FLAT_ITEM);
    }

    private void emissiveHandheldFlatItem(ItemModelGenerators models, ItemLike holder)
    {
        emissiveFlatItem(models, holder, ModelTemplates.FLAT_HANDHELD_ITEM, UNLIT_HANDHELD_FLAT_ITEM);
    }

    private void emissiveFishingRod(ItemModelGenerators models)
    {
        Item item = LTX_FISHING_ROD.asItem();
        Identifier id = LimaRegistryUtil.getItemId(item);

        models.generateBooleanDispatch(item, new FishingRodCast(),
                emissiveFlatModel(id, ModelTemplates.FLAT_HANDHELD_ROD_ITEM, null, UNLIT_HANDHELD_ROD_ITEM, null, models.modelOutput),
                emissiveFlatModel(id.withSuffix("_cast"), ModelTemplates.FLAT_HANDHELD_ROD_ITEM, null, UNLIT_HANDHELD_ROD_ITEM, null, models.modelOutput));
    }

    private void emissiveBrush(ItemModelGenerators models)
    {
        Item item = LTX_BRUSH.asItem();
        Identifier id = LimaRegistryUtil.getItemId(item);

        Identifier baseTexture = id.withPath(s -> "item/" + s + "_base");
        Identifier emissiveTexture = id.withPath(s -> "item/" + s + "_emissive");

        ItemModel.Unbaked[] brushModels = new ItemModel.Unbaked[4];

        for (int i = 0; i < 4; i++)
        {
            String suffix = i > 0 ? "_brushing" + (i - 1) : "";
            Identifier mcId = ModResources.MC.id("item/brush" + suffix);

            ModelTemplate baseTemplate = new ModelTemplate(Optional.of(mcId), Optional.empty(), TextureSlot.LAYER0);
            ModelTemplate emissiveTemplate = ExtendedModelTemplateBuilder.of(baseTemplate).build();

            Identifier subId = id.withPath(s -> "item/" + s + suffix);
            brushModels[i] = emissiveFlatModel(subId, baseTemplate, baseTexture, emissiveTemplate, emissiveTexture, models.modelOutput);
        }

        models.itemModelOutput.accept(item, ItemModelUtils.rangeSelect(new UseCycle(10f), 0.1f,
                brushModels[0],
                ItemModelUtils.override(brushModels[1], 0.25f),
                ItemModelUtils.override(brushModels[2], 0.5f),
                ItemModelUtils.override(brushModels[3], 0.75f)));
    }

    private void bucket(ItemModelGenerators models, ItemLike item, Holder<Fluid> fluidHolder)
    {
        DynamicFluidContainerModel.Textures textures = new DynamicFluidContainerModel.Textures(
                Optional.empty(),
                Optional.of(new Material(ModResources.NEOFORGE.id("item/bucket"))),
                Optional.of(new Material(LimaRegistryUtil.getNonNullRegistryId(fluidHolder))),
                Optional.empty());

        DynamicFluidContainerModel.Unbaked unbaked = new DynamicFluidContainerModel.Unbaked(
                textures,
                fluidHolder.value(),
                true,
                false,
                true);

        models.itemModelOutput.accept(item.asItem(), unbaked);
    }
}