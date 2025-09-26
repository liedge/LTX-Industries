package liedge.ltxindustries.data.generation;

import com.mojang.datafixers.util.Function3;
import liedge.limacore.data.generation.LimaBlockStateProvider;
import liedge.limacore.lib.ModResources;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.block.MachineState;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.function.Function;

import static liedge.limacore.util.LimaRegistryUtil.getBlockName;
import static liedge.ltxindustries.block.LTXIBlockProperties.BINARY_MACHINE_STATE;
import static liedge.ltxindustries.registry.game.LTXIBlocks.*;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;

class BlockStatesGen extends LimaBlockStateProvider
{
    // Existing model references
    private final ModelFile machineParticlesOnly = existingModel(blockFolderLocation("machine_particles"));
    private final ModelFile turretBase = existingModel(blockFolderLocation("turret_base"));
    private final ModelFile basicMachineSEW = existingModel(blockFolderLocation("basic_machine_sew"));
    private final ModelFile basicMachineNSEW = existingModel(blockFolderLocation("basic_machine_nsew"));

    BlockStatesGen(PackOutput output, ExistingFileHelper helper)
    {
        super(output, LTXIndustries.RESOURCES, helper);
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
        oreCluster(RAW_TITANIUM_CLUSTER, blockFolderLocation(ModResources.MC, "basalt_top"), RAW_TITANIUM_BLOCK);
        oreCluster(RAW_NIOBIUM_CLUSTER, blockFolderLocation(Blocks.END_STONE), RAW_NIOBIUM_BLOCK);
        cubeAll(TITANIUM_BLOCK);
        cubeAll(NIOBIUM_BLOCK);
        cubeAll(SLATESTEEL_BLOCK);

        // Glow blocks
        final ModelFile neonLightModel = models().withExistingParent("neon_light_base", "block/block").texture("particle", "#all").ao(false)
                .element().from(0, 0, 0).to(16, 16, 16).allFaces((side, face) -> face.uvs(0, 0, 16, 16).texture("#all").cullface(side)).emissivity(15, 15).shade(false).end();
        NEON_LIGHTS.forEach((color, holder) -> simpleBlockWithItem(holder, getBlockBuilder(holder).parent(neonLightModel).texture("all", blockFolderLocation("neon_light/" + color.toString()))));
        cubeAll(TITANIUM_PANEL);
        cubeAll(SMOOTH_TITANIUM_PANEL);
        cubeAll(TITANIUM_GLASS).renderType("cutout");

        // Plants
        sparkFruit(SPARK_FRUIT);
        berryVines(BILEVINE, AGE_25);
        berryVines(BILEVINE_PLANT);
        simpleBlock(GLOOM_SHROOM, existingModel(blockFolderLocation(GLOOM_SHROOM)));

        // Energy cell array
        horizontalBlock(ENERGY_CELL_ARRAY.get(), existingModel(blockFolderLocation(ENERGY_CELL_ARRAY)));
        horizontalBlock(INFINITE_ENERGY_CELL_ARRAY.get(), existingModel(blockFolderLocation(INFINITE_ENERGY_CELL_ARRAY)));

        // Simple machines
        cookingMachine(DIGITAL_FURNACE);
        cookingMachine(DIGITAL_SMOKER);
        cookingMachine(DIGITAL_BLAST_FURNACE);
        basicFrameStateMachine(GRINDER, (state, $, builder) -> builder.parent(basicMachineSEW));
        emissiveFrontMachine(MATERIAL_FUSING_CHAMBER);
        stateMachineBase(ELECTROCENTRIFUGE);
        stateMachineBase(MIXER);
        stateMachineBase(VOLTAIC_INJECTOR);
        stateMachineBase(CHEM_LAB);
        primaryMeshBlock(FABRICATOR);
        simpleBlockItem(FABRICATOR);
        horizontalBlockWithSimpleItem(AUTO_FABRICATOR);
        simpleBlockWithItem(EQUIPMENT_UPGRADE_STATION);
        primaryMeshBlock(MOLECULAR_RECONSTRUCTOR);
        simpleBlockItem(MOLECULAR_RECONSTRUCTOR);

        // Turret
        primaryMeshBlock(ROCKET_TURRET, turretBase);
        primaryMeshBlock(RAILGUN_TURRET, turretBase);

        // Fluids
        liquidBlock(VIRIDIC_ACID_BLOCK);

        // Technical blocks
        surfaceStickingBlock(GLOWSTICK, state -> existingModel(blockFolderLocation(state.getBlock())));
        getVariantBuilder(MESH_BLOCK).forAllStatesExcept(state -> ConfiguredModel.builder().modelFile(machineParticlesOnly).build(), HORIZONTAL_FACING, WATERLOGGED);
    }

