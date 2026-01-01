package liedge.ltxindustries.item.tool;

import liedge.ltxindustries.LTXITags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class EnergyHoeItem extends ModularEnergyMiningItem
{
    public EnergyHoeItem(Properties properties, float poweredAttackDamage, float attackSpeed)
    {
        super(properties, poweredAttackDamage, attackSpeed, List.of(BlockTags.MINEABLE_WITH_HOE));
    }

    @Override
    protected Set<ItemAbility> getAvailableAbilities()
    {
        return ItemAbilities.DEFAULT_HOE_ACTIONS;
    }

    @Override
    protected InteractionResult useToolOn(UseOnContext context, Level level, BlockPos pos, BlockState state, @Nullable Player player, ItemStack stack)
    {
        // 'Tend' to crops
        if (state.is(LTXITags.Blocks.LTX_HOE_BOOSTABLE) && player != null)
        {
            player.startUsingItem(context.getHand());
            return InteractionResult.CONSUME;
        }

        // Till blocks
        BlockState state1 = state.getToolModifiedState(context, ItemAbilities.HOE_TILL, false);
        if (state1 == null) return InteractionResult.PASS;

        level.playSound(player, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS);

        if (!level.isClientSide())
        {
            level.setBlock(pos, state1, Block.UPDATE_ALL_IMMEDIATE);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, state1));

            if (player != null) consumeUsageEnergy(player, stack);
        }

        return InteractionResult.sidedSuccess(!level.isClientSide());
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack)
    {
        return UseAnim.BRUSH;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity)
    {
        return 72000;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration)
    {
        if (livingEntity instanceof Player player && player.level() instanceof ServerLevel serverLevel)
        {
            // Use every 5 ticks only
            if (remainingUseDuration % 5 == 0)
            {
                // Only proceed if we have enough energy
                if (hasEnergyForAction(stack))
                {
                    HitResult result = ProjectileUtil.getHitResultOnViewVector(player, e -> !e.isSpectator() && e.isPickable(), player.blockInteractionRange());
                    if (result instanceof BlockHitResult blockHitResult && blockHitResult.getType() == HitResult.Type.BLOCK)
                    {
                        final BlockPos origin = blockHitResult.getBlockPos();
                        boolean boostedBlock = false;

                        // Iterate a 3x3x3 cube around the player's aimed block
                        Iterable<BlockPos> positions = BlockPos.betweenClosed(origin.getX() - 1, origin.getY() - 1, origin.getZ() - 1, origin.getX() + 1, origin.getY() + 1, origin.getZ() + 1);
                        for (BlockPos pos : positions)
                        {
                            BlockState state = level.getBlockState(pos);
                            if (state.is(LTXITags.Blocks.LTX_HOE_BOOSTABLE))
                            {
                                // Apply 3 random ticks to boostable blocks
                                for (int i = 0; i < 3; i++)
                                {
                                    state.randomTick(serverLevel, pos, serverLevel.random);
                                }

                                boostedBlock = true;
                            }
                        }

                        // Consume energy & play sound only if crop was found
                        if (boostedBlock)
                        {
                            consumeUsageEnergy(player, stack);
                            level.playSound(null, origin, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1f, 1f);
                        }
                    }
                }
                else // Otherwise, stop using item
                {
                    player.stopUsingItem();
                }
            }
        }
    }
}