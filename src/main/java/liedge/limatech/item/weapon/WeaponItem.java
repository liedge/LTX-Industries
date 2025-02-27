package liedge.limatech.item.weapon;

import com.mojang.serialization.Codec;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.item.LimaCreativeTabFillerItem;
import liedge.limacore.lib.Translatable;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limatech.entity.LimaTechProjectile;
import liedge.limatech.item.EnergyHolderItem;
import liedge.limatech.item.TooltipShiftHintItem;
import liedge.limatech.item.UpgradableEquipmentItem;
import liedge.limatech.lib.upgrades.effect.AmmoSourceUpgradeEffect;
import liedge.limatech.lib.upgrades.equipment.EquipmentUpgrades;
import liedge.limatech.lib.weapons.AbstractWeaponControls;
import liedge.limatech.lib.weapons.GlobalWeaponDamageModifiers;
import liedge.limatech.lib.weapons.WeaponAmmoSource;
import liedge.limatech.lib.weapons.WeaponDamageSource;
import liedge.limatech.registry.LimaTechAttachmentTypes;
import liedge.limatech.registry.LimaTechGameEvents;
import liedge.limatech.registry.LimaTechUpgradeEffectComponents;
import liedge.limatech.util.LimaTechTooltipUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ItemAbilities;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

import static liedge.limacore.capability.energy.LimaEnergyUtil.formatEnergyWithSuffix;
import static liedge.limatech.LimaTech.RESOURCES;
import static liedge.limatech.LimaTechConstants.*;
import static liedge.limatech.registry.LimaTechDataComponents.*;

public abstract class WeaponItem extends Item implements EnergyHolderItem, LimaCreativeTabFillerItem, TooltipShiftHintItem, UpgradableEquipmentItem
{
    public static final Codec<WeaponItem> CODEC = LimaCoreCodecs.classCastRegistryCodec(BuiltInRegistries.ITEM, WeaponItem.class);
    public static final StreamCodec<RegistryFriendlyByteBuf, WeaponItem> STREAM_CODEC = LimaStreamCodecs.classCastRegistryStreamCodec(Registries.ITEM, WeaponItem.class);
    public static final Translatable AMMO_LOADED_TOOLTIP = RESOURCES.translationHolder("tooltip.{}.ammo_loaded");
    public static final Translatable ENERGY_AMMO_COST_TOOLTIP = RESOURCES.translationHolder("tooltip.{}.energy_ammo_cost");

    public static WeaponAmmoSource getAmmoSourceFromItem(ItemStack stack)
    {
        return stack.getOrDefault(WEAPON_AMMO_SOURCE, WeaponAmmoSource.NORMAL);
    }

    protected WeaponItem(Properties properties)
    {
        super(properties);
    }

    //#region Weapon user events
    public abstract void triggerPressed(ItemStack heldItem, Player player, AbstractWeaponControls input);

    // TODO: Re-introduce later if necessary, kept for reference
    //public abstract void triggerRelease(ItemStack heldItem, Player player, AbstractWeaponControls input, boolean releasedByPlayer);

    public abstract boolean canContinueHoldingTrigger(ItemStack heldItem, Player player, AbstractWeaponControls input);

    public void triggerHoldingTick(ItemStack heldItem, Player player, AbstractWeaponControls input) {}

    public void onStartedHoldingTrigger(ItemStack heldItem, Player player, AbstractWeaponControls input) {}

    public void onStoppedHoldingTrigger(ItemStack heldItem, Player player, AbstractWeaponControls input, boolean releasedByPlayer, boolean serverAction) {}

    public abstract void weaponFired(ItemStack heldItem, Player player, Level level, AbstractWeaponControls controls);

    public void onPlayerKill(WeaponDamageSource damageSource, Player player, LivingEntity targetEntity)
    {
        ItemStack heldItem = player.getMainHandItem();
        if (player.level() instanceof ServerLevel serverLevel)
        {
            getUpgrades(heldItem).forEachListEffect(LimaTechUpgradeEffectComponents.WEAPON_KILL, (effect, rank) -> effect.activateEquipmentEffect(serverLevel, rank, player, heldItem, targetEntity, damageSource));
        }
    }
    //#endregion

    //#region Weapon properties/behavior
    public boolean canFocusReticle(ItemStack heldItem, Player player, AbstractWeaponControls controls)
    {
        return controls.canStartShootingWeapon(heldItem, player, this);
    }

