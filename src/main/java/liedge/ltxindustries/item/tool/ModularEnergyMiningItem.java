package liedge.ltxindustries.item.tool;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.ltxindustries.item.ScrollModeSwitchItem;
import liedge.ltxindustries.lib.upgrades.effect.ModularTool;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrades;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
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

    // Dynamic mining functions
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
}