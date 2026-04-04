package liedge.ltxindustries.blockentity;

import liedge.limacore.blockentity.LimaBlockEntity;
import liedge.ltxindustries.block.PrimaryMeshBlock;
import liedge.ltxindustries.block.mesh.BlockMesh;
import liedge.ltxindustries.block.mesh.LTXIBlockMeshes;
import liedge.ltxindustries.block.mesh.MeshPosition;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public final class MeshBlockEntity extends LimaBlockEntity
{
    @Nullable
    private Identifier meshId;
    @Nullable
    private BlockMesh blockMesh;

    private int meshIndex = -1;
    @Nullable
    private MeshPosition meshPosition;
    @Nullable
    private BlockPos primaryPos;

    public MeshBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.MESH_BLOCK.get(), pos, state);
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector) {}

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state)
    {
        Level level = nonNullLevel();

        BlockMesh mesh = getBlockMesh();
        BlockPos primary = getPrimaryPos(pos, state);

        if (mesh != null && primary != null)
        {
            BlockState primaryState = level.getBlockState(primary);
            if (primaryState.getBlock() instanceof PrimaryMeshBlock primaryBlock && primaryBlock.getBlockMesh().equals(mesh))
            {
                level.removeBlock(primary, false);
            }
        }
    }

    public @Nullable BlockMesh getBlockMesh()
    {
        if (blockMesh == null && meshId != null)
        {
            blockMesh = LTXIBlockMeshes.getBlockMesh(meshId);
        }

        return blockMesh;
    }

    public void setBlockMesh(BlockMesh blockMesh)
    {
        this.blockMesh = blockMesh;
        this.meshId = LTXIBlockMeshes.getBlockMeshId(blockMesh);
    }

    public @Nullable MeshPosition getMeshPosition()
    {
        if (meshPosition == null && meshIndex != -1)
        {
            BlockMesh mesh = getBlockMesh();
            if (mesh != null) meshPosition = mesh.getMeshPosition(meshIndex);
        }

        return meshPosition;
    }

    public void setMeshPosition(MeshPosition meshPosition)
    {
        this.meshPosition = meshPosition;
        this.meshIndex = meshPosition.index();
    }

    public @Nullable BlockPos getPrimaryPos(BlockPos pos, BlockState state)
    {
        if (this.primaryPos == null)
        {
            BlockMesh mesh = getBlockMesh();
            MeshPosition meshPosition = getMeshPosition();

            if (mesh != null && meshPosition != null)
            {
                Direction zAxis = state.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
                this.primaryPos = mesh.getPrimaryBlockPos(pos, meshPosition, zAxis);
            }
        }

        return this.primaryPos;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries)
    {
        CompoundTag tag;

        try (ProblemReporter.ScopedCollector reporter = createReporter())
        {
            TagValueOutput output = TagValueOutput.createWithContext(reporter, registries);
            saveMesh(output);
            tag = output.buildResult();
        }

        return tag;
    }

    @Override
    public void handleUpdateTag(ValueInput input)
    {
        loadMesh(input);
    }

    @Override
    protected void loadAdditional(ValueInput input)
    {
        super.loadAdditional(input);
        loadMesh(input);
    }

    @Override
    protected void saveAdditional(ValueOutput output)
    {
        super.saveAdditional(output);
        saveMesh(output);
    }

    private void loadMesh(ValueInput input)
    {
        this.meshId = input.read("mesh", Identifier.CODEC).orElse(null);
        this.meshIndex = input.getIntOr("index", -1);
    }

    private void saveMesh(ValueOutput output)
    {
        output.storeNullable("mesh", Identifier.CODEC, meshId);
        output.putInt("index", meshIndex);
    }
}