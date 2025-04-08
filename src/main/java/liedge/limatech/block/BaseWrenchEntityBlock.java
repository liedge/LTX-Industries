package liedge.limatech.block;

import liedge.limacore.block.LimaEntityBlock;
import liedge.limatech.item.LimaTechItemAbilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public abstract class BaseWrenchEntityBlock extends LimaEntityBlock
{
    protected BaseWrenchEntityBlock(Properties properties)
    {
        super(properties);
    }

    protected @Nullable BlockState handleWrenchRotation(Level level, BlockPos pos, BlockState state)
    {
        Direction oldFront = state.getValue(HORIZONTAL_FACING);
        return state.setValue(HORIZONTAL_FACING, oldFront.getClockWise());
    }

    protected BlockState handleWrenchDismantle(Level level, BlockPos pos, BlockState state, @Nullable Player player, ItemStack tool, boolean simulate)
    {
        if (!level.isClientSide() && !simulate) Block.dropResources(state, level, pos, level.getBlockEntity(pos), player, tool);
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate)
    {
        ItemStack stack = context.getItemInHand();
        if (!stack.canPerformAction(itemAbility)) return null;

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        if (itemAbility == LimaTechItemAbilities.WRENCH_ROTATE)
        {
            return handleWrenchRotation(level, pos, state);
        }
        else if (itemAbility == LimaTechItemAbilities.WRENCH_DISMANTLE)
        {
            return handleWrenchDismantle(level, pos, state, context.getPlayer(), stack, simulate);
        }

        return null;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        if (stack.canPerformAction(LimaTechItemAbilities.WRENCH_ROTATE) || stack.canPerformAction(LimaTechItemAbilities.WRENCH_DISMANTLE)) return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}