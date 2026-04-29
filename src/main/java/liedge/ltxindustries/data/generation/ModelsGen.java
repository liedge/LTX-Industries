package liedge.ltxindustries.data.generation;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.client.model.ExtendedCuboidBuilder;
import liedge.limacore.lib.ModResources;
import liedge.limacore.util.LimaRegistryUtil;
import liedge.ltxindustries.block.LTXIBlockProperties;
import liedge.ltxindustries.block.MachineState;
import liedge.ltxindustries.client.model.custom.EnergyDisplayModel;
import liedge.ltxindustries.client.model.item.GrenadeTypeTint;
import liedge.ltxindustries.client.model.item.WeaponModel;
import liedge.ltxindustries.client.renderer.item.RecoilAnimation;
import liedge.ltxindustries.client.renderer.item.WeaponSpecialRenderer;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.registry.game.LTXIBlocks;
import liedge.ltxindustries.registry.game.LTXIFluids;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.properties.conditional.FishingRodCast;
import net.minecraft.client.renderer.item.properties.numeric.UseCycle;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.model.generators.loaders.CompositeModelBuilder;
import net.neoforged.neoforge.client.model.generators.template.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;
import net.neoforged.neoforge.client.model.item.DynamicFluidContainerModel;
import org.apache.commons.lang3.function.Consumers;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static liedge.ltxindustries.LTXIndustries.RESOURCES;
import static liedge.ltxindustries.registry.game.LTXIItems.*;

class ModelsGen extends ModelProvider
{
    // Common resources
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
        registerFlatModels(models,
                RAW_TITANIUM,
                TITANIUM_INGOT,
                TITANIUM_NUGGET,
                RAW_NIOBIUM,
                NIOBIUM_INGOT,
                NIOBIUM_NUGGET,
                SPARK_FRUIT,
                VITRIOL_BERRIES,
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
                UPGRADE_MODULE,
                EMPTY_FABRICATION_BLUEPRINT,
                FABRICATION_BLUEPRINT,
                ITEMS_IO_CONFIG_CARD,
                ENERGY_IO_CONFIG_CARD,
                FLUIDS_IO_CONFIG_CARD,
                LIGHTWEIGHT_WEAPON_ENERGY,
                SPECIALIST_WEAPON_ENERGY,
                EXPLOSIVES_WEAPON_ENERGY,
                HEAVY_WEAPON_ENERGY);

        emissiveHandheldFlatItem(models, EPSILON_DRILL);
        emissiveHandheldFlatItem(models, EPSILON_SWORD);
        emissiveHandheldFlatItem(models, EPSILON_SHOVEL);
        emissiveHandheldFlatItem(models, EPSILON_AXE);
        emissiveHandheldFlatItem(models, EPSILON_HOE);
        emissiveHandheldFlatItem(models, EPSILON_WRENCH);
        emissiveFlatItem(models, EPSILON_SHEARS);
        emissiveBrush(models);
        emissiveFishingRod(models);
        emissiveFlatItem(models, EPSILON_LIGHTER);

