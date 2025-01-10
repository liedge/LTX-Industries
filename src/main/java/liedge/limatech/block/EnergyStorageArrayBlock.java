package liedge.limatech.block;

import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.LimaBlockEntityType;
import liedge.limatech.registry.LimaTechBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import static liedge.limatech.block.LimaTechBlockProperties.getESASideIOProperty;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class EnergyStorageArrayBlock extends BaseWrenchEntityBlock
{
    private static final VoxelShape SHAPE = Block.box(0.5d, 0.5d, 0.5d, 15.5d, 15.5d, 15.5d);

    private final boolean infinite;

    public EnergyStorageArrayBlock(Properties properties, boolean infinite)
    {
        super(properties);
        this.infinite = infinite;
    }

    @Override
    public @Nullable LimaBlockEntityType<?> getBlockEntityType(BlockState state)
    {
        return infinite ? LimaTechBlockEntities.INFINITE_ENERGY_STORAGE_ARRAY.get() : LimaTechBlockEntities.ENERGY_STORAGE_ARRAY.get();
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
        BlockState state = defaultBlockState().setValue(HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
        for (Direction side : Direction.values())
        {
            state = state.setValue(getESASideIOProperty(side), IOAccess.INPUT_ONLY);
        }

        return state;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(HORIZONTAL_FACING);
        for (Direction side : Direction.values())
        {
            builder.add(getESASideIOProperty(side));
        }
    }
}