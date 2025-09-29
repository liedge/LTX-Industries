package liedge.ltxindustries.item.weapon;

import com.mojang.serialization.Codec;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.client.particle.ColorParticleOptions;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.item.LimaCreativeTabFillerItem;
import liedge.limacore.lib.TickTimer;
import liedge.limacore.lib.Translatable;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limacore.util.LimaMathUtil;
import liedge.limacore.util.LimaNetworkUtil;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.entity.CompoundHitResult;
import liedge.ltxindustries.entity.DynamicClipContext;
import liedge.ltxindustries.entity.LimaTraceableEntity;
import liedge.ltxindustries.entity.damage.UpgradableEquipmentDamageSource;
import liedge.ltxindustries.item.EnergyHolderItem;
import liedge.ltxindustries.item.TooltipShiftHintItem;
import liedge.ltxindustries.item.UpgradableEquipmentItem;
import liedge.ltxindustries.lib.EquipmentDamageModifiers;
import liedge.ltxindustries.lib.upgrades.effect.ValueUpgradeEffect;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrades;
import liedge.ltxindustries.lib.weapons.AbstractWeaponControls;
import liedge.ltxindustries.lib.weapons.WeaponReloadSource;
import liedge.ltxindustries.registry.bootstrap.LTXIDamageTypes;
import liedge.ltxindustries.registry.game.*;
import liedge.ltxindustries.util.LTXITooltipUtil;
import liedge.ltxindustries.util.LTXIUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbilities;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

import static liedge.ltxindustries.LTXIConstants.LIME_GREEN;

public abstract class WeaponItem extends Item implements EnergyHolderItem, LimaCreativeTabFillerItem, TooltipShiftHintItem, UpgradableEquipmentItem
{
    public static final Codec<WeaponItem> CODEC = LimaCoreCodecs.classCastRegistryCodec(BuiltInRegistries.ITEM, WeaponItem.class);
    public static final StreamCodec<RegistryFriendlyByteBuf, WeaponItem> STREAM_CODEC = LimaStreamCodecs.classCastRegistryStreamCodec(Registries.ITEM, WeaponItem.class);

    public static final Translatable AMMO_LOADED_TOOLTIP = LTXILangKeys.tooltip("ammo_loaded");
    public static final Translatable RELOAD_SPEED_TOOLTIP = LTXILangKeys.tooltip("reload_speed");

    public static final double MAX_PROJECTILE_SPEED = 3.9d;

    private final int baseMagCapacity;
    private final double baseRange;
    private final int baseReloadSpeed;
    private final WeaponReloadSource baseReloadSource;
    private final int baseMaxHits;
    private final double basePunchTrough;

    protected WeaponItem(Properties properties, int baseMagCapacity, double baseRange, int baseReloadSpeed, WeaponReloadSource baseReloadSource, int baseMaxHits, double basePunchThrough)
    {
        super(properties
                .component(LTXIDataComponents.MAGAZINE_CAPACITY, baseMagCapacity)
                .component(LTXIDataComponents.WEAPON_RANGE, baseRange)
                .component(LTXIDataComponents.RELOAD_SPEED, baseReloadSpeed)
                .component(LTXIDataComponents.RELOAD_SOURCE, baseReloadSource)
                .component(LTXIDataComponents.MAX_HITS, baseMaxHits)
                .component(LTXIDataComponents.PUNCH_THROUGH, basePunchThrough));

        this.baseMagCapacity = baseMagCapacity;
        this.baseRange = baseRange;
        this.baseReloadSpeed = baseReloadSpeed;
        this.baseReloadSource = baseReloadSource;
        this.baseMaxHits = baseMaxHits;
        this.basePunchTrough = basePunchThrough;
    }

    protected WeaponItem(Properties properties, int baseMagCapacity, double baseRange, int baseReloadSpeed, Holder<Item> defaultAmmoItem, int baseMaxHits, double basePunchTrough)
    {
        this(properties, baseMagCapacity, baseRange, baseReloadSpeed, WeaponReloadSource.withItem(defaultAmmoItem), baseMaxHits, basePunchTrough);
    }

    //#region Weapon user events
    public abstract void triggerPressed(ItemStack heldItem, Player player, AbstractWeaponControls input);

    // TODO: Re-introduce later if necessary, kept for reference
    //public abstract void triggerRelease(ItemStack heldItem, Player player, AbstractWeaponControls input, boolean releasedByPlayer);

    public abstract boolean canContinueHoldingTrigger(ItemStack heldItem, Player player, AbstractWeaponControls input);

    public void triggerHoldingTick(ItemStack heldItem, Player player, AbstractWeaponControls input) {}

    public void onStartedHoldingTrigger(ItemStack heldItem, Player player, AbstractWeaponControls input) {}

    public void onStoppedHoldingTrigger(ItemStack heldItem, Player player, AbstractWeaponControls input, boolean releasedByPlayer) {}

    public abstract void weaponFired(ItemStack heldItem, Player player, Level level, AbstractWeaponControls controls);
    //#endregion

