package liedge.ltxindustries.item.tool;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseEnergyMiningItem extends EnergyBaseToolItem
{
    static ItemAttributeModifiers attackSpeedModifier(float attackSpeed)
    {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    private final ItemAttributeModifiers defaultModifiers;

    protected BaseEnergyMiningItem(Properties properties, float poweredAttackDamage, ItemAttributeModifiers defaultModifiers)
    {
        super(properties, poweredAttackDamage);
        this.defaultModifiers = defaultModifiers;
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack)
    {
        return defaultModifiers;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity)
    {
        if (!hasEnergyForAction(stack)) return false;

        if (!level.isClientSide() && state.getDestroySpeed(level, pos) != 0 && miningEntity instanceof Player player)
        {
            consumeActionEnergy(player, stack);
        }

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
}