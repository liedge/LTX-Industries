package liedge.ltxindustries.block;

import liedge.limacore.util.LimaBlockUtil;
import liedge.ltxindustries.blockentity.BaseTurretBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;

public class TurretBlock extends DoubleWrenchBlock
{
    private static final VoxelShape LOWER_SHAPE = Shapes.or(
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

    private static final VoxelShape UPPER_SHAPE = LimaBlockUtil.moveShape(LOWER_SHAPE, 0, -1, 0);

    public TurretBlock(Properties properties)
    {
        super(properties);

        registerDefaultState(getStateDefinition().any()
                .setValue(HORIZONTAL_FACING, Direction.NORTH)
                .setValue(DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected VoxelShape getUpperShape()
    {
        return UPPER_SHAPE;
    }

    @Override
    protected VoxelShape getLowerShape()
    {
        return LOWER_SHAPE;
    }

    @Override
    protected void onDoubleBlockRemoved(BlockState state, Level level, BlockPos pos, BlockState newState)
    {
        BaseTurretBlockEntity be = LimaBlockUtil.getSafeBlockEntity(level, pos, BaseTurretBlockEntity.class);
        if (be != null) be.onRemovedFromLevel();
    }
}