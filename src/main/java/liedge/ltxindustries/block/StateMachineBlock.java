package liedge.ltxindustries.block;

import liedge.limacore.util.LimaBlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;
import java.util.function.Function;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class StateMachineBlock extends BaseWrenchEntityBlock
{
    public static StateMachineBlock staticShape(Properties properties, VoxelShape shape, boolean tickClient)
    {
        return new StateMachineBlock(properties, $ -> shape, tickClient);
    }

    public static StateMachineBlock rotatingShape(Properties properties, VoxelShape referenceShape, boolean tickClient)
    {
        Map<Direction, VoxelShape> shapeMap = LimaBlockUtil.createHorizontalShapeMap(referenceShape);
        return new StateMachineBlock(properties, shapeMap::get, tickClient);
    }

    private final Function<Direction, VoxelShape> shapeFunction;
    private final boolean tickClient;

    private StateMachineBlock(Properties properties, Function<Direction, VoxelShape> shapeFunction, boolean tickClient)
    {
        super(properties);

        this.shapeFunction = shapeFunction;
        this.tickClient = tickClient;

        registerDefaultState(getStateDefinition().any()
                .setValue(HORIZONTAL_FACING, Direction.NORTH)
                .setValue(LTXIBlockProperties.MACHINE_WORKING, false));
    }

    @Override
    protected boolean shouldTickServer(BlockState state)
    {
        return true;
    }

    @Override
    protected boolean shouldTickClient(BlockState state)
    {
        return tickClient;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return defaultBlockState().setValue(HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(HORIZONTAL_FACING, LTXIBlockProperties.MACHINE_WORKING);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return shapeFunction.apply(state.getValue(HORIZONTAL_FACING));
    }
}