    //#region Energy handling properties
    @Override
    public boolean supportsEnergyStorage(ItemStack stack)
    {
        return getReloadSource(stack).getType() == WeaponReloadSource.Type.COMMON_ENERGY;
    }

    @Override
    public final int getBaseEnergyCapacity(ItemStack stack)
    {
        return 1;
    }

    @Override
    public final int getBaseEnergyUsage(ItemStack stack)
    {
        return 1;
    }

    @Override
    public final int getBaseEnergyTransferRate(ItemStack stack)
    {
        return getEnergyCapacity(stack) / 20;
    }
    //#endregion

    //#region Weapon properties/behavior
    public boolean canFocusReticle(ItemStack heldItem, Player player, AbstractWeaponControls controls)
    {
        return controls.getReloadTimer().getTimerState() == TickTimer.State.STOPPED;
    }

    public int getAmmoLoaded(ItemStack stack)
    {
        return stack.getOrDefault(LTXIDataComponents.WEAPON_AMMO, 0);
    }

    public void setAmmoLoaded(ItemStack stack, int newAmmo)
    {
        stack.set(LTXIDataComponents.WEAPON_AMMO, Math.max(0, newAmmo));
    }

    public void setAmmoLoadedMax(ItemStack stack)
    {
        setAmmoLoaded(stack, getAmmoCapacity(stack));
    }

    public final int getAmmoCapacity(ItemStack stack)
    {
        return stack.getOrDefault(LTXIDataComponents.MAGAZINE_CAPACITY, baseMagCapacity);
    }

    public boolean isOneHanded(ItemStack stack)
    {
        return false;
    }

    public abstract int getFireRate(ItemStack stack);

    public double getWeaponRange(ItemStack stack)
    {
        return stack.getOrDefault(LTXIDataComponents.WEAPON_RANGE, baseRange);
    }

    public double getProjectileWeaponRange(ItemStack stack)
    {
        return Mth.clamp(getWeaponRange(stack), 0, MAX_PROJECTILE_SPEED);
    }

    public int getReloadSpeed(ItemStack stack)
    {
        return stack.getOrDefault(LTXIDataComponents.RELOAD_SPEED, baseReloadSpeed);
    }

    public WeaponReloadSource getReloadSource(ItemStack stack)
    {
        return stack.getOrDefault(LTXIDataComponents.RELOAD_SOURCE, baseReloadSource);
    }

    public int getEntityMaxHits(ItemStack stack)
    {
        return stack.getOrDefault(LTXIDataComponents.MAX_HITS, baseMaxHits);
    }

    public double getBlockPierceDistance(ItemStack stack)
    {
        return stack.getOrDefault(LTXIDataComponents.PUNCH_THROUGH, basePunchTrough);
    }
    //#endregion

    @Override
    public void onUpgradeRefresh(LootContext context, ItemStack stack, EquipmentUpgrades upgrades)
    {
        // Apply modifiers for weapon base stats
        applyIntStat(stack, upgrades, context, baseMagCapacity, LTXIDataComponents.MAGAZINE_CAPACITY, LTXIUpgradeEffectComponents.MAGAZINE_CAPACITY);
        applyDoubleStat(stack, upgrades, context, baseRange, LTXIDataComponents.WEAPON_RANGE, LTXIUpgradeEffectComponents.WEAPON_RANGE);
        applyIntStat(stack, upgrades, context, baseReloadSpeed, LTXIDataComponents.RELOAD_SPEED, LTXIUpgradeEffectComponents.RELOAD_SPEED);
        applyIntStat(stack, upgrades, context, baseMaxHits, LTXIDataComponents.MAX_HITS, LTXIUpgradeEffectComponents.MAX_HITS);
        applyDoubleStat(stack, upgrades, context, basePunchTrough, LTXIDataComponents.PUNCH_THROUGH, LTXIUpgradeEffectComponents.BLOCK_PIERCE_DISTANCE);

        // Refresh reload source
        WeaponReloadSource reloadSource = upgrades.effectStream(LTXIUpgradeEffectComponents.RELOAD_SOURCE)
                .max(Comparator.comparing(WeaponReloadSource::getType))
                .orElse(baseReloadSource);
        stack.set(LTXIDataComponents.RELOAD_SOURCE, reloadSource);

        // Run base after to allow CE ammo type to properly work
        UpgradableEquipmentItem.super.onUpgradeRefresh(context, stack, upgrades);
    }

    private boolean hurtEntity(LivingEntity attacker, Entity target, UpgradableEquipmentDamageSource damageSource, double baseDamage)
    {
        if (attacker.level() instanceof ServerLevel level)
        {
            // Create the loot context and get the upgrades
            LootContext context = LTXIUtil.entityLootContext(level, target, damageSource, attacker);
            ItemStack weaponItem = damageSource.getWeaponItem();
            EquipmentUpgrades upgrades = getUpgrades(weaponItem);

            // Run pre attack effects here
            upgrades.applyDamageContextEffects(LTXIUpgradeEffectComponents.EQUIPMENT_PRE_ATTACK, level, EnchantmentTarget.ATTACKER, target, attacker, damageSource);

            // Get upgraded damage, then apply global damage modifiers
            double damage = EquipmentDamageModifiers.getInstance().apply(weaponItem, context, baseDamage,
                    getUpgradedDamage(level, upgrades, target, damageSource, baseDamage));

            // Only hurt if we have non-negligible damage
            return damage > 1e-4 && target.hurt(damageSource, (float) damage);
        }

        return false;
    }

