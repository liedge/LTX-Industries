package liedge.limatech.item.tool;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class EnergyShovelItem extends EnergyMiningToolItem
{
    public EnergyShovelItem(Properties properties, float attackDamage, float attackSpeed)
    {
        super(properties.component(DataComponents.TOOL, createDefaultFixedTool(BlockTags.MINEABLE_WITH_SHOVEL)), attackDamage, attackSpeed);
    }

    @Override
    protected Set<ItemAbility> getAvailableAbilities()
    {
        return ItemAbilities.DEFAULT_SHOVEL_ACTIONS;
    }

    @Override
    public boolean canUseToolOn(UseOnContext context, Level level, BlockPos pos, BlockState state, @Nullable Player player, ItemStack stack)
    {
        return super.canUseToolOn(context, level, pos, state, player, stack) && context.getClickedFace() != Direction.DOWN;
    }

    @Override
    protected InteractionResult useToolOn(UseOnContext context, Level level, BlockPos pos, BlockState state, @Nullable Player player, ItemStack stack)
    {
        BlockState state1 = state.getToolModifiedState(context, ItemAbilities.SHOVEL_FLATTEN, false);
        BlockState state2;

        if (state1 != null && level.getBlockState(pos.above()).isAir())
        {
            level.playSound(player, pos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS);
            state2 = state1;
        } else if ((state2 = state.getToolModifiedState(context, ItemAbilities.SHOVEL_DOUSE, false)) != null)
        {
            if (!level.isClientSide()) level.levelEvent(null, LevelEvent.SOUND_EXTINGUISH_FIRE, pos, 0);
        }

        if (state2 != null)
        {
            if (!level.isClientSide())
            {
                level.setBlock(pos, state2, Block.UPDATE_ALL_IMMEDIATE);
                level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, state2));
                if (player != null) consumeActionEnergy(player, stack);
            }

            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        return InteractionResult.PASS;
    }
}