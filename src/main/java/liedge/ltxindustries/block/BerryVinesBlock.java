package liedge.ltxindustries.block;

import liedge.ltxindustries.registry.LTXILootTables;
import liedge.ltxindustries.registry.game.LTXIBlocks;
import liedge.ltxindustries.registry.game.LTXIItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CaveVinesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.ItemAbilities;
import org.jspecify.annotations.Nullable;

public class BerryVinesBlock extends CaveVinesBlock
{
    static InteractionResult pickBerries(Level level, BlockPos pos, BlockState state, Player player, @Nullable ItemInstance tool)
    {
        if (level instanceof ServerLevel)
        {
            Block.dropFromBlockInteractLootTable(
                    (ServerLevel) level,
                    LTXILootTables.BILEVINE_HARVEST,
                    state,
                    level.getBlockEntity(pos),
                    tool,
                    player,
                    (sl, stack) -> popResource(sl, pos, stack));

            level.playSound(null, pos, SoundEvents.CAVE_VINES_PICK_BERRIES, SoundSource.BLOCKS, 1f, Mth.randomBetween(level.getRandom(), 0.8f, 1.2f));
            BlockState newState = state.setValue(BERRIES, false);
            level.setBlock(pos, newState, Block.UPDATE_CLIENTS);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, newState));
        }

        return InteractionResult.SUCCESS;
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
        return LTXIItems.VITRIOL_BERRIES.toStack();
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult)
    {
        if (state.getValue(BERRIES))
            return pickBerries(level, pos, state, player, null);
        else
            return InteractionResult.PASS;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        if (itemStack.canPerformAction(ItemAbilities.SHEARS_HARVEST) && state.getValue(BERRIES))
            return pickBerries(level, pos, state, player, itemStack);
        else
            return InteractionResult.TRY_WITH_EMPTY_HAND;
    }
}