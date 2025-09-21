package liedge.ltxindustries.block;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

import static liedge.limacore.util.LimaBlockUtil.*;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class SurfaceStickingBlock extends Block implements SimpleWaterloggedBlock
{
    private final Map<Direction, VoxelShape> shapeMap;
    private final boolean needsSturdyFace;

    public SurfaceStickingBlock(Properties properties, VoxelShape identityShape, boolean needsSturdyFace)
    {
        super(properties);
        this.shapeMap = createShapeMap(identityShape);
        registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.UP).setValue(WATERLOGGED, false));
        this.needsSturdyFace = needsSturdyFace;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return shapeMap.get(state.getValue(FACING));
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        Direction facing = state.getValue(FACING);
        BlockPos placedOnPos = pos.relative(facing.getOpposite());
        BlockState placedOnState = level.getBlockState(placedOnPos);

        return placedOnState.isFaceSturdy(level, placedOnPos, facing);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        boolean waterlogged = level.getFluidState(pos).is(Fluids.WATER);
        BlockState placeState = defaultBlockState()
                .setValue(WATERLOGGED, waterlogged)
                .setValue(FACING, context.getClickedFace());

        return canSurvive(placeState, level, pos) ? placeState : null;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos)
    {
        if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));

        if (needsSturdyFace && direction == state.getValue(FACING).getOpposite() && !state.canSurvive(level, pos))
        {
            return Blocks.AIR.defaultBlockState();
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    protected FluidState getFluidState(BlockState state)
    {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    private static Map<Direction, VoxelShape> createShapeMap(VoxelShape identity)
    {
        Map<Direction, VoxelShape> map = new EnumMap<>(Direction.class);

        map.put(Direction.UP, identity);
        map.put(Direction.DOWN, rotateXClockwise(identity, 180));
        VoxelShape north = rotateZClockWise(identity, 270);
        Direction.Plane.HORIZONTAL.forEach(facing -> map.put(facing, rotateYClockwise(north, rotationYFromDirection(facing))));

        return ImmutableMap.copyOf(map);
    }
}