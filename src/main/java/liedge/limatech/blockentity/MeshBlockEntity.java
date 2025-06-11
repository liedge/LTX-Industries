package liedge.limatech.blockentity;

import liedge.limacore.blockentity.LimaBlockEntity;
import liedge.limacore.util.LimaBlockUtil;
import liedge.limacore.util.LimaNbtUtil;
import liedge.limatech.block.mesh.BlockMesh;
import liedge.limatech.block.mesh.LimaTechBlockMeshes;
import liedge.limatech.block.mesh.MeshPosition;
import liedge.limatech.registry.game.LimaTechBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public final class MeshBlockEntity extends LimaBlockEntity
{
    @Nullable
    private ResourceLocation meshId;
    @Nullable
    private BlockMesh blockMesh;

    private int meshIndex = -1;
    @Nullable
    private MeshPosition meshPosition;
    @Nullable
    private VoxelShape shape;
    @Nullable
    private BlockPos primaryPos;

    public MeshBlockEntity(BlockPos pos, BlockState state)
    {
        super(LimaTechBlockEntities.MESH_BLOCK.get(), pos, state);
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector) {}

    public @Nullable BlockMesh getBlockMesh()
    {
        if (blockMesh == null && meshId != null)
        {
            blockMesh = LimaTechBlockMeshes.getBlockMesh(meshId);
        }

        return blockMesh;
    }

    public void setBlockMesh(BlockMesh blockMesh)
    {
        this.blockMesh = blockMesh;
        this.meshId = LimaTechBlockMeshes.getBlockMeshId(blockMesh);
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

    public @Nullable VoxelShape getMeshShape(LevelReader level, BlockState state, BlockPos pos)
    {
        if (this.shape == null)
        {
            BlockPos primaryPos = getPrimaryPos(pos, state);
            BlockMesh blockMesh = getBlockMesh();
            MeshPosition meshPosition = getMeshPosition();
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);

            if (primaryPos != null && blockMesh != null && meshPosition != null)
            {
                VoxelShape identityShape = level.getBlockState(primaryPos).getShape(level, primaryPos);

                identityShape = LimaBlockUtil.rotateYClockwise(identityShape, LimaBlockUtil.rotationYFromDirection(facing));
                Vec3i offset = blockMesh.computeMeshOffset(meshPosition, blockMesh.getPrimary(), facing.getOpposite());
                identityShape = LimaBlockUtil.moveShape(identityShape, offset.getX(), offset.getY(), offset.getZ());

                this.shape = identityShape;
            }
        }

        return this.shape;
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
        CompoundTag tag = new CompoundTag();
        saveMesh(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider)
    {
        loadMesh(tag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);
        loadMesh(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
        saveMesh(tag);
    }

    private void loadMesh(CompoundTag tag)
    {
        this.meshId = LimaNbtUtil.getOptionalResourceLocation(tag, "mesh");
        this.meshIndex = LimaNbtUtil.getAsInt(tag, "index", -1);
    }

    private void saveMesh(CompoundTag tag)
    {
        LimaNbtUtil.putOptionalResourceLocation(tag, "mesh", meshId);
        tag.putInt("index", meshIndex);
    }
}