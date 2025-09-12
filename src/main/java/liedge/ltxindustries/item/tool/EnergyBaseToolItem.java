package liedge.ltxindustries.item.tool;

import liedge.limacore.capability.energy.LimaEnergyUtil;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.item.LimaCreativeTabFillerItem;
import liedge.limacore.registry.game.LimaCoreDataComponents;
import liedge.limacore.util.LimaMathUtil;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.item.EnergyHolderItem;
import liedge.ltxindustries.item.TooltipShiftHintItem;
import liedge.ltxindustries.item.UpgradableEquipmentItem;
import liedge.ltxindustries.util.config.LTXIServerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public abstract class EnergyBaseToolItem extends Item implements EnergyHolderItem, UpgradableEquipmentItem, TooltipShiftHintItem, LimaCreativeTabFillerItem
{
    private final float poweredAttackDamage;

    protected EnergyBaseToolItem(Properties properties, float poweredAttackDamage)
    {
        super(properties);
        this.poweredAttackDamage = poweredAttackDamage;
    }

    protected abstract Set<ItemAbility> getAvailableAbilities();

    // Helpers
    public void consumeActionEnergy(Player player, ItemStack stack)
    {
        if (!player.isCreative()) LimaEnergyUtil.extractWithoutLimit(getOrCreateEnergyStorage(stack), getEnergyUsage(stack), false);
    }

    public boolean hasEnergyForAction(ItemStack stack)
    {
        return getEnergyStored(stack) >= getEnergyUsage(stack);
    }

    // Energy stuff
    @Override
    public int getBaseEnergyCapacity(ItemStack stack)
    {
        return LTXIServerConfig.TOOLS_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyTransferRate(ItemStack stack)
    {
        return getBaseEnergyCapacity(stack) / 40;
    }

    @Override
    public int getBaseEnergyUsage(ItemStack stack)
    {
        return LTXIServerConfig.TOOLS_ENERGY_PER_ACTION.getAsInt();
    }

    // Damage/attack functions
    public float getPoweredAttackDamage()
    {
        return poweredAttackDamage;
    }

    @Override
    public float getAttackDamageBonus(Entity target, float damage, DamageSource damageSource)
    {
        // Living attackers only
        if (damageSource.getDirectEntity() instanceof LivingEntity attacker && !attacker.level().isClientSide())
        {
            ItemStack stack = attacker.getWeaponItem();
            if (stack.is(this) && hasEnergyForAction(stack))
            {
                return getUpgradedDamage((ServerLevel) attacker.level(), getUpgrades(stack), target, damageSource, getPoweredAttackDamage());
            }
        }

        return 0f;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker)
    {
        return poweredAttackDamage > 0 && hasEnergyForAction(stack);
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker)
    {
        if (poweredAttackDamage > 0 && !attacker.level().isClientSide() && attacker instanceof Player)
            consumeActionEnergy((Player) attacker, stack);
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
    public boolean canPerformAction(ItemStack stack, ItemAbility ability)
    {
        return hasEnergyForAction(stack) && getAvailableAbilities().contains(ability);
    }

    // Overridden static properties
    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack)
    {
        return !ItemStack.isSameItem(oldStack, newStack);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        return slotChanged;
    }

    @Override
    public boolean isFoil(ItemStack stack)
    {
        return false;
    }

    @Override
    public boolean isEnchantable(ItemStack stack)
    {
        return false;
    }

    @Override
    public boolean isBarVisible(ItemStack stack)
    {
        return true;
    }

    @Override
    public int getBarColor(ItemStack stack)
    {
        return LTXIConstants.REM_BLUE.argb32();
    }

    @Override
    public int getBarWidth(ItemStack stack)
    {
        float fill = Math.min(LimaMathUtil.divideFloat(getEnergyStored(stack), getEnergyCapacity(stack)), 1f);
        return Math.round(13f * fill);
    }

    @Override
    public void appendTooltipHintComponents(@Nullable Level level, ItemStack stack, TooltipLineConsumer consumer)
    {
        appendEquipmentEnergyTooltip(consumer, stack);
    }

    // Creative tab
    @Override
    public boolean addDefaultInstanceToCreativeTab(ResourceLocation tabId)
    {
        return false;
    }

    @Override
    public void addAdditionalToCreativeTab(ResourceLocation tabId, CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output, CreativeModeTab.TabVisibility tabVisibility)
    {
        ItemStack stack = createStackWithDefaultUpgrades(parameters.holders());
        stack.set(LimaCoreDataComponents.ENERGY, getBaseEnergyCapacity(stack));
        output.accept(stack, tabVisibility);
    }
}