        weapon(WAYFINDER)
                .addEnergyDisplay(EnergyDisplayModel.create(6.75f, 9f, 13f, 2.5f, 2.5f, 5f, Direction.Axis.Z))
                .setChamberOrigin(8f, 10.25f, 8.5f)
                .setRenderer(WeaponSpecialRenderer.Type.SIMPLE_RECOIL, RecoilAnimation.linear(0.15f), 0.5f, 2.5f)
                .build(models);
        EnergyDisplayModel autoFrameED = EnergyDisplayModel.create(6.75f, -1f, 4f, 2.5f, 7f, 3f, Direction.Axis.Y, EnergyDisplayModel.Rotation.axisAngle(6.75f, 9f, 4f, Direction.Axis.X, -15f));
        weapon(SERENITY)
                .addEnergyDisplay(autoFrameED)
                .setChamberOrigin(8f, 11f, 12.5f)
                .setRenderer(WeaponSpecialRenderer.Type.SIMPLE_RECOIL, RecoilAnimation.sineCurve(), 0.03125f, 0.4f)
                .build(models);
        weapon(MIRAGE)
                .addEnergyDisplay(autoFrameED)
                .setChamberOrigin(8f, 11f, 12.5f)
                .setRenderer(WeaponSpecialRenderer.Type.SIMPLE_RECOIL, RecoilAnimation.sineCurve(), 0.03125f, 0.4f)
                .build(models);
        weapon(AURORA)
                .addEnergyDisplay(EnergyDisplayModel.create(6.75f, 0f, 3f, 2.5f, 6f, 3f, Direction.Axis.Y, EnergyDisplayModel.Rotation.axisAngle(6.75f, 9f, 3f, Direction.Axis.X, -15f)))
                .setChamberOrigin(8f, 11f, 11f)
                .setRenderer(WeaponSpecialRenderer.Type.SIMPLE_RECOIL, RecoilAnimation.linear(0.15f), 0.5f, 2.5f)
                .build(models);
        weapon(HANABI)
                .addEnergyDisplay(EnergyDisplayModel.create(6.25f, 14.5f, 13f, 3.5f, 5f, 3.5f, Direction.Axis.Y, EnergyDisplayModel.Rotation.axisAngle(8, 13, 13, Direction.Axis.X, 45f)))
                .setEnergyTint(GrenadeTypeTint.INSTANCE)
                .setChamberOrigin(8f, 10.75f, 8.5f)
                .setRenderer(WeaponSpecialRenderer.Type.SIMPLE_RECOIL, RecoilAnimation.linear(0.15f), 0.5f, 2.5f)
                .build(models);
        weapon(STARGAZER)
                .addEnergyDisplay(EnergyDisplayModel.create(6.75f, 0f, 4f, 2.5f, 5f, 4f, Direction.Axis.Y, EnergyDisplayModel.Rotation.axisAngle(6.75f, 7f, 4f, Direction.Axis.X, -15f)))
                .setChamberOrigin(8f, 10f, 6.5f)
                .setRenderer(WeaponSpecialRenderer.Type.STARGAZER_SIGHT, RecoilAnimation.linear(0.15f), 0.4f, 3f)
                .build(models);
        weapon(DAYBREAK)
                .addEnergyDisplay(EnergyDisplayModel.create(6f, 17f, 11f, 4f, 5f, 4f, Direction.Axis.Y, EnergyDisplayModel.Rotation.axisAngle(8f, 14f, 11f, Direction.Axis.X, 45f)))
                .setChamberOrigin(8f, 12f, 5.5f)
                .setRenderer(WeaponSpecialRenderer.Type.SIMPLE_RECOIL, RecoilAnimation.linear(0.1f), 0.625f, 2f)
                .build(models);
        weapon(NOVA)
                .setChamberOrigin(8f, 10f, 9.5f)
                .setRenderer(WeaponSpecialRenderer.Type.SIMPLE_RECOIL, RecoilAnimation.linear(0.1f), 0.375f, 5f)
                .build(models);