    private void surfaceStickingBlock(Holder<Block> holder, Function<BlockState, ModelFile> modelFunction)
    {
        getVariantBuilder(holder).forAllStatesExcept(state ->
        {
            ConfiguredModel.Builder<?> builder = ConfiguredModel.builder().modelFile(modelFunction.apply(state));
            Direction facing = state.getValue(FACING);

            if (facing == Direction.DOWN)
            {
                builder.rotationX(180);
            }
            else if (facing != Direction.UP)
            {
                builder.rotationX(90);
                builder.rotationY(getRotationY(facing));
            }

            return builder.build();
        }, WATERLOGGED);
    }

    private void oreCluster(Holder<Block> clusterBlock, ResourceLocation baseTexture, Holder<Block> rawOreBlock)
    {
        ModelFile model = getBlockBuilder(clusterBlock)
                .parent(existingModel(blockFolderLocation("raw_ore_cluster")))
                .texture("base", baseTexture)
                .texture("ore", blockFolderLocation(rawOreBlock));

        surfaceStickingBlock(clusterBlock, ignored -> model);
        simpleBlockItem(clusterBlock, model);
    }

    private void sparkFruit(Holder<Block> holder)
    {
        getVariantBuilder(holder).forAllStates(state ->
        {
            String name = getBlockName(holder);
            int age = state.getValue(AGE_2);
            ModelFile model = existingModel(blockFolderLocation(name + "_" + age));
            return ConfiguredModel.builder().modelFile(model).build();
        });
    }

    private void berryVines(Holder<Block> holder, Property<?>... ignoredProperties)
    {
        getVariantBuilder(holder).forAllStatesExcept(state ->
        {
            String name = getBlockName(holder);
            if (state.getValue(BERRIES)) name += "_lit";
            ModelFile model = models().cross(name, blockFolderLocation(name)).renderType("cutout");
            return ConfiguredModel.builder().modelFile(model).build();
        }, ignoredProperties);
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

    private void stateMachine(Holder<Block> holder, Function<MachineState, ModelFile> modelMapper)
    {
        getVariantBuilder(holder).forAllStatesExcept(state -> ConfiguredModel.builder().modelFile(modelMapper.apply(state.getValue(BINARY_MACHINE_STATE))).rotationY(getRotationY(state.getValue(HORIZONTAL_FACING))).build(),
                WATERLOGGED);
    }

    private void stateMachineBase(Holder<Block> holder)
    {
        ResourceLocation pathBase = blockFolderLocation(holder);
        stateMachine(holder, state -> models().getExistingFile(pathBase.withSuffix("_" + state.getSerializedName())));
    }

    private void primaryMeshBlock(Holder<Block> holder, ModelFile model)
    {
        getVariantBuilder(holder).forAllStatesExcept(state -> ConfiguredModel.builder().modelFile(model).rotationY(getRotationY(state.getValue(HORIZONTAL_FACING))).build(), WATERLOGGED);
    }

    private void primaryMeshBlock(Holder<Block> holder)
    {
        primaryMeshBlock(holder, existingModel(blockFolderLocation(holder)));
    }

    private void basicFrameStateMachine(Holder<Block> holder, Function3<MachineState, ResourceLocation, BlockModelBuilder, BlockModelBuilder> operator)
    {
        Function<MachineState, String> nameFunction = state -> getBlockName(holder) + "_" + state.getSerializedName();
        ResourceLocation pathBase = blockFolderLocation(holder);
        stateMachine(holder, state -> operator.apply(state, pathBase, models().getBuilder(nameFunction.apply(state)).texture("front", pathBase.withSuffix("_" + state.getSerializedName()))));
        simpleBlockItem(holder, models().getBuilder(nameFunction.apply(MachineState.IDLE)));
    }

    private void emissiveFrontMachine(Holder<Block> holder)
    {
        basicFrameStateMachine(holder, (state, pathBase, builder) ->
        {
            if (state == MachineState.IDLE)
                return builder.parent(basicMachineSEW);
            else
                return builder.parent(basicMachineNSEW).texture("front_emissive", pathBase.withSuffix("_" + state.getSerializedName() + "_emissive"));
        });
    }

    private void cookingMachine(Holder<Block> holder)
    {
        ResourceLocation basicMachineMesh = blockFolderLocation("basic_machine_mesh");
        basicFrameStateMachine(holder, (state, pathBase, builder) ->
        {
            if (state == MachineState.IDLE)
                return builder.parent(basicMachineSEW).texture("top", basicMachineMesh);
            else
                return builder.parent(basicMachineNSEW).texture("front_emissive", pathBase.withSuffix("_" + state.getSerializedName() + "_emissive")).texture("top", basicMachineMesh);
        });
    }
}