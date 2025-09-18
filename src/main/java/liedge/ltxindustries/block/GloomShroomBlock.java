package liedge.ltxindustries.block;

import com.mojang.serialization.MapCodec;
import liedge.limacore.util.LimaBlockUtil;
import liedge.ltxindustries.registry.game.LTXIBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.util.TriState;

import java.util.List;

public class GloomShroomBlock extends BushBlock implements SculkBehaviour
{
    private static final VoxelShape SHAPE = LimaBlockUtil.dimensionBox(3, 0, 3, 10, 13, 10);

    public GloomShroomBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos)
    {
        return state.is(Blocks.SCULK);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        TriState soilDecision = belowState.canSustainPlant(level, belowPos, Direction.UP, state);

        if (!soilDecision.isDefault()) return soilDecision.isTrue();
        else return belowState.is(Blocks.SCULK) && !level.canSeeSky(pos);
    }

    @Override
    protected MapCodec<? extends BushBlock> codec()
    {
        return FungusBlock.CODEC;
    }

    @Override
    public int attemptUseCharge(SculkSpreader.ChargeCursor cursor, LevelAccessor level, BlockPos catalystPos, RandomSource random, SculkSpreader spreader, boolean shouldConvertBlocks)
    {
        int charge = cursor.getCharge();

        if (charge != 0 && random.nextFloat() <= 0.33333f)
        {
            BlockPos cursorPos = cursor.getPos();
            List<BlockPos> spacesToGrow = BlockPos.betweenClosedStream(cursorPos.offset(-1, -1, -1), cursorPos.offset(1, 1, 1))
                    .filter(bp -> canGrowOn(level, bp))
                    .map(BlockPos::immutable)
                    .toList();

            if (!spacesToGrow.isEmpty())
            {
                BlockPos placePos = spacesToGrow.get(random.nextInt(spacesToGrow.size())).above();
                level.setBlock(placePos, LTXIBlocks.GLOOM_SHROOM.get().defaultBlockState(), Block.UPDATE_ALL);
                level.playSound(null, placePos, SoundEvents.FUNGUS_PLACE, SoundSource.BLOCKS, 1f, 1f);

                return charge - 1;
            }
        }

        return charge;
    }

    @Override
    public boolean canChangeBlockStateOnSpread()
    {
        return false;
    }

    private boolean canGrowOn(LevelAccessor level, BlockPos sculkPos)
    {
        if (!level.getBlockState(sculkPos).is(Blocks.SCULK)) return false;

        BlockPos pos = sculkPos.above();
        BlockState state = level.getBlockState(pos);
        FluidState fluidState = level.getFluidState(pos);

        return state.canBeReplaced() && !fluidState.isSource();
    }
}