package liedge.limatech.item.tool;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limatech.lib.upgrades.effect.equipment.MiningRuleUpgradeEffect;
import liedge.limatech.lib.upgrades.equipment.EquipmentUpgrades;
import liedge.limatech.registry.game.LimaTechUpgradeEffectComponents;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootContext;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class EnergyDrillItem extends EnergyMiningToolItem
{
    private static final float DEFAULT_MINING_SPEED = 7f;

    private static Tool.Rule createDefaultAllowRule(TagKey<Block> blocks, float miningSpeed)
    {
        return Tool.Rule.minesAndDrops(blocks, miningSpeed);
    }

    private final Tool.Rule defaultDenyRule;

    public EnergyDrillItem(Properties properties, float poweredAttackDamage, float attackSpeed)
    {
        super(properties, poweredAttackDamage, attackSpeed);
        this.defaultDenyRule = Tool.Rule.deniesDrops(BlockTags.INCORRECT_FOR_IRON_TOOL);
    }

    @Override
    protected Set<ItemAbility> getAvailableAbilities()
    {
        return ItemAbilities.DEFAULT_PICKAXE_ACTIONS;
    }

    @Override
    public void onUpgradeRefresh(LootContext context, ItemStack stack, EquipmentUpgrades upgrades)
    {
        super.onUpgradeRefresh(context, stack, upgrades);

        // Rule variables
        HolderSet<Block> denySet = null;
        List<HolderSet<Block>> allowSets = new ObjectArrayList<>();
        float miningSpeed = DEFAULT_MINING_SPEED;

        // Get effects and iterate
        List<MiningRuleUpgradeEffect> effects = upgrades.effectFlatStream(LimaTechUpgradeEffectComponents.MINING_RULES).sorted().toList();
        for (MiningRuleUpgradeEffect effect : effects)
        {
            // Find first denial set
            if (denySet == null && effect.deniedBlocks().isPresent()) denySet = effect.deniedBlocks().get();

            // Collect allow rules (with the effect's mining speed if any)
            effect.effectiveBlocks().ifPresent(allowSets::add);

            // Capture the highest mining speed
            float speed = effect.miningSpeed().orElse(DEFAULT_MINING_SPEED);
            if (speed > miningSpeed) miningSpeed = speed;
        }

        // Build rules
        List<Tool.Rule> rules = new ObjectArrayList<>();

        // Deny rule
        if (denySet != null)
        {
            if (!denySet.equals(HolderSet.empty())) rules.add(new Tool.Rule(denySet, Optional.empty(), Optional.of(false)));
        }
        else
        {
            rules.add(defaultDenyRule);
        }

        // Allow rules - always add default rules afterward, will be safely ignored if superior rule is found first
        for (HolderSet<Block> set : allowSets)
        {
            rules.add(new Tool.Rule(set, Optional.of(miningSpeed), Optional.of(true)));
        }
        rules.add(createDefaultAllowRule(BlockTags.MINEABLE_WITH_PICKAXE, miningSpeed));
        rules.add(createDefaultAllowRule(BlockTags.MINEABLE_WITH_SHOVEL, miningSpeed));

        // Finalize tool & apply to item
        Tool drillTool = new Tool(rules, 1f, 1);
        stack.set(DataComponents.TOOL, drillTool);
    }
}