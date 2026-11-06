package liedge.ltxindustries.blockentity;

import liedge.limacore.blockentity.LimaBlockEntity;
import liedge.limacore.blockentity.LimaBlockEntityType;
import liedge.limacore.transfer.energy.EnergyHolderBlockEntity;
import liedge.limacore.transfer.fluid.FluidHolderBlockEntity;
import liedge.limacore.transfer.item.ItemHolderBlockEntity;
import liedge.limacore.util.LimaBlockUtil;
import liedge.ltxindustries.block.PrimaryMeshBlock;
import liedge.ltxindustries.block.mesh.BlockMesh;
import liedge.ltxindustries.block.mesh.LTXIBlockMeshes;
import liedge.ltxindustries.block.mesh.MeshPosition;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

public sealed class MeshBlockEntity extends LimaBlockEntity permits MeshPortBlockEntity
{
    private @Nullable BlockMesh mesh;
    private @Nullable MeshPosition meshPos;
    private @Nullable BlockPos primaryPos;

    MeshBlockEntity(LimaBlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public MeshBlockEntity(BlockPos pos, BlockState state)
    {
        this(LTXIBlockEntities.MESH_BLOCK.get(), pos, state);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state)
    {
        BlockPos primary = getPrimaryPos(pos, state);

        if (level != null && mesh != null && primary != null)
        {
            BlockState primaryState = level.getBlockState(primary);
            if (primaryState.getBlock() instanceof PrimaryMeshBlock primaryBlock && primaryBlock.getBlockMesh().equals(mesh))
            {
                level.removeBlock(primary, false);
            }
        }
    }

    @Nullable
    public <T> T getPrimaryBlockEntity(BlockPos pos, BlockState state, Class<T> beType)
    {
        BlockPos primaryPos = getPrimaryPos(pos, state);
        return primaryPos != null ? LimaBlockUtil.getSafeBlockEntity(level, primaryPos, beType) : null;
    }

    public @Nullable ResourceHandler<ItemResource> getPrimaryItems(@Nullable Direction side)
    {
        if (side != null) return null;

        ItemHolderBlockEntity be = getPrimaryBlockEntity(getBlockPos(), getBlockState(), ItemHolderBlockEntity.class);
        return be != null ? be.createExternalItems(null) : null;
    }

    public @Nullable EnergyHandler getPrimaryEnergy(@Nullable Direction side)
    {
        if (side != null) return null;

        EnergyHolderBlockEntity be = getPrimaryBlockEntity(getBlockPos(), getBlockState(), EnergyHolderBlockEntity.class);
        return be != null ? be.createExternalEnergy(null) : null;
    }

    public @Nullable ResourceHandler<FluidResource> getPrimaryFluids(@Nullable Direction side)
    {
        if (side != null) return null;

        FluidHolderBlockEntity be = getPrimaryBlockEntity(getBlockPos(), getBlockState(), FluidHolderBlockEntity.class);
        return be != null ? be.createExternalFluids(null) : null;
    }

    public @Nullable BlockMesh getMesh()
    {
        return mesh;
    }

    public void setMesh(BlockMesh mesh)
    {
        this.mesh = mesh;
    }

    public @Nullable MeshPosition getMeshPos()
    {
        return meshPos;
    }

    public void setMeshPos(MeshPosition meshPos)
    {
        this.meshPos = meshPos;
    }

    public @Nullable BlockPos getPrimaryPos(BlockPos pos, BlockState state)
    {
        if (this.primaryPos == null)
        {
            if (mesh != null && meshPos != null)
            {
                Direction zAxis = state.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
                this.primaryPos = mesh.getPrimaryBlockPos(pos, meshPos, zAxis);
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
        this.mesh = input.read("mesh", LTXIBlockMeshes.CODEC).orElse(null);

        int index = input.getIntOr("index", -1);
        this.meshPos = mesh != null && index >= 0 ? mesh.getMeshPosition(index) : null;
    }

    private void saveMesh(ValueOutput output)
    {
        output.storeNullable("mesh", LTXIBlockMeshes.CODEC, mesh);

        int index = meshPos != null ? meshPos.index() : -1;
        output.putInt("index", index);
    }
}