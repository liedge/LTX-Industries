package liedge.limatech.item.weapon;

import com.mojang.serialization.Codec;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.item.LimaCreativeTabFillerItem;
import liedge.limacore.lib.Translatable;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limatech.entity.LimaTraceableProjectile;
import liedge.limatech.entity.damage.WeaponDamageSource;
import liedge.limatech.item.EnergyHolderItem;
import liedge.limatech.item.TooltipShiftHintItem;
import liedge.limatech.item.UpgradableEquipmentItem;
import liedge.limatech.lib.upgrades.equipment.EquipmentUpgrades;
import liedge.limatech.lib.weapons.AbstractWeaponControls;
import liedge.limatech.lib.weapons.GlobalWeaponDamageModifiers;
import liedge.limatech.lib.weapons.WeaponAmmoSource;
import liedge.limatech.registry.bootstrap.LimaTechDamageTypes;
import liedge.limatech.registry.game.LimaTechAttachmentTypes;
import liedge.limatech.registry.game.LimaTechUpgradeEffectComponents;
import liedge.limatech.util.LimaTechUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.neoforged.neoforge.common.ItemAbilities;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

import static liedge.limatech.LimaTech.RESOURCES;
import static liedge.limatech.LimaTechConstants.*;
import static liedge.limatech.registry.game.LimaTechDataComponents.WEAPON_AMMO;
import static liedge.limatech.registry.game.LimaTechDataComponents.WEAPON_AMMO_SOURCE;

public abstract class WeaponItem extends Item implements EnergyHolderItem, LimaCreativeTabFillerItem, TooltipShiftHintItem, UpgradableEquipmentItem
{
    public static final Codec<WeaponItem> CODEC = LimaCoreCodecs.classCastRegistryCodec(BuiltInRegistries.ITEM, WeaponItem.class);
    public static final StreamCodec<RegistryFriendlyByteBuf, WeaponItem> STREAM_CODEC = LimaStreamCodecs.classCastRegistryStreamCodec(Registries.ITEM, WeaponItem.class);
    public static final Translatable AMMO_LOADED_TOOLTIP = RESOURCES.translationHolder("tooltip.{}.ammo_loaded");

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

    public void setAmmoLoadedMax(ItemStack stack)
    {
        setAmmoLoaded(stack, getAmmoCapacity(stack));
    }

    @Override
    public boolean supportsEnergyStorage(ItemStack stack)
    {
        return getAmmoSourceFromItem(stack) == WeaponAmmoSource.COMMON_ENERGY_UNIT;
    }

    @Override
    public int getBaseEnergyTransferRate(ItemStack stack)
    {
        return getBaseEnergyCapacity(stack) / 20;
    }

    public abstract Item getAmmoItem(ItemStack stack);

    public abstract int getAmmoCapacity(ItemStack stack);

    public abstract int getFireRate(ItemStack stack);

    public abstract int getReloadSpeed(ItemStack stack);
    //#endregion

    @Override
    public void onUpgradeRefresh(LootContext context, ItemStack stack, EquipmentUpgrades upgrades)
    {
        // Refresh ammo types
        WeaponAmmoSource source = upgrades.effectStream(LimaTechUpgradeEffectComponents.AMMO_SOURCE).max(Comparator.naturalOrder()).orElse(WeaponAmmoSource.NORMAL);
        stack.set(WEAPON_AMMO_SOURCE, source);

        // Run base after to allow CE ammo type to properly work
        UpgradableEquipmentItem.super.onUpgradeRefresh(context, stack, upgrades);
    }

    public boolean hurtEntity(LivingEntity attacker, Entity target, EquipmentUpgrades upgrades, DamageSource damageSource, double baseDamage)
    {
        if (attacker.level() instanceof ServerLevel level)
        {
            // Create the loot context
            LootContext context = LimaTechUtil.entityLootContext(level, target, damageSource, attacker);

            // Run pre attack effects here
            upgrades.applyDamageContextEffects(LimaTechUpgradeEffectComponents.EQUIPMENT_PRE_ATTACK, level, EnchantmentTarget.ATTACKER, target, attacker, damageSource);

            // Get upgraded damage, then apply global damage modifiers
            double damage = getUpgradedDamage(level, upgrades, target, damageSource, baseDamage);
            damage = GlobalWeaponDamageModifiers.applyGlobalModifiers(this, target, context, baseDamage, damage);

            // Only hurt if we have non-negligible damage
            return damage > 1e-4 && target.hurt(damageSource, (float) damage);
        }

        return false;
    }

    protected void causeInstantDamage(EquipmentUpgrades upgrades, LivingEntity attacker, Entity targetEntity, double baseDamage)
    {
        WeaponDamageSource source = WeaponDamageSource.handheldInstantDamage(LimaTechDamageTypes.LIGHTFRAG, attacker, this, upgrades);
        hurtEntity(attacker, targetEntity, upgrades, source, baseDamage);
    }

    public void causeProjectileDamage(EquipmentUpgrades upgrades, LimaTraceableProjectile projectile, @Nullable LivingEntity attacker, ResourceKey<DamageType> damageTypeKey, Entity targetEntity, double baseDamage)
    {
        WeaponDamageSource source = WeaponDamageSource.projectileDamage(damageTypeKey, projectile, attacker, this, upgrades);
        if (attacker != null)
        {
            hurtEntity(attacker, targetEntity, upgrades, source, baseDamage);
        }
        else
        {
            targetEntity.hurt(source, (float) baseDamage);
        }
    }

    protected double calculateProjectileSpeed(ServerLevel level, EquipmentUpgrades upgrades, double baseSpeed)
    {
        LootContext context = LimaTechUtil.emptyLootContext(level);
        double newSpeed = upgrades.applyValue(LimaTechUpgradeEffectComponents.WEAPON_PROJECTILE_SPEED, context, baseSpeed);
        return Mth.clamp(newSpeed, 0.001d, 3.9d);
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
                appendEquipmentEnergyTooltip(consumer, stack);
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
        ItemStack stack = createStackWithDefaultUpgrades(parameters.holders());
        setAmmoLoadedMax(stack);

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
}