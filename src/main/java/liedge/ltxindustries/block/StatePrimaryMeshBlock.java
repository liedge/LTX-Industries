package liedge.ltxindustries.block;

import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.VoxelShape;

public class StatePrimaryMeshBlock extends PrimaryMeshBlock
{
    public StatePrimaryMeshBlock(Properties properties, Identifier meshId, VoxelShape identityShape, boolean tickClient)
    {
        super(properties, meshId, identityShape, tickClient);

        registerDefaultState(getStateDefinition().any()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
                .setValue(LTXIBlockProperties.BINARY_MACHINE_STATE, MachineState.IDLE)
                .setValue(BlockStateProperties.WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(LTXIBlockProperties.BINARY_MACHINE_STATE);
    }
}