    public int getAmmoLoaded(ItemStack stack)
    {
        return stack.getOrDefault(WEAPON_AMMO, 0);
    }

    public void setAmmoLoaded(ItemStack stack, int newAmmo)
    {
        stack.set(WEAPON_AMMO, Mth.clamp(newAmmo, 0, getAmmoCapacity(stack)));
    }

    @Override
    public boolean supportsEnergyStorage(ItemStack stack)
    {
        return getAmmoSourceFromItem(stack) == WeaponAmmoSource.COMMON_ENERGY_UNIT;
    }

    @Override
    public int getEnergyTransferRate(ItemStack stack)
    {
        return getEnergyCapacity(stack) / 10;
    }

    @Override
    public abstract int getEnergyCapacity(ItemStack stack);

    public abstract int getEnergyReloadCost(ItemStack stack);

    public abstract Item getAmmoItem(ItemStack stack);

    public abstract int getAmmoCapacity(ItemStack stack);

    public abstract int getFireRate(ItemStack stack);

    public abstract int getReloadSpeed(ItemStack stack);
    //#endregion


    @Override
    public void refreshEquipmentUpgrades(ItemStack stack, EquipmentUpgrades upgrades, Player player)
    {
        // Refresh ammo types
        WeaponAmmoSource newAmmoSource = upgrades.effectStream(LimaTechUpgradeEffectComponents.AMMO_SOURCE.get()).map(AmmoSourceUpgradeEffect::ammoSource).max(Comparator.naturalOrder()).orElse(WeaponAmmoSource.NORMAL);
        stack.set(WEAPON_AMMO_SOURCE, newAmmoSource);
    }

    public String getDesignationId()
    {
        return getDescriptionId() + ".designation";
    }

    protected void hurtTargetEntity(Player player, Entity target, EquipmentUpgrades upgrades, WeaponDamageSource damageSource, final double baseDamage)
    {
        if (player.level() instanceof ServerLevel serverLevel)
        {
            // Apply pre-damage upgrade effects
            upgrades.forEachListEffect(LimaTechUpgradeEffectComponents.WEAPON_PRE_ATTACK, (effect, rank) -> effect.activateEquipmentEffect(serverLevel, rank, player, player.getMainHandItem(), target, damageSource));

            // Run only if entity is living
            if (target instanceof LivingEntity livingTarget)
            {
                // Armor bypass calculations
                double targetBaseArmor = livingTarget.getAttributeBaseValue(Attributes.ARMOR);
                double targetTotalArmor = livingTarget.getAttributeValue(Attributes.ARMOR);
                double modifiedArmor = upgrades.runCompoundOps(LimaTechUpgradeEffectComponents.ARMOR_BYPASS, player, livingTarget, targetBaseArmor, targetTotalArmor);
                float modifier = (float) -(targetTotalArmor - modifiedArmor);
                damageSource.setArmorModifier(modifier);
            }

            // Apply damage modifiers from upgrade
            double totalDamage = upgrades.runCompoundOps(LimaTechUpgradeEffectComponents.WEAPON_DAMAGE, player, target, baseDamage);

            // Get global damage modifier factors and apply
            totalDamage = GlobalWeaponDamageModifiers.applyGlobalModifiers(this, target, baseDamage, totalDamage);

            if (totalDamage > 0)
            {
                target.hurt(damageSource, (float) totalDamage);
            }
        }
    }

    protected void causeInstantDamage(EquipmentUpgrades upgrades, Player player, ResourceKey<DamageType> damageTypeKey, Entity targetEntity, double baseDamage)
    {
        WeaponDamageSource source = WeaponDamageSource.handheldInstantDamage(damageTypeKey, player, this);
        hurtTargetEntity(player, targetEntity, upgrades, source, baseDamage);
    }

    public void causeProjectileDamage(EquipmentUpgrades upgrades, LimaTechProjectile projectile, @Nullable Entity owner, ResourceKey<DamageType> damageTypeKey, Entity targetEntity, double baseDamage)
    {
        WeaponDamageSource source = WeaponDamageSource.projectileDamage(damageTypeKey, projectile, owner, this);
        if (owner instanceof Player player)
        {
            hurtTargetEntity(player, targetEntity, upgrades, source, baseDamage);
        }
        else
        {
            targetEntity.hurt(source, (float) baseDamage);
        }
    }

