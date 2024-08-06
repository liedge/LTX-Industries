package liedge.limatech.util.datagen;

import liedge.limacore.data.generation.LimaBlockStateProvider;
import liedge.limatech.LimaTech;
import liedge.limatech.block.BasicHorizontalMachineBlock;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

import static liedge.limacore.util.LimaRegistryUtil.getBlockName;
import static liedge.limatech.block.LimaTechBlockProperties.MACHINE_WORKING;
import static liedge.limatech.registry.LimaTechBlocks.*;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.DOUBLE_BLOCK_HALF;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

class BlockStatesGen extends LimaBlockStateProvider
{
    // Existing model references
    private final ModelFile machineParticlesOnly = existingModel(blockFolderLocation("machine_particles"));
    private final ModelFile turretBase = existingModel(blockFolderLocation("turret_base"));
    private final ModelFile basicMachineModel = existingModel(blockFolderLocation("basic_machine"));

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
        cubeAll(TITANIUM_BLOCK);
        cubeAll(NIOBIUM_BLOCK);
        cubeAll(SLATE_ALLOY_BLOCK);

        // Glow blocks
        final ModelFile glowBlockModel = models().withExistingParent("glow_block", "block/block").texture("particle", "#all").ao(false)
                .element().from(0, 0, 0).to(16, 16, 16).allFaces((side, face) -> face.uvs(0, 0, 16, 16).texture("#all").cullface(side)).emissivity(15, 15).shade(false).end();
        GLOW_BLOCKS.forEach((color, deferredBlock) -> simpleBlockWithItem(deferredBlock, getBlockBuilder(deferredBlock).parent(glowBlockModel).texture("all", blockFolderLocation("glow_blocks/" + color.getSerializedName()))));

        // Energy storage array
        simpleBlockWithItem(ENERGY_STORAGE_ARRAY, existingModel(blockFolderLocation(ENERGY_STORAGE_ARRAY)));

        // Simple machines
        basicMachine(DIGITAL_FURNACE);
        basicMachine(GRINDER);
        basicMachine(RECOMPOSER);
        basicMachine(MATERIAL_FUSING_CHAMBER);
        horizontalBlockWithSimpleItem(FABRICATOR);
        simpleBlockWithItem(EQUIPMENT_MOD_TABLE, existingModel(blockFolderLocation(EQUIPMENT_MOD_TABLE)));

        // Turret
        getVariantBuilder(ROCKET_TURRET).forAllStatesExcept(state -> {
            ModelFile model = state.getValue(DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER ? turretBase : machineParticlesOnly;
            return ConfiguredModel.builder().modelFile(model).build();
        }, WATERLOGGED);
    }

    private void horizontalBlockWithSimpleItem(Holder<Block> holder)
    {
        ModelFile model = existingModel(blockFolderLocation(holder));
        horizontalBlock(holder.value(), model, 180);
        simpleBlockItem(holder, model);
    }

    private void basicMachine(DeferredHolder<Block, ? extends BasicHorizontalMachineBlock> holder)
    {
        String name = getBlockName(holder);
        ModelFile idleModel = models().getBuilder(name + "_idle").parent(basicMachineModel).texture("front", blockFolderLocation(name + "_idle"));
        ModelFile workingModel = models().getBuilder(name + "_working").parent(basicMachineModel).texture("front", blockFolderLocation(name + "_working"));
        horizontalBlock(holder.get(), state -> state.getValue(MACHINE_WORKING) ? workingModel : idleModel);
        simpleBlockItem(holder, idleModel);
    }
}