package liedge.limatech.data.generation;

import liedge.limacore.data.generation.LimaBlockStateProvider;
import liedge.limatech.LimaTech;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.function.BiFunction;

import static liedge.limacore.util.LimaRegistryUtil.getBlockName;
import static liedge.limatech.block.LimaTechBlockProperties.MACHINE_WORKING;
import static liedge.limatech.registry.game.LimaTechBlocks.*;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.DOUBLE_BLOCK_HALF;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

class BlockStatesGen extends LimaBlockStateProvider
{
    // Existing model references
    private final ModelFile machineParticlesOnly = existingModel(blockFolderLocation("machine_particles"));
    private final ModelFile turretBase = existingModel(blockFolderLocation("turret_base"));
    private final ModelFile basicMachineSEW = existingModel(blockFolderLocation("basic_machine_sew"));
    private final ModelFile basicMachineNSEW = existingModel(blockFolderLocation("basic_machine_nsew"));

    BlockStatesGen(PackOutput output, ExistingFileHelper helper)
    {
        super(output, LimaTech.RESOURCES, helper);
    }

    @Override
    protected void registerStatesAndModels()
    {
        final ModelFile glowingOre = models().getBuilder("glowing_ore")
                .parent(mcBlockBlock)
                .renderType("cutout")
                .element().from(0, 0, 0).to(16, 16, 16).allFaces((side, face) -> face.uvs(0, 0, 16, 16).texture("#0").cullface(side)).end()
                .element().from(0, 0, 0).to(16, 16, 16).allFaces((side, face) -> face.uvs(0, 0, 16, 16).texture("#1").cullface(side)).emissivity(9, 9).shade(false).ao(false).end();

        cubeAll(TITANIUM_ORE);
        cubeAll(DEEPSLATE_TITANIUM_ORE);
        simpleBlockWithItem(NIOBIUM_ORE, getBlockBuilder(NIOBIUM_ORE)
                .parent(glowingOre)
                .texture("particle", blockFolderLocation(Blocks.END_STONE))
                .texture("0", blockFolderLocation("niobium_ore_0"))
                .texture("1", blockFolderLocation("niobium_ore_1")));

        cubeAll(RAW_TITANIUM_BLOCK);
        cubeAll(RAW_NIOBIUM_BLOCK);
        cubeAll(TITANIUM_BLOCK, "titanium_block_1");
        cubeAll(NIOBIUM_BLOCK);
        cubeAll(SLATE_ALLOY_BLOCK);

        // Glow blocks
        final ModelFile glowBlockModel = models().withExistingParent("glow_block", "block/block").texture("particle", "#all").ao(false)
                .element().from(0, 0, 0).to(16, 16, 16).allFaces((side, face) -> face.uvs(0, 0, 16, 16).texture("#all").cullface(side)).emissivity(15, 15).shade(false).end();
        GLOW_BLOCKS.forEach((color, deferredBlock) -> simpleBlockWithItem(deferredBlock, getBlockBuilder(deferredBlock).parent(glowBlockModel).texture("all", blockFolderLocation("glow_blocks/" + color.getSerializedName()))));

        // Energy storage array
        final ModelFile esaModel = existingModel(blockFolderLocation("energy_storage_array"));
        simpleBlockWithItem(ENERGY_STORAGE_ARRAY, esaModel);
        simpleBlockWithItem(INFINITE_ENERGY_STORAGE_ARRAY, esaModel);

        // Simple machines
        cookingMachine(DIGITAL_FURNACE);
        cookingMachine(DIGITAL_SMOKER);
        cookingMachine(DIGITAL_BLAST_FURNACE);
        basicMachine(GRINDER, ($, builder) -> builder.parent(basicMachineSEW), ($, builder) -> builder.parent(basicMachineSEW));
        emissiveFrontMachine(RECOMPOSER);
        emissiveFrontMachine(MATERIAL_FUSING_CHAMBER);
        horizontalBlockWithSimpleItem(FABRICATOR);
        horizontalBlockWithSimpleItem(AUTO_FABRICATOR, blockFolderLocation(FABRICATOR));
        simpleBlockWithItem(EQUIPMENT_UPGRADE_STATION, existingModel(blockFolderLocation(EQUIPMENT_UPGRADE_STATION)));
        simpleDoubleMachineBlock(MOLECULAR_RECONSTRUCTOR);

        // Turret
        doubleMachineBlock(ROCKET_TURRET, turretBase);
        doubleMachineBlock(RAILGUN_TURRET, turretBase);
    }

    private void doubleMachineBlock(Holder<Block> doubleBlock, ModelFile baseModel)
    {
        VariantBlockStateBuilder builder = getVariantBuilder(doubleBlock);
        builder.setModels(builder.partialState().with(DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER), ConfiguredModel.builder().modelFile(machineParticlesOnly).build());
        for (Direction side : Direction.Plane.HORIZONTAL)
        {
            builder.setModels(builder.partialState().with(DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER).with(HORIZONTAL_FACING, side), ConfiguredModel.builder().modelFile(baseModel).rotationY(getRotationY(side, 180)).build());
        }
    }

    private void simpleDoubleMachineBlock(Holder<Block> doubleBlock)
    {
        ModelFile model = existingModel(blockFolderLocation(doubleBlock));
        doubleMachineBlock(doubleBlock, model);
        simpleBlockItem(doubleBlock, model);
    }

    private void horizontalBlockWithSimpleItem(Holder<Block> holder, ResourceLocation location)
    {
        ModelFile model = existingModel(location);
        horizontalBlock(holder.value(), model, 180);
        simpleBlockItem(holder, model);
    }

    private void horizontalBlockWithSimpleItem(Holder<Block> holder)
    {
        horizontalBlockWithSimpleItem(holder, blockFolderLocation(holder));
    }

    private void basicMachine(Holder<Block> holder, BiFunction<ResourceLocation, BlockModelBuilder, BlockModelBuilder> offModelFunc, BiFunction<ResourceLocation, BlockModelBuilder, BlockModelBuilder> onModelFunc)
    {
        String name = getBlockName(holder);
        ResourceLocation textureStart = blockFolderLocation(name);
        ModelFile offModel = offModelFunc.apply(textureStart, models().getBuilder(name + "_off").texture("front", textureStart.withSuffix("_off")));
        ModelFile onModel = onModelFunc.apply(textureStart, models().getBuilder(name + "_on").texture("front", textureStart.withSuffix("_on")));
        horizontalBlock(holder.value(), state -> state.getValue(MACHINE_WORKING) ? onModel : offModel);
        simpleBlockItem(holder, offModel);
    }

    private void emissiveFrontMachine(Holder<Block> holder)
    {
        basicMachine(holder, ($, builder) -> builder.parent(basicMachineSEW), (texture, builder) -> builder.parent(basicMachineNSEW).texture("front_emissive", texture.withSuffix("_on_emissive")));
    }

    private void cookingMachine(Holder<Block> holder)
    {
        ResourceLocation basicMachineMesh = blockFolderLocation("basic_machine_mesh");
        basicMachine(holder,
                (texture, builder) -> builder.parent(basicMachineSEW).texture("top", basicMachineMesh),
                (texture, builder) -> builder.parent(basicMachineNSEW).texture("front_emissive", texture.withSuffix("_on_emissive")).texture("top", basicMachineMesh));
    }
}