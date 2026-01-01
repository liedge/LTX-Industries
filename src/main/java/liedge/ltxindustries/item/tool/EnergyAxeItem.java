package liedge.ltxindustries.item.tool;

import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrade;
import liedge.ltxindustries.registry.bootstrap.LTXIEquipmentUpgrades;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
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

import java.util.List;
import java.util.Set;

public class EnergyAxeItem extends ModularEnergyMiningItem
{
    public EnergyAxeItem(Properties properties, float poweredAttackDamage, float attackSpeed)
    {
        super(properties, poweredAttackDamage, attackSpeed, List.of(BlockTags.MINEABLE_WITH_AXE));
    }

    @Override
    protected Set<ItemAbility> getAvailableAbilities()
    {
        return ItemAbilities.DEFAULT_AXE_ACTIONS;
    }

    @Override
    public @Nullable ResourceKey<EquipmentUpgrade> getDefaultUpgradeKey()
    {
        return LTXIEquipmentUpgrades.LTX_MELEE_DEFAULT;
    }

    @Override
    public boolean canUseToolOn(UseOnContext context, Level level, BlockPos pos, BlockState state, @Nullable Player player, ItemStack stack)
    {
        if (!super.canUseToolOn(context, level, pos, state, player, stack)) return false;
        if (player == null) return true;

        boolean willUseShield = context.getHand().equals(InteractionHand.MAIN_HAND) && player.getOffhandItem().canPerformAction(ItemAbilities.SHIELD_BLOCK) && !player.isSecondaryUseActive();
        return !willUseShield;
    }

    @Override
    protected InteractionResult useToolOn(UseOnContext context, Level level, BlockPos pos, BlockState state, @Nullable Player player, ItemStack stack)
    {
        BlockState state1 = transformBlock(context, level, pos, state, player);
        if (state1 == null) return InteractionResult.PASS;

        if (!level.isClientSide())
        {
            if (player instanceof ServerPlayer serverPlayer)
            {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, stack);
                consumeUsageEnergy(serverPlayer, stack);
            }

            level.setBlock(pos, state1, Block.UPDATE_ALL_IMMEDIATE);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, state1));
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private @Nullable BlockState transformBlock(UseOnContext context, Level level, BlockPos pos, BlockState state, @Nullable Player player)
    {
        BlockState state1 = state.getToolModifiedState(context, ItemAbilities.AXE_STRIP, false);
        if (state1 != null)
        {
            level.playSound(player, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS);
            return state1;
        }
        else if ((state1 = state.getToolModifiedState(context, ItemAbilities.AXE_SCRAPE, false)) != null)
        {
            level.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS);
            level.levelEvent(player, LevelEvent.PARTICLES_SCRAPE, pos, 0);
            return state1;
        }
        else if ((state1 = state.getToolModifiedState(context, ItemAbilities.AXE_WAX_OFF, false)) != null)
        {
            level.playSound(player, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS);
            level.levelEvent(player, LevelEvent.PARTICLES_WAX_OFF, pos, 0);
            return state1;
        }

        return null;
    }
}