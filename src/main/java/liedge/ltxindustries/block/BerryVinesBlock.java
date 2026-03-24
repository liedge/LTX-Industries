package liedge.ltxindustries.block;

import liedge.ltxindustries.registry.LTXILootTables;
import liedge.ltxindustries.registry.game.LTXIBlocks;
import liedge.ltxindustries.registry.game.LTXIItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CaveVinesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

public class BerryVinesBlock extends CaveVinesBlock
{
    static InteractionResult pickBerries(Level level, BlockPos pos, BlockState state, Player player)
    {
        if (state.getValue(BERRIES))
        {
            if (level instanceof ServerLevel serverLevel)
            {
                Block.dropFromBlockInteractLootTable(
                        serverLevel,
                        LTXILootTables.BILEVINE_HARVEST,
                        state,
                        level.getBlockEntity(pos),
                        null,
                        player,
                        (sl, stack) -> popResource(sl, pos, stack));
            }

            level.playSound(null, pos, SoundEvents.CAVE_VINES_PICK_BERRIES, SoundSource.BLOCKS, 1f, Mth.randomBetween(level.random, 0.8f, 1.2f));
            BlockState newState = state.setValue(BERRIES, false);
            level.setBlock(pos, newState, Block.UPDATE_CLIENTS);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, newState));

            return InteractionResult.SUCCESS;
        }
        else
        {
            return InteractionResult.PASS;
        }
    }

    public BerryVinesBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    protected Block getBodyBlock()
    {
        return LTXIBlocks.BILEVINE_PLANT.get();
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData)
    {
        return new ItemStack(LTXIItems.VITRIOL_BERRIES.get());
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult)
    {
        return pickBerries(level, pos, state, player);
    }
}