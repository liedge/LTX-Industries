package liedge.ltxindustries.block;

import liedge.limacore.util.LimaBlockUtil;
import liedge.ltxindustries.block.mesh.BlockMesh;
import liedge.ltxindustries.block.mesh.LTXIBlockMeshes;
import liedge.ltxindustries.blockentity.MeshBlockEntity;
import liedge.ltxindustries.registry.game.LTXIBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public abstract class PrimaryMeshBlock extends BaseMeshBlock
{
    private final BlockMesh blockMesh;

    protected PrimaryMeshBlock(Properties properties, ResourceLocation blockMeshId)
    {
        super(properties);
        this.blockMesh = Objects.requireNonNull(LTXIBlockMeshes.getBlockMesh(blockMeshId));
    }

    public BlockMesh getBlockMesh()
    {
        return blockMesh;
    }

    @Override
    protected abstract VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context);

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Level level = context.getLevel();
        BlockPos origin = context.getClickedPos();
        Direction zAxis = context.getHorizontalDirection();
        BlockMesh mesh = getBlockMesh();

        if (mesh.canPlaceMesh(level, origin, mesh.getPrimary(), zAxis))
        {
            return defaultBlockState()
                    .setValue(HORIZONTAL_FACING, zAxis.getOpposite())
                    .setValue(WATERLOGGED, level.getFluidState(origin).is(Fluids.WATER));
        }
        else
        {
            return null;
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        BlockMesh mesh = getBlockMesh();
        Direction facing = state.getValue(HORIZONTAL_FACING);

        mesh.meshStream(pos, mesh.getPrimary(), facing.getOpposite()).filter(cursor -> !cursor.equals(pos)).forEach(cursor ->
        {
            BlockState cursorState = LTXIBlocks.MESH_BLOCK.get().defaultBlockState()
                    .setValue(HORIZONTAL_FACING, facing)
                    .setValue(WATERLOGGED, level.getFluidState(cursor).is(Fluids.WATER));
            level.setBlockAndUpdate(cursor, cursorState);

            MeshBlockEntity blockEntity = LimaBlockUtil.getSafeBlockEntity(level, cursor, MeshBlockEntity.class);
            if (blockEntity != null)
            {
                blockEntity.setBlockMesh(mesh);
                blockEntity.setMeshPosition(cursor.getMeshPosition());
                blockEntity.setChanged();
            }
        });

        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston)
    {
        if (state.is(this) && !newState.is(this))
        {
            BlockMesh mesh = getBlockMesh();
            mesh.meshStream(pos, mesh.getPrimary(), state.getValue(HORIZONTAL_FACING).getOpposite()).filter(cursor -> !cursor.equals(pos)).forEach(cursor -> level.removeBlock(cursor, false));
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}