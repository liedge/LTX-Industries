package liedge.limatech.util.datagen;

import liedge.limacore.data.generation.LimaBlockStateProvider;
import liedge.limacore.lib.ModResources;
import liedge.limatech.LimaTech;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import static liedge.limatech.registry.LimaTechBlocks.*;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;

class BlockStatesGen extends LimaBlockStateProvider
{
    // Existing model references
    private final ModelFile machineParticlesOnly = existingModel(blockFolderLocation("machine_particles"));
    private final ModelFile turretBase = existingModel(blockFolderLocation("turret_base"));
    private final ModelFile rawOreCluster = existingModel(blockFolderLocation("raw_ore_cluster"));

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

        // Fabricator block
        final ModelFile fabricatorModel = existingModel(blockFolderLocation(FABRICATOR));
        getVariantBuilder(FABRICATOR).forAllStates(state -> {
            Direction facing = state.getValue(HORIZONTAL_FACING);
            return ConfiguredModel.builder().modelFile(fabricatorModel).rotationY(getRotationY(facing, 180)).build();
        });
        simpleBlockItem(FABRICATOR, fabricatorModel);

        // Turret
        getVariantBuilder(ROCKET_TURRET).forAllStatesExcept(state -> {
            ModelFile upper = existingModel(blockFolderLocation(ModResources.MC, "warped_fence_post"));
            ModelFile model = state.getValue(DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER ? turretBase : upper;
            return ConfiguredModel.builder().modelFile(model).build();
        }, WATERLOGGED);
    }

    /*
    private void powerBank(Supplier<? extends PowerBankBlock> supplier)
    {
        final ModelFile powerBankModel = existingModel(blockFolderLocation("power_bank"));
        getVariantBuilder(supplier.get()).forAllStates(state -> {
            Direction facing = state.getValue(FACING);
            if (facing == Direction.UP)
            {
                return ConfiguredModel.builder().modelFile(powerBankModel).build();
            }
            else if (facing == Direction.DOWN)
            {
                return ConfiguredModel.builder().modelFile(powerBankModel).rotationX(180).build();
            }
            else
            {
                int yRot = getRotationY(facing, 180);
                return ConfiguredModel.builder().modelFile(powerBankModel).rotationY(yRot).rotationX(90).build();
            }
        });

        simpleBlockItem(supplier, powerBankModel);
    }*/

    /*
    private void horizontalMachine(Supplier<? extends HorizontalMachineBlock> block, ResourceLocation sideTexture, ResourceLocation topTexture)
    {
        String name = getBlockName(block.get());
        ModelFile model = models().orientable(name, sideTexture, blockFolderLocation(name), topTexture);
        ModelFile onModel = models().orientable(name + "_on", sideTexture, blockFolderLocation(name + "_on"), topTexture);
        horizontalBlock(block.get(), state -> state.getValue(ON_PROPERTY) ? onModel : model);
        simpleBlockItem(block, model);
    }
    */
}