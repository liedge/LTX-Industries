package liedge.ltxindustries.block;

import liedge.limacore.blockentity.LimaBlockEntityType;
import liedge.limacore.menu.LimaMenuProvider;
import liedge.limacore.util.LimaBlockUtil;
import liedge.ltxindustries.block.mesh.MeshPosition;
import liedge.ltxindustries.blockentity.MeshBlockEntity;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public final class MeshBlock extends BaseMeshBlock
{
    public MeshBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context)
    {
        // Mesh blocks aren't directly placeable
        return null;
    }

    @Override
    public LimaBlockEntityType<?> getBlockEntityType(BlockState state)
    {
        return LTXIBlockEntities.MESH_BLOCK.get();
    }

    @Override
    public @Nullable LimaMenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos)
    {
        BlockPos primaryPos = getPrimaryPos(level, pos, state);
        return primaryPos != null ? blockEntityMenuProvider(level, primaryPos) : null;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player)
    {
        BlockPos primaryPos = getPrimaryPos(level, pos, state);

        if (primaryPos != null)
        {
            BlockState primaryState = level.getBlockState(primaryPos);
            if (primaryState.getBlock() instanceof PrimaryMeshBlock primaryMeshBlock)
                return primaryMeshBlock.getCloneItemStack(primaryState, target, level, primaryPos, player);
        }

        return ItemStack.EMPTY;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos)
    {
        if (level instanceof LevelReader)
        {
            BlockPos primaryPos = getPrimaryPos((LevelReader) level, pos, state);
            if (primaryPos != null) return level.getBlockState(primaryPos).getDestroyProgress(player, level, primaryPos);
        }

        return super.getDestroyProgress(state, player, level, pos);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston)
    {
        if (!state.is(newState.getBlock()))
        {
            BlockPos primaryPos = getPrimaryPos(level, pos, state);
            if (primaryPos != null)
            {
                BlockState primaryState = level.getBlockState(primaryPos);
                if (primaryState.getBlock() instanceof PrimaryMeshBlock)
                {
                    level.removeBlock(primaryPos, false);
                }
            }

            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player)
    {
        BlockPos primaryPos = getPrimaryPos(level, pos, state);
        if (primaryPos != null)
        {
            BlockState primaryState = level.getBlockState(primaryPos);
            if (primaryState.getBlock() instanceof PrimaryMeshBlock)
            {
                primaryState.getBlock().playerWillDestroy(level, primaryPos, primaryState, player);
                return state;
            }
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid)
    {
        if (willHarvest) return true; // What does this even do?

        BlockPos primaryPos = getPrimaryPos(level, pos, state);
        if (primaryPos != null)
        {
            BlockState primaryState = level.getBlockState(primaryPos);
            if (primaryState.getBlock() instanceof PrimaryMeshBlock)
            {
                primaryState.onDestroyedByPlayer(level, primaryPos, player, false, fluid);
            }
        }

        return super.onDestroyedByPlayer(state, level, pos, player, false, fluid);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool)
    {
        BlockPos primaryPos = getPrimaryPos(level, pos, state);

        if (primaryPos != null)
        {
            BlockState primaryState = level.getBlockState(primaryPos);
            if (primaryState.getBlock() instanceof PrimaryMeshBlock)
            {
                primaryState.getBlock().playerDestroy(level, player, primaryPos, primaryState, level.getBlockEntity(primaryPos), tool);
            }
        }
        else
        {
            super.playerDestroy(level, player, pos, state, blockEntity, tool);
        }

        level.removeBlock(pos, false);
    }

    @Override
    protected BlockState handleWrenchDismantle(Level level, BlockPos pos, BlockState state, @Nullable Player player, ItemStack tool, boolean simulate)
    {
        BlockPos primaryPos = getPrimaryPos(level, pos, state);
        if (primaryPos != null)
        {
            BlockState primaryState = level.getBlockState(primaryPos);
            if (primaryState.getBlock() instanceof PrimaryMeshBlock primaryMeshBlock)
            {
                primaryMeshBlock.handleWrenchDismantle(level, primaryPos, primaryState, player, tool, simulate);
            }
        }

        return getFluidState(state).createLegacyBlock();
    }

    @Override
    protected RenderShape getRenderShape(BlockState state)
    {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        MeshBlockEntity blockEntity = LimaBlockUtil.getBlockEntity(level, pos, MeshBlockEntity.class);
        if (blockEntity != null)
        {
            BlockPos primaryPos = blockEntity.getPrimaryPos(pos, state);
            MeshPosition meshPosition = blockEntity.getMeshPosition();

            if (primaryPos != null && meshPosition != null && level.getBlockState(primaryPos).getBlock() instanceof PrimaryMeshBlock primaryBlock)
            {
                return primaryBlock.getMeshBlockShape(state.getValue(HORIZONTAL_FACING), meshPosition);
            }
        }

        return Shapes.empty();
    }

    private @Nullable BlockPos getPrimaryPos(LevelReader level, BlockPos pos, BlockState state)
    {
        MeshBlockEntity blockEntity = LimaBlockUtil.getSafeBlockEntity(level, pos, MeshBlockEntity.class);
        return blockEntity != null ? blockEntity.getPrimaryPos(pos, state) : null;
    }
}