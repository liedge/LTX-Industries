package liedge.ltxindustries.item.tool;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class EnergyLighterItem extends BaseEnergyToolItem
{
    public EnergyLighterItem(Properties properties)
    {
        super(properties, 0f);
    }

    @Override
    protected Set<ItemAbility> getAvailableAbilities()
    {
        return ItemAbilities.DEFAULT_FLINT_ACTIONS;
    }

    @Override
    protected InteractionResult useToolOn(UseOnContext context, Level level, BlockPos pos, BlockState state, @Nullable Player player, ItemStack stack)
    {
        BlockState state1 = state.getToolModifiedState(context, ItemAbilities.FIRESTARTER_LIGHT, false);
        if (state1 == null)
        {
            BlockPos pos1 = pos.relative(context.getClickedFace());
            if (BaseFireBlock.canBePlacedAt(level, pos1, context.getHorizontalDirection()))
            {
                level.playSound(player, pos1, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1f, level.getRandom().nextFloat() * 0.4f + 0.8f);
                BlockState state2 = BaseFireBlock.getState(level, pos1);
                level.setBlock(pos1, state2, Block.UPDATE_ALL_IMMEDIATE);
                level.gameEvent(player, GameEvent.BLOCK_PLACE, pos);

                if (player instanceof ServerPlayer serverPlayer)
                {
                    CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, pos1, stack);
                    consumeEnergyAction(serverPlayer, stack);
                }

                return InteractionResult.sidedSuccess(level.isClientSide());
            }
            else
            {
                return InteractionResult.FAIL;
            }
        }
        else
        {
            level.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1f, level.getRandom().nextFloat() * 0.4f + 0.8f);
            level.setBlock(pos, state1, Block.UPDATE_ALL_IMMEDIATE);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);

            if (!level.isClientSide() && player != null) consumeEnergyAction(player, stack);

            return InteractionResult.sidedSuccess(level.isClientSide());
        }
    }
}