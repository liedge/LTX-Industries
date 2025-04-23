package liedge.limatech.item.tool;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
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

import java.util.List;

public abstract class EnergyMiningToolItem extends EnergyBaseToolItem
{
    private static ItemAttributeModifiers createAttributes(float attackSpeed)
    {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    protected static Tool createDefaultFixedTool(TagKey<Block> blocks)
    {
        List<Tool.Rule> rules = List.of(Tool.Rule.deniesDrops(BlockTags.INCORRECT_FOR_DIAMOND_TOOL), Tool.Rule.minesAndDrops(blocks, 9f));
        return new Tool(rules, 1f, 1);
    }

    private final ItemAttributeModifiers defaultModifiers;

    protected EnergyMiningToolItem(Properties properties, float poweredAttackDamage, ItemAttributeModifiers defaultModifiers)
    {
        super(properties, poweredAttackDamage);
        this.defaultModifiers = defaultModifiers;
    }

    protected EnergyMiningToolItem(Properties properties, float poweredAttackDamage, float attackSpeed)
    {
        this(properties, poweredAttackDamage, createAttributes(attackSpeed));
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

    // Damage functions
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker)
    {
        return hasEnergyForAction(stack);
    }
}