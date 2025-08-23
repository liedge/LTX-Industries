package liedge.ltxindustries.blockentity;

import liedge.limacore.blockentity.LimaBlockEntity;
import liedge.limacore.util.LimaNbtUtil;
import liedge.ltxindustries.block.mesh.BlockMesh;
import liedge.ltxindustries.block.mesh.LTXIBlockMeshes;
import liedge.ltxindustries.block.mesh.MeshPosition;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
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
    private BlockPos primaryPos;

    public MeshBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.MESH_BLOCK.get(), pos, state);
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector) {}

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