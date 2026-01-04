package liedge.ltxindustries.item.tool;

import liedge.ltxindustries.LTXITags;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.IShearable;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class EnergyShearsItem extends BaseEnergyMiningItem
{
    public EnergyShearsItem(Properties properties)
    {
        super(properties.component(DataComponents.TOOL, ShearsItem.createToolProperties()), 0f, ItemAttributeModifiers.EMPTY);
    }

    @Override
    protected Set<ItemAbility> getAvailableAbilities()
    {
        return ItemAbilities.DEFAULT_SHEARS_ACTIONS;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity)
    {
        return super.mineBlock(stack, level, state, pos, miningEntity) && state.is(LTXITags.Blocks.SHEARS_HARVESTABLE);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand)
    {
        if (!hasEnergyForAction(stack)) return InteractionResult.PASS;

        if (interactionTarget instanceof IShearable shearable)
        {
            Level level = interactionTarget.level();
            BlockPos pos = interactionTarget.blockPosition();

            if (shearable.isShearable(player, stack, level, pos))
            {
                List<ItemStack> drops = shearable.onSheared(player, stack, level, pos);

                if (!level.isClientSide())
                {
                    for (ItemStack drop : drops)
                    {
                        shearable.spawnShearedDrop(level, pos, drop);
                    }

                    consumeEnergyAction(player, stack);
                }

                interactionTarget.gameEvent(GameEvent.SHEAR, player);

                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    protected InteractionResult useToolOn(UseOnContext context, Level level, BlockPos pos, BlockState state, @Nullable Player player, ItemStack stack)
    {
        BlockState state1 = state.getToolModifiedState(context, ItemAbilities.SHEARS_TRIM, false);
        if (state1 != null)
        {
            if (player instanceof ServerPlayer serverPlayer)
            {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, stack);
                consumeEnergyAction(serverPlayer, stack);
            }

            level.setBlockAndUpdate(pos, state1);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, state1));

            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        return InteractionResult.PASS;
    }
}