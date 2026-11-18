package liedge.ltxindustries.block;

import liedge.ltxindustries.registry.game.LTXIBlocks;
import liedge.ltxindustries.registry.game.LTXIItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CaveVinesPlantBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.ItemAbilities;

public class BerryVinesPlantBlock extends CaveVinesPlantBlock
{
    public BerryVinesPlantBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    protected GrowingPlantHeadBlock getHeadBlock()
    {
        return LTXIBlocks.BILEVINE.get();
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData)
    {
        return LTXIItems.VITRIOL_BERRIES.toStack();
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult)
    {
        if (state.getValue(BERRIES))
            return BerryVinesBlock.pickBerries(level, pos, state, player, null);
        else
            return InteractionResult.PASS;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        if (itemStack.canPerformAction(ItemAbilities.SHEARS_HARVEST) && state.getValue(BERRIES))
            return BerryVinesBlock.pickBerries(level, pos, state, player, itemStack);
        else
            return InteractionResult.TRY_WITH_EMPTY_HAND;
    }
}