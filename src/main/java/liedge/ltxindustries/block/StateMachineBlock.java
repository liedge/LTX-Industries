package liedge.ltxindustries.block;

import liedge.limacore.blockentity.LimaBlockEntityType;
import liedge.limacore.util.LimaBlockUtil;
import liedge.limacore.util.LimaCoreUtil;
import liedge.limacore.util.LimaRegistryUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

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

    private LimaBlockEntityType<?> entityType;
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
    public @Nullable LimaBlockEntityType<?> getBlockEntityType(BlockState state)
    {
        if (entityType == null)
        {
            ResourceLocation blockId = LimaRegistryUtil.getBlockId(this);
            this.entityType = LimaCoreUtil.castOrThrow(LimaBlockEntityType.class, LimaRegistryUtil.getNonNullRegistryValue(blockId, BuiltInRegistries.BLOCK_ENTITY_TYPE));
        }

        return entityType;
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