    protected double calculateProjectileSpeed(Player player, EquipmentUpgrades upgrades, double baseSpeed)
    {
        double newSpeed = upgrades.runCompoundOps(LimaTechUpgradeEffectComponents.WEAPON_PROJECTILE_SPEED, player, null, baseSpeed);
        return Mth.clamp(newSpeed, 0.001d, 3.9d);
    }

    protected void postWeaponFiredGameEvent(EquipmentUpgrades upgrades, Level level, Player player)
    {
        if (upgrades.upgradeEffectTypeAbsent(LimaTechUpgradeEffectComponents.PREVENT_SCULK_VIBRATION.get())) level.gameEvent(player, LimaTechGameEvents.WEAPON_FIRED, player.getEyePosition());
    }

    public ItemStack createDefaultStack(@Nullable HolderLookup.Provider registries, boolean fullMagazine)
    {
        ItemStack stack = new ItemStack(this);

        if (registries != null)
        {
            EquipmentUpgrades defaultUpgrades = getDefaultUpgrades(registries);
            stack.set(EQUIPMENT_UPGRADES, defaultUpgrades);
        }

        if (fullMagazine) setAmmoLoaded(stack, getAmmoCapacity(stack));
        return stack;
    }

    protected EquipmentUpgrades getDefaultUpgrades(HolderLookup.Provider registries)
    {
        return EquipmentUpgrades.EMPTY;
    }

    @Override
    public MutableComponent getDescription()
    {
        return Component.translatable(getDescriptionId());
    }

    @Override
    public Component getName(ItemStack stack)
    {
        return Component.translatable(getDesignationId()).append(CommonComponents.SPACE).append(getDescription().withStyle(ChatFormatting.ITALIC)).withStyle(LIME_GREEN.chatStyle());
    }

    @Override
    public void appendTooltipHintComponents(@Nullable Level level, ItemStack stack, TooltipLineConsumer consumer)
    {
        consumer.accept(AMMO_LOADED_TOOLTIP.translateArgs(stack.getOrDefault(WEAPON_AMMO, 0), getAmmoCapacity(stack)).withStyle(LIME_GREEN.chatStyle()));

        WeaponAmmoSource ammoSource = getAmmoSourceFromItem(stack);
        switch (ammoSource)
        {
            case NORMAL -> consumer.accept(ammoSource.getItemTooltip().translateArgs(getAmmoItem(stack).getDescription()).withStyle(ChatFormatting.GRAY));
            case COMMON_ENERGY_UNIT -> {
                consumer.accept(ammoSource.getItemTooltip().translate().withStyle(REM_BLUE.chatStyle()));
                LimaTechTooltipUtil.appendSimpleEnergyTooltip(consumer, getEnergyStored(stack));
                consumer.accept(ENERGY_AMMO_COST_TOOLTIP.translateArgs(formatEnergyWithSuffix(getEnergyReloadCost(stack))).withStyle(REM_BLUE.chatStyle()));
            }
            case INFINITE -> consumer.accept(ammoSource.getItemTooltip().translate().withStyle(CREATIVE_PINK.chatStyle()));
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity)
    {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack)
    {
        return UseAnim.CUSTOM;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand)
    {
        ItemStack stack = player.getItemInHand(usedHand);

        if (usedHand == InteractionHand.MAIN_HAND)
        {
            ItemStack offhandStack = player.getItemInHand(InteractionHand.OFF_HAND);
            if (!offhandStack.canPerformAction(ItemAbilities.SHIELD_BLOCK) && canFocusReticle(stack, player, player.getData(LimaTechAttachmentTypes.WEAPON_CONTROLS)))
            {
                return ItemUtils.startUsingInstantly(level, player, InteractionHand.MAIN_HAND);
            }
            else
            {
                return InteractionResultHolder.pass(stack);
            }
        }
        else
        {
            return InteractionResultHolder.fail(stack);
        }
    }

    @Override
    public boolean addDefaultInstanceToCreativeTab(ResourceLocation tabId)
    {
        return false;
    }

    @Override
    public void addAdditionalToCreativeTab(ResourceLocation tabId, CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output, CreativeModeTab.TabVisibility tabVisibility)
    {
        ItemStack stack = createDefaultStack(parameters.holders(), true);
        output.accept(stack, tabVisibility);
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
    public int getEnchantmentLevel(ItemStack stack, Holder<Enchantment> enchantment)
    {
        return getUpgradeEnchantmentLevel(stack, enchantment);
    }
}