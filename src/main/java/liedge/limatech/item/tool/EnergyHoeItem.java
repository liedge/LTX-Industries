package liedge.limatech.item.tool;

import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class EnergyHoeItem extends EnergyMiningToolItem
{
    public EnergyHoeItem(Properties properties, float attackDamage, float attackSpeed)
    {
        super(properties.component(DataComponents.TOOL, createDefaultFixedTool(BlockTags.MINEABLE_WITH_HOE)), attackDamage, attackSpeed);
    }

    @Override
    protected Set<ItemAbility> getAvailableAbilities()
    {
        return ItemAbilities.DEFAULT_HOE_ACTIONS;
    }

    @Override
    protected InteractionResult useToolOn(UseOnContext context, Level level, BlockPos pos, BlockState state, @Nullable Player player, ItemStack stack)
    {
        BlockState state1 = state.getToolModifiedState(context, ItemAbilities.HOE_TILL, false);
        if (state1 == null) return InteractionResult.PASS;

        level.playSound(player, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS);

        if (!level.isClientSide())
        {
            level.setBlock(pos, state1, Block.UPDATE_ALL_IMMEDIATE);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, state1));

            if (player != null) consumeActionEnergy(player, stack);
        }

        return InteractionResult.sidedSuccess(!level.isClientSide());
    }
}