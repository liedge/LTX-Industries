package liedge.limatech.block;

import liedge.limacore.blockentity.LimaBlockEntityType;
import liedge.limatech.registry.LimaTechBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class EquipmentModTableBlock extends BaseWrenchEntityBlock
{
    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(0, 0, 0, 16, 2, 16),
            Block.box(1, 2, 1, 15, 10, 15),
            Block.box(0, 10, 0, 16, 14, 16));

    public EquipmentModTableBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public LimaBlockEntityType<?> getBlockEntityType(BlockState state)
    {
        return LimaTechBlockEntities.EQUIPMENT_MOD_TABLE.get();
    }

    @Override
    protected boolean shouldTickServer(BlockState state)
    {
        return true;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return defaultBlockState().setValue(HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(HORIZONTAL_FACING);
    }
}