    protected void causeLightfragDamage(ItemStack heldItem, LivingEntity attacker, Entity targetEntity, double baseDamage)
    {
        UpgradableEquipmentDamageSource source = UpgradableEquipmentDamageSource.directDamage(LTXIDamageTypes.LIGHTFRAG, attacker, heldItem);
        hurtEntity(attacker, targetEntity, source, baseDamage);
    }

    public boolean causeProjectileDamage(ItemStack weaponItem, LimaTraceableEntity projectile, @Nullable LivingEntity attacker, ResourceKey<DamageType> damageTypeKey, Entity targetEntity, double baseDamage)
    {
        UpgradableEquipmentDamageSource source = UpgradableEquipmentDamageSource.projectileDamage(damageTypeKey, projectile, attacker, weaponItem);
        if (attacker != null)
        {
            return hurtEntity(attacker, targetEntity, source, baseDamage);
        }
        else
        {
            return targetEntity.hurt(source, (float) baseDamage);
        }
    }

    protected void traceLightfrag(ItemStack stack, Player player, Level level, double baseDamage, double inaccuracy, double bbExpansion)
    {
        if (!level.isClientSide())
        {
            CompoundHitResult hitResult = CompoundHitResult.tracePath(level, player, getUpgrades(stack), getWeaponRange(stack), inaccuracy, getEntityMaxHits(stack), getBlockPierceDistance(stack), DynamicClipContext.FluidCollisionPredicate.NONE, bbExpansion);
            hitResult.entityHits().forEach(hit -> causeLightfragDamage(stack, player, hit.getEntity(), baseDamage));
            level.gameEvent(player, LTXIGameEvents.WEAPON_FIRED, player.getEyePosition());
            sendTracerParticle(level, hitResult.origin(), hitResult.impactLocation());
        }
    }

    protected void sendTracerParticle(Level level, Vec3 start, Vec3 end)
    {
        LimaNetworkUtil.sendParticle(level, new ColorParticleOptions(LTXIParticles.LIGHTFRAG_TRACER, LIME_GREEN), LimaNetworkUtil.UNLIMITED_PARTICLE_DIST, start, end);
    }

    @Override
    public void appendTooltipHintComponents(Level level, ItemStack stack, TooltipLineConsumer consumer)
    {
        consumer.accept(AMMO_LOADED_TOOLTIP.translateArgs(Component.literal(Integer.toString(getAmmoLoaded(stack))).withStyle(LIME_GREEN.chatStyle()), getAmmoCapacity(stack)).withStyle(ChatFormatting.GRAY));
        consumer.accept(RELOAD_SPEED_TOOLTIP.translateArgs(LTXITooltipUtil.flatNumberWithoutSign(getReloadSpeed(stack) / 20d).withStyle(LIME_GREEN.chatStyle())).withStyle(ChatFormatting.GRAY));

        WeaponReloadSource reloadSource = getReloadSource(stack);
        consumer.accept(reloadSource.getItemTooltip());
        if (reloadSource.getType() == WeaponReloadSource.Type.COMMON_ENERGY)
            appendEquipmentEnergyTooltip(consumer, stack);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity)
    {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack)
    {
        return UseAnim.NONE;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand)
    {
        ItemStack stack = player.getItemInHand(usedHand);

        if (usedHand == InteractionHand.MAIN_HAND)
        {
            ItemStack offhandItem = player.getOffhandItem();

            boolean handCheck = offhandItem.isEmpty() || isOneHanded(stack);
            if (handCheck && !offhandItem.canPerformAction(ItemAbilities.SHIELD_BLOCK) && canFocusReticle(stack, player, player.getData(LTXIAttachmentTypes.WEAPON_CONTROLS)))
            {
                return ItemUtils.startUsingInstantly(level, player, InteractionHand.MAIN_HAND);
            }
        }

        return InteractionResultHolder.pass(stack);
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

    private void applyDoubleStat(ItemStack stack, EquipmentUpgrades upgrades, LootContext context, double baseFallback, Supplier<? extends DataComponentType<Double>> type, Supplier<? extends DataComponentType<List<ValueUpgradeEffect>>> effectType)
    {
        double base = components().getOrDefault(type.get(), baseFallback);
        double value = upgrades.applyValue(effectType, context, base);
        stack.set(type, value);
    }

    private void applyIntStat(ItemStack stack, EquipmentUpgrades upgrades, LootContext context, int baseFallback, Supplier<? extends DataComponentType<Integer>> type, Supplier<? extends DataComponentType<List<ValueUpgradeEffect>>> effectType)
    {
        int base = components().getOrDefault(type.get(), baseFallback);
        int value = LimaMathUtil.round(upgrades.applyValue(effectType, context, base));
        stack.set(type, value);
    }
}