        bucket(models, VIRIDIC_ACID_BUCKET, LTXIFluids.VIRIDIC_ACID);
        bucket(models, HYDROGEN_BUCKET, LTXIFluids.HYDROGEN);
        bucket(models, OXYGEN_BUCKET, LTXIFluids.OXYGEN);
    }

    private void registerBlockModels(BlockModelGenerators models)
    {
        models.createTrivialCube(LTXIBlocks.TITANIUM_ORE.get());
        models.createTrivialCube(LTXIBlocks.DEEPSLATE_TITANIUM_ORE.get());
        createEmissiveOre(models, Blocks.END_STONE, LTXIBlocks.NIOBIUM_ORE);
        models.createTrivialCube(LTXIBlocks.RAW_TITANIUM_BLOCK.get());
        models.createTrivialCube(LTXIBlocks.RAW_NIOBIUM_BLOCK.get());
        createOreCluster(models, LTXIBlocks.RAW_TITANIUM_CLUSTER, Blocks.SMOOTH_BASALT, LTXIBlocks.RAW_TITANIUM_BLOCK.get());
        createOreCluster(models, LTXIBlocks.RAW_NIOBIUM_CLUSTER, Blocks.END_STONE, LTXIBlocks.RAW_NIOBIUM_BLOCK.get());
        models.createTrivialCube(LTXIBlocks.TITANIUM_BLOCK.get());
        models.createTrivialCube(LTXIBlocks.NIOBIUM_BLOCK.get());
        models.createTrivialCube(LTXIBlocks.SLATESTEEL_BLOCK.get());
        createNeonLights(models);
        models.createTrivialCube(LTXIBlocks.TITANIUM_PANEL.get());
        models.createTrivialCube(LTXIBlocks.SMOOTH_TITANIUM_PANEL.get());
        models.createTrivialCube(LTXIBlocks.TILED_TITANIUM_PANEL.get());
        models.createTrivialCube(LTXIBlocks.TITANIUM_GLASS.get());
        models.createTrivialCube(LTXIBlocks.GLACIA_GLASS.get());
        models.createTrivialCube(LTXIBlocks.SLATESTEEL_PANEL.get());
        models.createTrivialCube(LTXIBlocks.SMOOTH_SLATESTEEL_PANEL.get());
        models.createTrivialCube(LTXIBlocks.TILED_SLATESTEEL_PANEL.get());

        createSparkFruit(models);
        createBerryVines(models, LTXIBlocks.BILEVINE);
        createBerryVines(models, LTXIBlocks.BILEVINE_PLANT);
        createGloomShroom(models);

        createIdentityMachine(models, LTXIBlocks.UPGRADE_STATION);
        createIdentityMachine(models, LTXIBlocks.ENERGY_CELL_ARRAY);
        createIdentityMachine(models, LTXIBlocks.INFINITE_ENERGY_CELL_ARRAY);

        createBasicBinaryMachine(models, LTXIBlocks.DIGITAL_FURNACE, Templates.BASIC_MACHINE_IDLE, Templates.BASIC_MACHINE_ACTIVE);
        createBasicBinaryMachine(models, LTXIBlocks.DIGITAL_SMOKER, Templates.BASIC_MACHINE_IDLE, Templates.BASIC_MACHINE_ACTIVE);
        createBasicBinaryMachine(models, LTXIBlocks.DIGITAL_BLAST_FURNACE, Templates.BASIC_MACHINE_IDLE, Templates.BASIC_MACHINE_ACTIVE);
        createCompositeBinaryMachine(models, LTXIBlocks.GRINDER,
                List.of(Parts.EMISSIVE_ACTIVE, "front_crusher", "rear_crusher"), List.of("front_crusher", "rear_crusher"), List.of());
        createBasicBinaryMachine(models, LTXIBlocks.MATERIAL_FUSING_CHAMBER, Templates.BASIC_MACHINE_IDLE, Templates.BASIC_MACHINE_ACTIVE);
        createCompositeBinaryMachine(models, LTXIBlocks.ELECTROCENTRIFUGE,
                List.of(Parts.EMISSIVE_ACTIVE, "tubes", "tubes_emissive"), List.of("tubes", "tubes_emissive"), List.of());
        createCompositeBinaryMachine(models, LTXIBlocks.MIXER, List.of(Parts.EMISSIVE_ACTIVE, "blades"), List.of("blades"), List.of());
        createCompositeBinaryMachine(models, LTXIBlocks.VOLTAIC_INJECTOR, List.of(Parts.EMISSIVE_ACTIVE), List.of(), List.of());
        createCompositeBinaryMachine(models, LTXIBlocks.CHEM_LAB, List.of(Parts.EMISSIVE_ACTIVE, "fluid"), List.of(), List.of("fluid"));
        createBasicBinaryMachine(models, LTXIBlocks.ASSEMBLER, Templates.ASSEMBLER, null);
        createCompositeBinaryMachine(models, LTXIBlocks.GEO_SYNTHESIZER,
                List.of(Parts.EMISSIVE_ACTIVE),
                List.of(),
                List.of(), id -> ItemModelUtils.tintedModel(id, ItemModelUtils.constantTint(0x3f76e4)));
        createIdentityMachine(models, LTXIBlocks.FABRICATOR);
        createIdentityMachine(models, LTXIBlocks.AUTO_FABRICATOR);
        createIdentityMachine(models, LTXIBlocks.DIGITAL_GARDEN, id -> ItemModelUtils.tintedModel(id, ItemModelUtils.constantTint(0x3f76e4)));

        createTurretBlock(models, LTXIBlocks.ARC_TURRET);
        createTurretBlock(models, LTXIBlocks.ROCKET_TURRET);
        createTurretBlock(models, LTXIBlocks.RAILGUN_TURRET);

        models.createAirLikeBlock(LTXIBlocks.VIRIDIC_ACID_BLOCK.get(), new Material(resources.id("block/viridic_acid_still")));
        createGlowstick(models);
        models.blockStateOutput.accept(MultiVariantGenerator.dispatch(LTXIBlocks.MESH_BLOCK.get(), BlockModelGenerators.plainVariant(Templates.MACHINE_BLOCK.model.orElseThrow())));

        // Block entity parts
        createBEPart(models, LTXIBlocks.GRINDER.get(), "_front_crusher", false, Parts.FRAME, Parts.EMISSIVE_IDLE, Parts.EMISSIVE_ACTIVE, "rear_crusher");
        createBEPart(models, LTXIBlocks.GRINDER.get(), "_rear_crusher", false, Parts.FRAME, Parts.EMISSIVE_IDLE, Parts.EMISSIVE_ACTIVE, "front_crusher");
        createBEPart(models, LTXIBlocks.ELECTROCENTRIFUGE.get(), "_tubes", true, Parts.FRAME, Parts.EMISSIVE_IDLE, Parts.EMISSIVE_ACTIVE);
        createBEPart(models, LTXIBlocks.MIXER.get(), "_blades", false, Parts.FRAME, Parts.EMISSIVE_IDLE, Parts.EMISSIVE_ACTIVE);
    }

    private void createEmissiveOre(BlockModelGenerators models, Block particleBlock, Holder<Block> holder)
    {
        Block block = holder.value();
        TextureMapping textures = new TextureMapping()
                .put(Textures.BASE, TextureMapping.getBlockTexture(block, "_base"))
                .put(Textures.EMISSIVE, TextureMapping.getBlockTexture(block, "_emissive"))
                .put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(particleBlock));
        models.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, BlockModelGenerators.plainVariant(
                Templates.EMISSIVE_ORE.create(block, textures, models.modelOutput))));
    }

    private void createOreCluster(BlockModelGenerators models, Holder<Block> holder, Block baseBlock, Block rawOreBlock)
    {
        Block block = holder.value();
        TextureMapping textures = new TextureMapping()
                .put(Textures.BASE, TextureMapping.getBlockTexture(baseBlock))
                .put(Textures.ORE, TextureMapping.getBlockTexture(rawOreBlock));
        models.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, BlockModelGenerators.plainVariant(
                Templates.ORE_CLUSTER.create(block, textures, models.modelOutput))).with(BlockModelGenerators.ROTATIONS_COLUMN_WITH_FACING));
    }

    private void createNeonLights(BlockModelGenerators models)
    {
        ModelTemplate template = new ModelTemplate(Optional.of(resources.id("block/neon_light")), Optional.empty(), TextureSlot.ALL);

        for (Holder<Block> holder : LTXIBlocks.NEON_LIGHTS.values())
        {
            Block block = holder.value();
            Identifier model = template.create(block, TextureMapping.cube(block), models.modelOutput);
            models.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, BlockModelGenerators.plainVariant(model)));
            models.registerSimpleItemModel(block, model);
        }
    }

    private void createGlowstick(BlockModelGenerators models)
    {
        Block block = LTXIBlocks.GLOWSTICK.get();
        MultiVariant variant = BlockModelGenerators.plainVariant(resources.id("block/glowstick"));
        models.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, variant).with(BlockModelGenerators.ROTATIONS_COLUMN_WITH_FACING));
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems()
    {
        return super.getKnownItems().filter(o -> !(o.value() instanceof WeaponItem));
    }

    // Helpers
    private void registerFlatModels(ItemModelGenerators models, ItemLike... items)
    {
        for (ItemLike item : items)
        {
            models.generateFlatItem(item.asItem(), ModelTemplates.FLAT_ITEM);
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
        emissiveFlatItem(models, holder, ModelTemplates.FLAT_ITEM, Templates.UNLIT_FLAT_ITEM);
    }

    private void emissiveHandheldFlatItem(ItemModelGenerators models, ItemLike holder)
    {
        emissiveFlatItem(models, holder, ModelTemplates.FLAT_HANDHELD_ITEM, Templates.UNLIT_HANDHELD_FLAT_ITEM);
    }

    private void emissiveFishingRod(ItemModelGenerators models)
    {
        Item item = EPSILON_FISHING_ROD.asItem();
        Identifier id = LimaRegistryUtil.getItemId(item);

        models.generateBooleanDispatch(item, new FishingRodCast(),
                emissiveFlatModel(id.withSuffix("_cast"), ModelTemplates.FLAT_HANDHELD_ROD_ITEM, null, Templates.UNLIT_HANDHELD_ROD_ITEM, null, models.modelOutput),
                emissiveFlatModel(id, ModelTemplates.FLAT_HANDHELD_ROD_ITEM, null, Templates.UNLIT_HANDHELD_ROD_ITEM, null, models.modelOutput));
    }

    private void emissiveBrush(ItemModelGenerators models)
    {
        Item item = EPSILON_BRUSH.asItem();
        Identifier id = LimaRegistryUtil.getItemId(item);

        Identifier baseTexture = id.withPath(s -> "item/" + s + "_base");
        Identifier emissiveTexture = id.withPath(s -> "item/" + s + "_emissive");

        ItemModel.Unbaked[] brushModels = new ItemModel.Unbaked[4];

        for (int i = 0; i < 4; i++)
        {
            String suffix = i > 0 ? "_brushing_" + (i - 1) : "";
            Identifier mcId = ModResources.MC.id("item/brush" + suffix);

            ModelTemplate baseTemplate = new ModelTemplate(Optional.of(mcId), Optional.empty(), TextureSlot.LAYER0);
            ModelTemplate emissiveTemplate = ExtendedModelTemplateBuilder.of(baseTemplate).customLoader(ExtendedCuboidBuilder::new, ExtendedCuboidBuilder::forceEmissiveQuads).build();

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
                Optional.of(new Material(ModResources.MC.id("item/bucket"))),
                Optional.of(new Material(ModResources.NEOFORGE.id("item/mask/bucket_fluid"))),
                Optional.empty());

        DynamicFluidContainerModel.Unbaked unbaked = new DynamicFluidContainerModel.Unbaked(
                textures,
                fluidHolder.value(),
                true,
                false,
                true);

        models.itemModelOutput.accept(item.asItem(), unbaked);
    }

    private WeaponBuilder weapon(Holder<Item> holder)
    {
        Item item = holder.value();
        Identifier id = LimaRegistryUtil.getItemId(item);
        Identifier template = id.withPrefix("item/template/");
        Identifier frame = id.withPath(s -> "item/" + s + "_frame");
        Identifier chamber = id.withPath(s -> "item/" + s + "_chamber");

        return new WeaponBuilder(item, template, frame, chamber);
    }

    private void createSparkFruit(BlockModelGenerators models)
    {
        Block block = LTXIBlocks.SPARK_FRUIT.get();
        MultiVariantGenerator blockState = MultiVariantGenerator.dispatch(block).with(PropertyDispatch.initial(BlockStateProperties.AGE_2).generate(age -> {
            Identifier model = ModelLocationUtils.getModelLocation(block, "_" + age);
            return BlockModelGenerators.plainVariant(model);
        }));
        models.blockStateOutput.accept(blockState);
    }

    private void createBerryVines(BlockModelGenerators models, Holder<Block> holder)
    {
        Block block = holder.value();

        MultiVariant emptyVines = BlockModelGenerators.plainVariant(models.createSuffixedVariant(block, "", ModelTemplates.CROSS, TextureMapping::cross));
        MultiVariant berryVines = BlockModelGenerators.plainVariant(models.createSuffixedVariant(block, "_lit", ModelTemplates.CROSS, TextureMapping::cross));

        MultiVariantGenerator blockState = MultiVariantGenerator.dispatch(block)
                .with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.BERRIES, berryVines, emptyVines));
        models.blockStateOutput.accept(blockState);
    }

    private void createGloomShroom(BlockModelGenerators models)
    {
        Block block = LTXIBlocks.GLOOM_SHROOM.get();
        MultiVariant variant = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(block));
        models.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, variant));

        Identifier itemModel = ModelTemplates.FLAT_ITEM.create(block.asItem(), TextureMapping.layer0(block), models.modelOutput);
        models.itemModelOutput.accept(block.asItem(), ItemModelUtils.plainModel(itemModel));
    }

    private void createBasicBinaryMachine(BlockModelGenerators models, Holder<Block> holder, ModelTemplate idleTemplate, @Nullable ModelTemplate activeTemplate)
    {
        Block block = holder.value();

        Identifier idleModel = idleTemplate.createWithSuffix(block, idleTemplate.suffix.orElse("_idle"), Textures.idleFront(block), models.modelOutput);
        if (activeTemplate == null) activeTemplate = idleTemplate;
        Identifier activeModel = activeTemplate.createWithSuffix(block, activeTemplate.suffix.orElse("_active"), Textures.activeFront(block), models.modelOutput);

        models.blockStateOutput.accept(binaryMachineState(block, idleModel, activeModel));
        createEmissiveParentItem(models, block, idleModel);
    }

    private void createCompositeBinaryMachine(BlockModelGenerators models, Holder<Block> holder, List<String> idleExclusions, List<String> activeExclusions, List<String> itemExclusions)
    {
        createCompositeBinaryMachine(models, holder, idleExclusions, activeExclusions, itemExclusions, ItemModelUtils::plainModel);
    }

    private void createCompositeBinaryMachine(BlockModelGenerators models, Holder<Block> holder, List<String> idleExclusions, List<String> activeExclusions, List<String> itemExclusions,
                                              Function<Identifier, ItemModel.Unbaked> itemMapper)
    {
        Block block = holder.value();
        Identifier parent = ModelLocationUtils.getModelLocation(block);

        Identifier idleModel = ExclusionTemplate.of(parent).exclude(idleExclusions).create(block, "_idle", models.modelOutput);
        Identifier activeModel = ExclusionTemplate.of(parent).exclude(activeExclusions).create(block, "_active", models.modelOutput);
        models.blockStateOutput.accept(binaryMachineState(block, idleModel, activeModel));

        Identifier itemModel = ExclusionTemplate.of(parent).exclude(itemExclusions).emissive().create(block.asItem(), models.modelOutput);
        models.itemModelOutput.accept(block.asItem(), itemMapper.apply(itemModel));
    }

    private void createTurretBlock(BlockModelGenerators models, Holder<Block> holder)
    {
        Block block = holder.value();
        Identifier baseModel = RESOURCES.id("block/turret_base");

        models.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, BlockModelGenerators.plainVariant(baseModel)).with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING));

        Identifier blockModel = ModelLocationUtils.getModelLocation(block);
        Identifier itemModel = Templates.TURRET.extend().customLoader(CompositeModelBuilder::new, o -> o
                .inlineChild("base", unlitParent(baseModel), new TextureMapping())
                .inlineChild("weapons", unlitParent(blockModel), new TextureMapping()))
                .build()
                .create(block.asItem(), new TextureMapping(), models.modelOutput);
        models.itemModelOutput.accept(block.asItem(), ItemModelUtils.plainModel(itemModel));

        createBEPart(models, block, "_swivel", true, "weapons_base", "weapons_emissive");
        createBEPart(models, block, "_weapons", true, "swivel_base", "swivel_emissive");
    }

    private MultiVariantGenerator binaryMachineState(Block block, Identifier idleModel, Identifier activeModel)
    {
        return MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(LTXIBlockProperties.BINARY_MACHINE_STATE)
                        .select(MachineState.IDLE, BlockModelGenerators.plainVariant(idleModel))
                        .select(MachineState.ACTIVE, BlockModelGenerators.plainVariant(activeModel)))
                .with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING);
    }

    private void createEmissiveParentItem(BlockModelGenerators models, Block block, Identifier blockModel)
    {
        Identifier model = unlitParent(blockModel).create(block.asItem(), new TextureMapping(), models.modelOutput);
        models.itemModelOutput.accept(block.asItem(), ItemModelUtils.plainModel(model));
    }

    private void createIdentityMachine(BlockModelGenerators models, Holder<Block> holder, Function<Identifier, ItemModel.Unbaked> itemModelMapper)
    {
        Block block = holder.value();

        Identifier blockModel = ModelLocationUtils.getModelLocation(block);
        models.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, BlockModelGenerators.plainVariant(blockModel))
                .with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING));

        Identifier itemModel = unlitParent(blockModel).create(block.asItem(), new TextureMapping(), models.modelOutput);
        models.itemModelOutput.accept(block.asItem(), itemModelMapper.apply(itemModel));
    }

    private void createIdentityMachine(BlockModelGenerators models, Holder<Block> holder)
    {
        createIdentityMachine(models, holder, ItemModelUtils::plainModel);
    }

    private ModelTemplate unlitParent(Identifier parent)
    {
        return ExtendedModelTemplateBuilder.builder().parent(parent).customLoader(ExtendedCuboidBuilder::new, Consumers.nop()).build();
    }

    private void createBEPart(BlockModelGenerators models, Block block, String suffix, boolean emissive, String... parts)
    {
        ExclusionTemplate template = ExclusionTemplate.of(block).exclude(parts).emissive(emissive);
        Identifier path = LimaRegistryUtil.getBlockId(block).withPath(s -> "block_entity/" + s + suffix);
        template.create(path, new TextureMapping(), models.modelOutput);
    }

    private static class ExclusionTemplate extends ModelTemplate
    {
        static ExclusionTemplate of(Identifier parent)
        {
            return new ExclusionTemplate(parent);
        }

        static ExclusionTemplate of(Block block)
        {
            return new ExclusionTemplate(ModelLocationUtils.getModelLocation(block));
        }

        private final List<String> hiddenParts = new ObjectArrayList<>();
        private @Nullable CustomLoaderBuilder custom;

        ExclusionTemplate(Identifier parent)
        {
            super(Optional.of(parent), Optional.empty());
        }

        ExclusionTemplate exclude(List<String> parts)
        {
            hiddenParts.addAll(parts);
            return this;
        }

        ExclusionTemplate exclude(String... parts)
        {
            return exclude(Arrays.asList(parts));
        }

        ExclusionTemplate emissive(boolean emissive)
        {
            this.custom = emissive ? new ExtendedCuboidBuilder() : null;
            return this;
        }

        ExclusionTemplate emissive()
        {
            return emissive(true);
        }

        Identifier create(Block block, String suffix, BiConsumer<Identifier, ModelInstance> output)
        {
            return createWithSuffix(block, suffix, new TextureMapping(), output);
        }

        Identifier create(Item item, BiConsumer<Identifier, ModelInstance> output)
        {
            return create(item, new TextureMapping(), output);
        }

        @Override
        public JsonObject createBaseTemplate(Identifier target, Map<TextureSlot, Material> slots)
        {
            JsonObject json = super.createBaseTemplate(target, slots);

            if (custom != null) json = custom.toJson(json);

            if (!hiddenParts.isEmpty())
            {
                JsonObject visibility = new JsonObject();
                hiddenParts.forEach(part -> visibility.addProperty(part, false));
                json.add("visibility", visibility);
            }

            return json;
        }
    }

    private static class WeaponBuilder
    {
        private final Item item;
        private final Identifier template;
        private final Identifier frame;
        private final Identifier chamber;

        private final List<EnergyDisplayModel> energyDisplays = new ObjectArrayList<>();
        private @Nullable ItemTintSource energyTint;
        private @Nullable Vector3fc chamberOrigin;
        private WeaponSpecialRenderer.@Nullable Unbaked specialModel;

        private WeaponBuilder(Item item, Identifier template, Identifier frame, Identifier chamber)
        {
            this.item = item;
            this.template = template;
            this.frame = frame;
            this.chamber = chamber;
        }

        WeaponBuilder addEnergyDisplay(EnergyDisplayModel display)
        {
            energyDisplays.add(display);
            return this;
        }

        WeaponBuilder setEnergyTint(ItemTintSource energyTint)
        {
            this.energyTint = energyTint;
            return this;
        }

        WeaponBuilder setChamberOrigin(float x, float y, float z)
        {
            chamberOrigin = new Vector3f(x, y, z).mul(0.0625f);
            return this;
        }

        WeaponBuilder setRenderer(WeaponSpecialRenderer.Type type, RecoilAnimation recoilAnimation, float recoilDistance, float recoilAngle)
        {
            this.specialModel = new WeaponSpecialRenderer.Unbaked(frame, chamber, Objects.requireNonNull(chamberOrigin), recoilAnimation, recoilDistance, recoilAngle, type);
            return this;
        }

        void build(ItemModelGenerators models)
        {
            models.itemModelOutput.accept(item, new WeaponModel.Unbaked(template, energyDisplays, Optional.ofNullable(energyTint), Objects.requireNonNull(specialModel)));
        }
    }

    private static class Templates
    {
        private static final ModelTemplate UNLIT_FLAT_ITEM = unlitFlat(ModelTemplates.FLAT_ITEM);
        private static final ModelTemplate UNLIT_HANDHELD_FLAT_ITEM = unlitFlat(ModelTemplates.FLAT_HANDHELD_ITEM);
        private static final ModelTemplate UNLIT_HANDHELD_ROD_ITEM = unlitFlat(ModelTemplates.FLAT_HANDHELD_ROD_ITEM);

        private static final ModelTemplate ORE_CLUSTER = builder("block/raw_ore_cluster").requiredTextureSlot(Textures.BASE).requiredTextureSlot(Textures.ORE).build();
        private static final ModelTemplate EMISSIVE_ORE = builder("block/emissive_ore").requiredTextureSlot(Textures.BASE).requiredTextureSlot(Textures.EMISSIVE)
                .requiredTextureSlot(TextureSlot.PARTICLE).build();

        private static final ModelTemplate MACHINE_BLOCK = builder("block/template/machine").build();
        private static final ModelTemplate BASIC_MACHINE_IDLE = builder("block/basic_machine_idle").requiredTextureSlot(TextureSlot.FRONT).build();
        private static final ModelTemplate BASIC_MACHINE_ACTIVE = builder("block/basic_machine_active").requiredTextureSlot(TextureSlot.FRONT)
                .requiredTextureSlot(Textures.FRONT_EMISSIVE).build();
        private static final ModelTemplate ASSEMBLER = builder("block/assembler").requiredTextureSlot(TextureSlot.FRONT).build();
        private static final ModelTemplate TURRET = builder("block/template/turret").build();

        private static ModelTemplate unlitFlat(ModelTemplate original)
        {
            return original.extend().customLoader(ExtendedCuboidBuilder::new, ExtendedCuboidBuilder::forceEmissiveQuads).build();
        }

        private static ExtendedModelTemplateBuilder builder(String path)
        {
            return ExtendedModelTemplateBuilder.builder().parent(RESOURCES.id(path));
        }
    }

    private static class Textures
    {
        private static final TextureSlot BASE = TextureSlot.create("base");
        private static final TextureSlot ORE = TextureSlot.create("ore");
        private static final TextureSlot EMISSIVE = TextureSlot.create("emissive");
        private static final TextureSlot FRONT_EMISSIVE = TextureSlot.create("front_emissive");

        private static TextureMapping idleFront(Block block)
        {
            return new TextureMapping().put(TextureSlot.FRONT, TextureMapping.getBlockTexture(block, "_idle"));
        }

        private static TextureMapping activeFront(Block block)
        {
            return new TextureMapping()
                    .put(TextureSlot.FRONT, TextureMapping.getBlockTexture(block, "_active"))
                    .put(FRONT_EMISSIVE, TextureMapping.getBlockTexture(block, "_active_emissive"));
        }
    }

    private static class Parts
    {
        private static final String FRAME = "frame";
        private static final String EMISSIVE_IDLE = "emissive_idle";
        private static final String EMISSIVE_ACTIVE = "emissive_active";
    }
}