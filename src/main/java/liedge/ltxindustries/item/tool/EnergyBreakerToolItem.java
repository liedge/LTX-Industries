package liedge.ltxindustries.item.tool;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.ltxindustries.lib.upgrades.effect.MiningRuleUpgradeEffect;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrades;
import liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public abstract class EnergyBreakerToolItem extends EnergyBaseToolItem
{
    private static final float DEFAULT_MINING_SPEED = 9f;

    private static ItemAttributeModifiers createAttributes(float attackSpeed)
    {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    private static Tool createDefaultTool(@Nullable Tool.Rule denyRule, List<Tool.Rule> allowRules)
    {
        if (denyRule == null) return new Tool(allowRules, 1f, 1);
        else
        {
            List<Tool.Rule> rules = new ObjectArrayList<>(allowRules.size() + 1);
            rules.add(denyRule);
            rules.addAll(allowRules);
            return new Tool(rules, 1f, 1);
        }
    }

    private final ItemAttributeModifiers defaultModifiers;
    private final @Nullable Tool.Rule defaultDenyRule;
    private final Function<Float, List<Tool.Rule>> allowRulesFunction;

    private EnergyBreakerToolItem(Properties properties, float poweredAttackDamage, ItemAttributeModifiers defaultModifiers, @Nullable Tool.Rule defaultDenyRule, Function<Float, List<Tool.Rule>> allowRulesFunction)
    {
        super(properties.component(DataComponents.TOOL, createDefaultTool(defaultDenyRule, allowRulesFunction.apply(DEFAULT_MINING_SPEED))), poweredAttackDamage);
        this.defaultModifiers = defaultModifiers;
        this.defaultDenyRule = defaultDenyRule;
        this.allowRulesFunction = allowRulesFunction;
    }

    protected EnergyBreakerToolItem(Properties properties, Function<Float, List<Tool.Rule>> allowRulesFunction)
    {
        this(properties, 0, ItemAttributeModifiers.EMPTY, null, allowRulesFunction);
    }

    protected EnergyBreakerToolItem(Properties properties, float poweredAttackDamage, float attackSpeed, @Nullable Tool.Rule defaultDenyRule, Function<Float, List<Tool.Rule>> allowRulesFunction)
    {
        this(properties, poweredAttackDamage, createAttributes(attackSpeed), defaultDenyRule, allowRulesFunction);
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack)
    {
        return defaultModifiers;
    }

    // Mining/world interaction functions
    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity)
    {
        Tool tool = stack.get(DataComponents.TOOL);
        if (tool == null || !hasEnergyForAction(stack)) return false;

        if (!level.isClientSide() && state.getDestroySpeed(level, pos) != 0f && miningEntity instanceof Player) consumeActionEnergy((Player) miningEntity, stack);

        return true;
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state)
    {
        return hasEnergyForAction(stack) && super.isCorrectToolForDrops(stack, state);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state)
    {
        return hasEnergyForAction(stack) ? super.getDestroySpeed(stack, state) : 0f;
    }

    // Dynamic mining functions
    private List<Tool.Rule> createRules(@Nullable HolderSet<Block> denySet, Collection<HolderSet<Block>> allowSets, float miningSpeed)
    {
        List<Tool.Rule> rules = new ObjectArrayList<>();

        // Deny rule, if present, must be first in list
        if (denySet != null)
        {
            if (!denySet.equals(HolderSet.empty()))
            {
                rules.add(new Tool.Rule(denySet, Optional.empty(), Optional.of(false)));
            }
        }
        else if (defaultDenyRule != null)
        {
            rules.add(defaultDenyRule);
        }

        // Build the custom allow rules
        for (HolderSet<Block> set : allowSets)
        {
            rules.add(new Tool.Rule(set, Optional.of(miningSpeed), Optional.of(true)));
        }

        // Always add default allow rules, will be ignored if a superior custom rule matches
        rules.addAll(allowRulesFunction.apply(miningSpeed));

        return rules;
    }

    @Override
    public void onUpgradeRefresh(LootContext context, ItemStack stack, EquipmentUpgrades upgrades)
    {
        super.onUpgradeRefresh(context, stack, upgrades);

        // Retrieve mining upgrade effects, revert to default tool if empty
        List<MiningRuleUpgradeEffect> effects = upgrades.listEffectStream(LTXIUpgradeEffectComponents.MINING_RULES).sorted().toList();
        if (effects.isEmpty())
        {
            stack.set(DataComponents.TOOL, Objects.requireNonNull(components().get(DataComponents.TOOL), "Missing default Tool component"));
            return;
        }

        // Rule variables
        HolderSet<Block> denySet = null;
        List<HolderSet<Block>> allowSets = new ObjectArrayList<>();
        float miningSpeed = DEFAULT_MINING_SPEED;

        // Get rule variables
        for (MiningRuleUpgradeEffect effect : effects)
        {
            // Find first denial set
            if (denySet == null && effect.deniedBlocks().isPresent()) denySet = effect.deniedBlocks().get();

            // Collect allow rules
            effect.effectiveBlocks().ifPresent(allowSets::add);

            // Capture the highest mining speed, or fallback to our default
            float speed = effect.miningSpeed().orElse(DEFAULT_MINING_SPEED);
            if (speed > miningSpeed) miningSpeed = speed;
        }

        // Finalize tool and apply data component
        Tool dynamicTool = new Tool(createRules(denySet, allowSets, miningSpeed), 1f, 1);
        stack.set(DataComponents.TOOL, dynamicTool);
    }
}