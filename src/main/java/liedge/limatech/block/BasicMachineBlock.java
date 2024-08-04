package liedge.limatech.block;

import liedge.limacore.block.LimaEntityBlock;
import liedge.limacore.blockentity.LimaBlockEntityType;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class BasicMachineBlock extends LimaEntityBlock
{
    public static final BooleanProperty MACHINE_WORKING = BooleanProperty.create("working");
    public static final VoxelShape BASIC_MACHINE_SHAPE = Shapes.or(
            // Center frame piece
            Block.box(0.5d, 0.5d, 0.5d, 15.5d, 15.5d, 15.5d),
            // Bottom frame pieces
            Block.box(0, 0, 0, 16, 1, 1),
            Block.box(0, 0, 15, 16,  1, 16),
            Block.box(0, 0, 0, 1, 1, 16),
            Block.box(15, 0, 0, 16, 1, 16),
            // Vertical frame pieces
            Block.box(0, 0, 0, 1, 16, 1),
            Block.box(15, 0, 0, 16, 16, 1),
            Block.box(0, 0, 15, 1, 16, 16),
            Block.box(15, 0, 15, 16, 16, 16),
            // Top frame pieces
            Block.box(0, 15, 0, 16, 16, 1),
            Block.box(0, 15, 15, 16, 16, 16),
            Block.box(0, 15, 0, 1, 16, 16),
            Block.box(15, 15, 0, 16, 16, 16));

    private LimaBlockEntityType<?> entityType;

    public BasicMachineBlock(Properties properties)
    {
        super(properties);

        registerDefaultState(getStateDefinition().any()
                .setValue(HORIZONTAL_FACING, Direction.NORTH)
                .setValue(MACHINE_WORKING, false));
    }

    @Override
    public @Nullable LimaBlockEntityType<?> getBlockEntityType(BlockState state)
    {
        if (entityType == null)
        {
            ResourceLocation blockId = LimaRegistryUtil.getNonNullRegistryKey(this, BuiltInRegistries.BLOCK);
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
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return defaultBlockState().setValue(HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(HORIZONTAL_FACING, MACHINE_WORKING);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return BASIC_MACHINE_SHAPE;
    }
}