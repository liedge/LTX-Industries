package liedge.ltxindustries.block;

import liedge.ltxindustries.registry.game.LTXIBlocks;
import liedge.ltxindustries.registry.game.LTXIItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CaveVinesPlantBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

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
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state)
    {
        return new ItemStack(LTXIItems.VITRIOL_BERRIES.get());
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult)
    {
        return BerryVinesBlock.pickBerries(level, pos, state, player);
    }
}