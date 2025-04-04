package liedge.limatech.block;

import liedge.limacore.blockentity.LimaBlockEntityType;
import liedge.limacore.inventory.menu.LimaMenuProvider;
import liedge.limacore.util.LimaBlockUtil;
import liedge.limacore.util.LimaCoreUtil;
import liedge.limacore.util.LimaRegistryUtil;
import liedge.limatech.blockentity.BaseTurretBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;

public class TurretBlock extends BaseWrenchEntityBlock implements SimpleWaterloggedBlock
{
    private static final VoxelShape TURRET_SHAPE = Shapes.or(
            // Base
            Block.box(0, 0, 0, 16, 1, 16),
            Block.box(1, 1, 1, 15, 3, 15),
            Block.box(0, 3, 0, 16, 15, 16),
            // Top frame pieces
            Block.box(0, 15, 0, 16, 16, 1),
            Block.box(0, 15, 15, 16, 16, 16),
            Block.box(0, 15, 0, 1, 16, 16),
            Block.box(15, 15, 0, 16, 16, 16),
            // Swivel & gun
            Block.box(4, 15, 4, 12, 17, 12),
            Block.box(5, 17, 5, 11, 25, 11));

    private static final VoxelShape UPPER_TURRET_SHAPE = LimaBlockUtil.moveShape(TURRET_SHAPE, 0, -1, 0);

    private LimaBlockEntityType<?> entityType;

    public TurretBlock(Properties properties)
    {
        super(properties);

        registerDefaultState(getStateDefinition().any()
                .setValue(HORIZONTAL_FACING, Direction.NORTH)
                .setValue(DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER)
                .setValue(WATERLOGGED, false));
    }

    @Override
    public @Nullable LimaBlockEntityType<?> getBlockEntityType(BlockState state)
    {
        return state.is(this) && state.getValue(DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER ? resolveBlockEntityType() : null;
    }

    @Override
    public @Nullable LimaMenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos)
    {
        BlockPos basePos = state.getValue(DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();
        return super.getMenuProvider(state, level, basePos);
    }

    @Override
    protected boolean shouldTickClient(BlockState state)
    {
        return state.getValue(DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER;
    }

    @Override
    protected boolean shouldTickServer(BlockState state)
    {
        return state.getValue(DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        return state.getValue(DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER ? TURRET_SHAPE : UPPER_TURRET_SHAPE;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player)
    {
        if (state.getValue(DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER)
        {
            BlockPos below = pos.below();
            BlockState belowState = level.getBlockState(below);
            if (belowState.is(this)) return belowState.getCloneItemStack(target, level, below, player);
        }

        return super.getCloneItemStack(state, target, level, pos, player);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockPos above = pos.above();
        FluidState fluidState = level.getFluidState(pos);

        if (pos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(above).canBeReplaced(ctx))
        {
            return defaultBlockState()
                    .setValue(HORIZONTAL_FACING, ctx.getHorizontalDirection().getOpposite())
                    .setValue(DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER)
                    .setValue(WATERLOGGED, fluidState.getType().equals(Fluids.WATER));
        }
        else
        {
            return null;
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        BlockPos above = pos.above();
        FluidState fluidState = level.getFluidState(above);
        BlockState aboveState = state.setValue(DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
        level.setBlock(above, aboveState, Block.UPDATE_ALL);

        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston)
    {
        if (!state.is(newState.getBlock()))
        {
            BlockPos opposite = state.getValue(DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos.below();
            level.removeBlock(opposite, false);

            BaseTurretBlockEntity be = LimaBlockUtil.getSafeBlockEntity(level, pos, BaseTurretBlockEntity.class);
            if (be != null) be.onRemovedFromLevel();

            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player)
    {
        if (state.getValue(DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER)
        {
            BlockPos below = pos.below();
            BlockState belowState = level.getBlockState(below);

            if (belowState.is(this))
            {
                belowState.getBlock().playerWillDestroy(level, below, belowState, player);
                return state;
            }
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid)
    {
        if (state.getValue(DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER)
        {
            if (willHarvest) return true;

            BlockPos below = pos.below();
            BlockState belowState = level.getBlockState(below);

            if (belowState.is(this))
            {
                belowState.onDestroyedByPlayer(level, below, player, false, fluid);
            }
        }

        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool)
    {
        if (state.getValue(DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER)
        {
            BlockPos below = pos.below();
            BlockState belowState = level.getBlockState(below);

            if (belowState.is(this))
            {
                belowState.getBlock().playerDestroy(level, player, below, belowState, level.getBlockEntity(below), tool); // Critical fix: Must use lower block entity, not upper (doesn't exist)
            }

            level.removeBlock(pos, false);
        }
        else
        {
            super.playerDestroy(level, player, pos, state, blockEntity, tool);
        }
    }

    @Override
    public boolean allowsRotation(Level level, BlockPos pos, BlockState state)
    {
        return false;
    }

    @Override
    public InteractionResult useWrenchOnBlock(UseOnContext context, Player player, Level level, BlockPos pos, BlockState state)
    {
        BlockPos basePos;
        BlockState baseState;

        if (state.getValue(DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER)
        {
            basePos = pos.below();
            baseState = level.getBlockState(basePos);
        }
        else
        {
            basePos = pos;
            baseState = state;
        }

        return dismantleOrRotateMachine(context, player, level, basePos, baseState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(HORIZONTAL_FACING, DOUBLE_BLOCK_HALF, WATERLOGGED);
    }

    @Override
    protected FluidState getFluidState(BlockState state)
    {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state)
    {
        return state.getValue(DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER ? RenderShape.MODEL : RenderShape.INVISIBLE;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos)
    {
        if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    private LimaBlockEntityType<?> resolveBlockEntityType()
    {
        if (entityType == null)
        {
            ResourceLocation blockId = LimaRegistryUtil.getBlockId(this);
            this.entityType = LimaCoreUtil.castOrThrow(LimaBlockEntityType.class, LimaRegistryUtil.getNonNullRegistryValue(blockId, BuiltInRegistries.BLOCK_ENTITY_TYPE),
                    () -> new IllegalStateException("Block with id '" + blockId + "' has no block entity type registered with matching ID."));
        }

        return entityType;
    }
}