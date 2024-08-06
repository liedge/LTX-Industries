package liedge.limatech.block;

import liedge.limacore.block.LimaEntityBlock;
import liedge.limatech.item.MachineWrenchItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public abstract class BaseWrenchEntityBlock extends LimaEntityBlock
{
    protected BaseWrenchEntityBlock(Properties properties)
    {
        super(properties);
    }

    public InteractionResult useWrenchOnBlock(UseOnContext context, Player player, Level level, BlockPos pos, BlockState state)
    {
        return dismantleOrRotateMachine(context, player, level, pos, state);
    }

    public boolean allowsRotation(BlockState state)
    {
        return state.hasProperty(HORIZONTAL_FACING);
    }

    protected BlockState rotateBlockState(UseOnContext context, Player player, Level level, BlockPos pos, BlockState oldState, Direction oldFront)
    {
        return oldState.setValue(HORIZONTAL_FACING, oldFront.getClockWise());
    }

    protected InteractionResult dismantleOrRotateMachine(UseOnContext context, Player player, Level level, BlockPos pos, BlockState state)
    {
        if (!level.isClientSide())
        {
            if (player.isShiftKeyDown())
            {
                Block.dropResources(state, level, pos, level.getBlockEntity(pos), player, context.getItemInHand());
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            }
            else if (allowsRotation(state))
            {
                Direction oldFront = state.getValue(HORIZONTAL_FACING);
                level.setBlockAndUpdate(pos, rotateBlockState(context, player, level, pos, state, oldFront));
            }

            return InteractionResult.CONSUME;
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        if (stack.getItem() instanceof MachineWrenchItem) return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}