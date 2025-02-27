package liedge.limatech.block;

import liedge.limacore.blockentity.LimaBlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class DummyMeshBlock extends BaseWrenchEntityBlock
{
    public DummyMeshBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public @Nullable LimaBlockEntityType<?> getBlockEntityType(BlockState state)
    {
        return null;
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos)
    {
        return 1f;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state)
    {
        return RenderShape.INVISIBLE;
    }
}