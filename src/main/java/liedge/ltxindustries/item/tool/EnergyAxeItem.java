package liedge.ltxindustries.item.tool;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import liedge.limacore.capability.energy.LimaEnergyUtil;
import liedge.limacore.util.LimaNetworkUtil;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrade;
import liedge.ltxindustries.registry.bootstrap.LTXIEquipmentUpgrades;
import liedge.ltxindustries.registry.game.LTXIParticles;
import liedge.ltxindustries.util.config.LTXIServerConfig;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class EnergyAxeItem extends EnergyBreakerToolItem
{
    private static final int MAX_LOGS_TO_BREAK = 128;

    public EnergyAxeItem(Properties properties, float attackDamage, float attackSpeed)
    {
        super(properties, attackDamage, attackSpeed, Tool.Rule.deniesDrops(BlockTags.INCORRECT_FOR_DIAMOND_TOOL),
                speed -> List.of(Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_AXE, speed)));
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
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity)
    {
        Tool tool = stack.get(DataComponents.TOOL);
        if (tool == null || !hasEnergyForAction(stack)) return false; // Shouldn't happen since mining speed is 0 if not enough energy, but we need to double-check. If we don't have enough energy for even 1 operation, cancel

        if (!level.isClientSide() && state.getDestroySpeed(level, pos) != 0f && miningEntity instanceof Player player)
        {
            if (!chopTree(stack, (ServerLevel) level, state, pos, player))
            {
                consumeActionEnergy(player, stack); // Only consume 1 energy action if not a tree or tree chop failed
            }
        }

        return true;
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
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer) player, pos, stack);
                consumeActionEnergy(serverPlayer, stack);
            }

            level.setBlock(pos, state1, Block.UPDATE_ALL_IMMEDIATE);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, state1));
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private boolean chopTree(ItemStack stack, ServerLevel level, BlockState originState, BlockPos originPos, Player player)
    {
        if (!originState.is(BlockTags.LOGS)) return false; // Not a log, don't even check

        Set<BlockPos> checked = new ObjectOpenHashSet<>();
        Set<BlockPos> toBreak = new ObjectOpenHashSet<>();
        Queue<BlockPos> toCheck = new ArrayDeque<>();

        toBreak.add(originPos); // Always break
        toCheck.add(originPos);
        checked.add(originPos);

        // Scan the tree structure
        final int maxLogs = Math.min(MAX_LOGS_TO_BREAK, getEnergyStored(stack) / getEnergyUsage(stack));
        boolean foundLeaves = false;
        while (!toCheck.isEmpty() && toBreak.size() <= maxLogs)
        {
            BlockPos pos = toCheck.poll();
            for (BlockPos visiting : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1)))
            {
                if (visiting.equals(pos) || checked.contains(visiting)) continue;
                BlockPos visitingImmutable = visiting.immutable();
                BlockState visitingState = level.getBlockState(visitingImmutable);

                checked.add(visitingImmutable);
                if (visitingState.is(BlockTags.LOGS))
                {
                    toBreak.add(visitingImmutable);
                    toCheck.add(visitingImmutable);
                }
                else if (!foundLeaves && visitingState.is(BlockTags.LEAVES))
                {
                    foundLeaves = visitingState.hasProperty(BlockStateProperties.PERSISTENT) && !visitingState.getValue(BlockStateProperties.PERSISTENT);
                }
            }
        }
        if (!foundLeaves && !LTXIServerConfig.AXE_ALWAYS_CHOPS_LOGS.getAsBoolean()) return false; // Cancel tree chop if no tree leaves found and the bypass config option isn't on.

        // Extract energy. Fail to chop if we don't have enough energy for some reason
        int totalEnergyUsage = getEnergyUsage(stack) * toBreak.size();
        if (getEnergyStored(stack) < totalEnergyUsage) return false;
        LimaEnergyUtil.extractWithoutLimit(getOrCreateEnergyStorage(stack), totalEnergyUsage, false);

        // Destroy blocks, collect drops and experience
        List<ItemEntity> drops = new ObjectArrayList<>();
        Vec3 dropsPos = Vec3.atCenterOf(originPos);

        for (BlockPos pos : toBreak)
        {
            BlockState state = level.getBlockState(pos);
            if (state.isAir()) continue;
            BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;

            // We need to pass the player's axe ItemStack to Block.getDrops(), not done by level.destroyBlock().
            // Otherwise, equipment upgrades will not work correctly.
            FluidState fluidState = level.getFluidState(pos);

            Block.getDrops(state, level, pos, blockEntity, player, stack).forEach(o -> drops.add(new ItemEntity(level, dropsPos.x, dropsPos.y, dropsPos.z, o)));
            level.setBlock(pos, fluidState.createLegacyBlock(), Block.UPDATE_ALL);

            // Spawn electric sparks that drift towards origin
            Vec3 particlePos = Vec3.atCenterOf(pos);
            Vec3 particleDelta = dropsPos.subtract(particlePos).scale(0.1f);
            LimaNetworkUtil.sendParticle(level, LTXIParticles.MINI_ELECTRIC_SPARK, LimaNetworkUtil.NORMAL_PARTICLE_DIST, particlePos, particleDelta);
        }

        // Fire drops event early and batch all drops into one event. Will not be fired in ServerPlayerGameMode after mineBlock() as origin is already air.
        BlockEntity originBE = originState.hasBlockEntity() ? level.getBlockEntity(originPos) : null;
        CommonHooks.handleBlockDrops(level, originPos, originState, originBE, drops, player, stack);

        return true;
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