package liedge.ltxindustries.item.tool;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.capability.energy.LimaEnergyUtil;
import liedge.limacore.util.LimaNetworkUtil;
import liedge.ltxindustries.item.ScrollModeSwitchItem;
import liedge.ltxindustries.lib.upgrades.effect.ModularTool;
import liedge.ltxindustries.lib.upgrades.effect.VeinMine;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrades;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import liedge.ltxindustries.registry.game.LTXIParticles;
import liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public abstract class ModularEnergyMiningItem extends BaseEnergyMiningItem implements ScrollModeSwitchItem
{
    private static Tool createDefaultTool(Tool.Rule denyRule, List<Tool.Rule> allowRules)
    {
        List<Tool.Rule> rules = new ObjectArrayList<>(allowRules.size() + 1);
        rules.add(denyRule);
        rules.addAll(allowRules);
        return new Tool(rules, 1f, 1);
    }

    private final Tool.Rule defaultDenyRule;
    private final List<Tool.Rule> defaultAllowRules;

    private ModularEnergyMiningItem(Properties properties, float poweredAttackDamage, float attackSpeed, Tool.Rule defaultDenyRule, List<Tool.Rule> defaultAllowRules)
    {
        super(properties
                .component(DataComponents.TOOL, createDefaultTool(defaultDenyRule, defaultAllowRules))
                .component(LTXIDataComponents.TOOL_SPEED, ToolSpeed.NORMAL),
                poweredAttackDamage, attackSpeedModifier(attackSpeed));
        this.defaultDenyRule = defaultDenyRule;
        this.defaultAllowRules = defaultAllowRules;
    }

    protected ModularEnergyMiningItem(Properties properties, float poweredAttackDamage, float attackSpeed, List<TagKey<Block>> tags)
    {
        this(properties, poweredAttackDamage, attackSpeed, Tool.Rule.deniesDrops(BlockTags.INCORRECT_FOR_DIAMOND_TOOL),
                tags.stream().map(key -> Tool.Rule.minesAndDrops(key, 1f)).toList());
    }

    public ToolSpeed getToolSpeed(ItemStack stack)
    {
        return stack.getOrDefault(LTXIDataComponents.TOOL_SPEED, ToolSpeed.NORMAL);
    }

    @Override
    public void switchItemMode(Player player, ItemStack stack, boolean forward)
    {
        ToolSpeed current = getToolSpeed(stack);
        ToolSpeed next = forward ? current.next() : current.previous();
        stack.set(LTXIDataComponents.TOOL_SPEED, next);
    }

    @Override
    public int getSwitchCooldown()
    {
        return 2;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity)
    {
        if (!hasEnergyForAction(stack)) return false;

        if (!level.isClientSide() && state.getDestroySpeed(level, pos) != 0f && miningEntity instanceof Player player)
        {
            int veinOps = Math.max(1, veinMine(player, (ServerLevel) level, stack, pos));

            if (!player.isCreative())
            {
                LimaEnergyUtil.extractWithoutLimit(getOrCreateEnergyStorage(stack), getEnergyUsage(stack) * veinOps, false);
            }
        }

        return true;
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player)
    {
        return getToolSpeed(player.getMainHandItem()) != ToolSpeed.OFF;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state)
    {
        Tool tool = stack.get(DataComponents.TOOL);
        ToolSpeed speed = getToolSpeed(stack);

        if (tool == null || !hasEnergyForAction(stack) || speed == ToolSpeed.OFF) return 0f;

        for (Tool.Rule rule : tool.rules())
        {
            if (state.is(rule.blocks())) return speed.getSpeed();
        }

        return 1f;
    }

    @Override
    public void onUpgradeRefresh(LootContext context, ItemStack stack, EquipmentUpgrades upgrades)
    {
        super.onUpgradeRefresh(context, stack, upgrades);

        List<ModularTool> effects = upgrades.listEffectStream(LTXIUpgradeEffectComponents.MODULAR_TOOL).toList();
        if (effects.isEmpty())
        {
            stack.set(DataComponents.TOOL, components().get(DataComponents.TOOL));
            return;
        }

        HolderSet<Block> denySet = null;
        List<HolderSet<Block>> effectiveSets = new ObjectArrayList<>();
        for (ModularTool data : effects)
        {
            if (denySet == null && data.limit().isPresent()) denySet = data.limit().get();

            data.effective().ifPresent(effectiveSets::add);
        }

        stack.set(DataComponents.TOOL, new Tool(createRules(denySet, effectiveSets), 1f, 1));
    }

    private List<Tool.Rule> createRules(@Nullable HolderSet<Block> denySet, Collection<HolderSet<Block>> effectiveSets)
    {
        List<Tool.Rule> rules = new ObjectArrayList<>();

        // Deny rule, if present, must be first in list
        if (denySet != null && !denySet.equals(HolderSet.empty()))
            rules.add(new Tool.Rule(denySet, Optional.empty(), Optional.of(false)));
        else if (denySet == null)
            rules.add(defaultDenyRule);

        // Compile effective sets, order doesn't matter
        for (HolderSet<Block> set : effectiveSets)
        {
            rules.add(new Tool.Rule(set, Optional.of(1f), Optional.of(true)));
        }
        rules.addAll(defaultAllowRules);

        return rules;
    }

    private int veinMine(Player player, ServerLevel level, ItemStack stack, BlockPos origin)
    {
        BlockState originState = level.getBlockState(origin);
        int maxBreak = player.isCreative() ? VeinMine.MAX_BLOCK_LIMIT : getEnergyStored(stack) / getEnergyUsage(stack);

        if (maxBreak <= 1) return 0;

        return getUpgrades(stack).listEffectStream(LTXIUpgradeEffectComponents.VEIN_MINE)
                .mapToInt(effect -> tryVeinMine(player, level, stack, effect, maxBreak, origin, originState))
                .filter(result -> result > 0)
                .findFirst()
                .orElse(0);
    }

    private int tryVeinMine(Player player, ServerLevel level, ItemStack stack, VeinMine effect, int maxBreak, BlockPos origin, BlockState originState)
    {
        List<BlockPos> positions = effect.apply(level, origin, originState);
        if (positions.size() < 2) return 0;

        // Destroy blocks and collect drops
        List<ItemEntity> drops = new ObjectArrayList<>();
        Vec3 dropsPos = Vec3.atCenterOf(origin);

        int ops = Math.min(positions.size(), maxBreak);
        for (int i = 0; i < ops; i++)
        {
            BlockPos pos = positions.get(i);
            BlockState state = level.getBlockState(pos);
            if (state.isAir()) continue;

            // Collect drops and experience with the player's tool
            BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
            FluidState fluidState = level.getFluidState(pos);
            Block.getDrops(state, level, pos, blockEntity, player, stack).forEach(drop ->
                    drops.add(new ItemEntity(level, dropsPos.x, dropsPos.y, dropsPos.z, drop)));
            level.setBlock(pos, fluidState.createLegacyBlock(), Block.UPDATE_ALL);

            // Send particles
            Vec3 particlePos = Vec3.atCenterOf(pos);
            Vec3 particleDelta = dropsPos.subtract(particlePos).scale(0.1f);
            LimaNetworkUtil.sendParticle(level, LTXIParticles.MINI_ELECTRIC_SPARK, LimaNetworkUtil.NORMAL_PARTICLE_DIST, particlePos, particleDelta);
        }

        BlockEntity originBE = originState.hasBlockEntity() ? level.getBlockEntity(origin) : null;
        CommonHooks.handleBlockDrops(level, origin, originState, originBE, drops, player, stack);

        return ops;
    }
}