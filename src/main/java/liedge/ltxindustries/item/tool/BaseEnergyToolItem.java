package liedge.ltxindustries.item.tool;

import liedge.limacore.util.LimaLootUtil;
import liedge.ltxindustries.data.LTXIReloadListeners;
import liedge.ltxindustries.item.EnergyEquipmentItem;
import liedge.ltxindustries.util.config.LTXIServerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Weapon;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public abstract class BaseEnergyToolItem extends EnergyEquipmentItem
{
    private final float poweredAttackDamage;

    protected BaseEnergyToolItem(Properties properties, float poweredAttackDamage)
    {
        super(properties);
        this.poweredAttackDamage = poweredAttackDamage;
    }

    protected abstract Set<ItemAbility> getAvailableAbilities();

    // Energy stuff
    @Override
    public int getBaseEnergyCapacity(ItemInstance stack)
    {
        return LTXIServerConfig.TOOLS_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyUsage(ItemInstance stack)
    {
        return LTXIServerConfig.TOOLS_ENERGY_PER_ACTION.getAsInt();
    }

    // Damage/attack functions

    @Override
    public float getAttackDamageBonus(Entity target, float damage, DamageSource damageSource)
    {
        final float baseDamage = poweredAttackDamage;

        if (baseDamage > 0 && target.level() instanceof ServerLevel level)
        {
            ItemStack stack = damageSource.getWeaponItem();
            if (stack != null && stack.is(this) && hasEnergyForAction(stack))
            {
                LootContext context = LimaLootUtil.entityLootContext(level, target, damageSource);
                double upgradedDamage = getUpgradedDamage(getUpgrades(stack), context, baseDamage);
                return (float) LTXIReloadListeners.equipmentDamageModifiers().apply(stack, context, baseDamage, upgradedDamage);
            }
        }

        return 0f;
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker)
    {
        if (!attacker.level().isClientSide() && poweredAttackDamage > 0)
        {
            Weapon weapon = stack.get(DataComponents.WEAPON);
            if (weapon != null)
            {
                consumeEnergyActions(attacker, stack, weapon.itemDamagePerAttack());
            }
        }
    }

    // Tool actions
    public boolean canUseToolOn(UseOnContext context, Level level, BlockPos pos, BlockState state, @Nullable Player player, ItemStack stack)
    {
        return hasEnergyForAction(stack);
    }

    protected InteractionResult useToolOn(UseOnContext context, Level level, BlockPos pos, BlockState state, @Nullable Player player, ItemStack stack)
    {
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();

        return canUseToolOn(context, level, pos, state, player, stack) ? useToolOn(context, level, pos, state, player, stack) : InteractionResult.PASS;
    }

    @Override
    public boolean canPerformAction(ItemInstance stack, ItemAbility itemAbility)
    {
        return hasEnergyForAction(stack) && getAvailableAbilities().contains(itemAbility);
    }

    // Overridden static properties
    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack)
    {
        return !ItemStack.isSameItem(oldStack, newStack);
    }
}