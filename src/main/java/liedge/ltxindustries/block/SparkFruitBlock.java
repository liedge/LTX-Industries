package liedge.ltxindustries.block;

import liedge.limacore.util.LimaBlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.util.TriState;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.AGE_2;

public class SparkFruitBlock extends Block implements BonemealableBlock
{
    private static final VoxelShape AGE_0_SHAPE = LimaBlockUtil.dimensionBox(5.5d, 7, 5.5d, 5, 9, 5);
    private static final VoxelShape AGE_1_SHAPE = LimaBlockUtil.dimensionBox(5.5d, 4, 5.5d, 5, 12, 5);
    private static final VoxelShape AGE_2_SHAPE = LimaBlockUtil.dimensionBox(4.5d, 2, 4.5d, 7, 14, 7);

    public SparkFruitBlock(Properties properties)
    {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(AGE_2, 0));
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state)
    {
        return state.getValue(AGE_2) < 2;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state)
    {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state)
    {
        level.setBlock(pos, state.setValue(AGE_2, state.getValue(AGE_2) + 1), Block.UPDATE_CLIENTS);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state)
    {
        return state.getValue(AGE_2) < 2;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        int age = state.getValue(AGE_2);
        if (age < 2 && CommonHooks.canCropGrow(level, pos, state, level.random.nextInt(8) == 0))
        {
            level.setBlock(pos, state.setValue(AGE_2, age + 1), Block.UPDATE_CLIENTS);
            CommonHooks.fireCropGrowPost(level, pos, state);
        }
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        BlockPos abovePos = pos.above();
        if (level.isOutsideBuildHeight(abovePos)) return false;

        BlockState aboveState = level.getBlockState(abovePos);
        TriState soilDecision = aboveState.canSustainPlant(level, abovePos, Direction.DOWN, state);

        if (!soilDecision.isDefault()) return soilDecision.isTrue();
        else return aboveState.is(Blocks.JUNGLE_LEAVES);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState defaultState = defaultBlockState();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        return canSurvive(defaultState, level, pos) ? defaultState : null;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos)
    {
        return direction == Direction.UP && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        int age = state.getValue(AGE_2);
        return switch (age)
        {
            case 1 -> AGE_1_SHAPE;
            case 2 -> AGE_2_SHAPE;
            default -> AGE_0_SHAPE;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(AGE_2);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType)
    {
        return false;
    }
}