package liedge.ltxindustries;

import liedge.limacore.event.DamageAttributeModifiersEvent;
import liedge.limacore.util.LimaCoreUtil;
import liedge.limacore.util.LimaLootUtil;
import liedge.ltxindustries.entity.LTXIEntityUtil;
import liedge.ltxindustries.entity.damage.DropsRedirect;
import liedge.ltxindustries.entity.damage.UpgradesAwareDamageSource;
import liedge.ltxindustries.item.UpgradableEquipmentItem;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.lib.EquipmentDamageModifiers;
import liedge.ltxindustries.lib.shield.EntityBubbleShield;
import liedge.ltxindustries.lib.upgrades.UpgradeContexts;
import liedge.ltxindustries.lib.upgrades.effect.EffectTarget;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrades;
import liedge.ltxindustries.registry.game.LTXIAttachmentTypes;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import liedge.ltxindustries.registry.game.LTXIMobEffects;
import liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents;
import liedge.ltxindustries.util.LTXIUpgradeUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.VanillaGameEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;

@EventBusSubscriber(modid = LTXIndustries.MODID)
public final class LTXIEventHandler
{
    private LTXIEventHandler() {}

    @SubscribeEvent
    public static void registerReloadListeners(final AddReloadListenerEvent event)
    {
        event.addListener(EquipmentDamageModifiers.getInstance());
    }

    @SubscribeEvent
    public static void onVanillaGameEvent(final VanillaGameEvent event)
    {
        // Players only
        if (event.getCause() instanceof Player player)
        {
            for (EquipmentSlot slot : EquipmentSlot.values())
            {
                EquipmentUpgrades upgrades = UpgradableEquipmentItem.getEquipmentUpgradesFromStack(player.getItemBySlot(slot));
                if (upgrades.anyMatch(LTXIUpgradeEffectComponents.SUPPRESS_VIBRATIONS.get(), (effect, ignored) -> effect.test(slot, event.getVanillaEvent())))
                {
                    event.setCanceled(true);
                    break;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEquipmentChanged(final LivingEquipmentChangeEvent event)
    {
        // Only run serverside and on upgradable equipment items
        ItemStack stack = event.getTo();
        if (event.getEntity().level() instanceof ServerLevel level && stack.getItem() instanceof UpgradableEquipmentItem equipmentItem)
        {
            EquipmentUpgrades upgrades = equipmentItem.getUpgrades(stack);
            Integer lastHash = stack.get(LTXIDataComponents.LAST_EQUIPMENT_HASH);

            // Run check if last upgrades hash-print is null or different
            if (lastHash == null || lastHash != upgrades.hashCode())
            {
                equipmentItem.onUpgradeRefresh(LimaLootUtil.emptyLootContext(level), stack, upgrades);
                stack.set(LTXIDataComponents.LAST_EQUIPMENT_HASH, upgrades.hashCode());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(final PlayerTickEvent.Pre event)
    {
        Player player = event.getEntity();

        // Weapon system tick
        ItemStack heldItem = player.getMainHandItem();
        WeaponItem weaponItem = LimaCoreUtil.castOrNull(WeaponItem.class, heldItem.getItem());
        player.getData(LTXIAttachmentTypes.INPUT_EXTENSIONS).tickInput(player, heldItem, weaponItem);

        // Shield & equipment tick
        if (player.level() instanceof ServerLevel serverLevel)
        {
            player.getData(LTXIAttachmentTypes.PLAYER_SHIELD).tick(player);

            LootContext tickContext = UpgradeContexts.entityContext(serverLevel, player);
            LTXIUpgradeUtil.iterateEquipmentUpgrades(player, EquipmentSlot.values(), (level, upgrades, equipmentInUse) ->
                    upgrades.tickEquipment(level, tickContext, player, equipmentInUse));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockDrops(final BlockDropsEvent event)
    {
        // Only for players and qualifying upgradable equipment
        if (event.getBreaker() instanceof Player player && event.getTool().getItem() instanceof UpgradableEquipmentItem equipmentItem)
        {
            EquipmentUpgrades upgrades = equipmentItem.getUpgrades(event.getTool());
            DropsRedirect redirect = DropsRedirect.forBlocks(player, upgrades);
            if (redirect != null)
            {
                redirect.captureAndRelocateDrops(event.getDrops());
                int xp = event.getDroppedExperience();
                event.setDroppedExperience(0);
                player.giveExperiencePoints(xp);
            }
        }
    }

    @SubscribeEvent
    public static void checkEffectApplicable(final MobEffectEvent.Applicable event)
    {
        if (event.getEffectSource() instanceof LivingEntity attacker && attacker.hasEffect(LTXIMobEffects.NEURO_SUPPRESSED))
        {
            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
        }

        if (event.getApplicationResult() && !event.getEffectInstance().getEffect().value().isBeneficial())
        {
            LivingEntity entity = event.getEntity();

            // TODO: Armor block?

            // Bubble shield block
            EntityBubbleShield shield = entity.getCapability(LTXICapabilities.ENTITY_BUBBLE_SHIELD);
            MobEffectInstance effectInstance = event.getEffectInstance();
            if (shield != null && shield.blockMobEffect(entity, entity.level(), effectInstance))
            {
                event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerAttackEntity(final AttackEntityEvent event)
    {
        Player player = event.getEntity();
        if (!player.level().isClientSide())
        {
            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() instanceof UpgradableEquipmentItem equipmentItem)
            {
                TriState result = LTXIEntityUtil.checkUpgradeTargetValidity(player, event.getTarget(), equipmentItem.getUpgrades(stack));
                if (result.isFalse()) event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDrops(final LivingDropsEvent event)
    {
        DamageSource damageSource = event.getSource();
        DropsRedirect dropsRedirect = null;

        // First attempt to get from upgradable damage source. Most likely case
        if (damageSource instanceof UpgradesAwareDamageSource upgradableSource)
        {
            dropsRedirect = upgradableSource.createDropsRedirect();
        }
        // Otherwise make drops redirect (if possible) from a melee attacks
        else if (damageSource.getDirectEntity() instanceof Player player && player.getWeaponItem().getItem() instanceof UpgradableEquipmentItem equipmentItem && damageSource.is(DamageTypeTags.IS_PLAYER_ATTACK))
        {
            EquipmentUpgrades upgrades = equipmentItem.getUpgrades(player.getWeaponItem());
            dropsRedirect = DropsRedirect.forMobDrops(player, upgrades);
        }

        // Perform redirection if one was found
        if (dropsRedirect != null) dropsRedirect.captureAndRelocateDrops(event.getDrops());
    }

    // Run this on highest so damage source components in LimaCore can apply properly
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingIncomingDamageHighest(final LivingIncomingDamageEvent event)
    {
        LivingEntity targetEntity = event.getEntity();
        if (!targetEntity.level().isClientSide()) return;

        LootContext context = LimaLootUtil.entityLootContext((ServerLevel) targetEntity.level(), targetEntity, event.getSource());
        LTXIUpgradeUtil.iterateEquipmentUpgrades(targetEntity, LTXIUpgradeUtil.ARMOR_SLOTS, (level, upgrades, equipmentInUse) ->
                upgrades.applyDamageEntityEffects(LTXIUpgradeEffectComponents.PRE_ATTACK, level, context, EffectTarget.VICTIM, equipmentInUse));

        LTXIUpgradeUtil.iterateDamageUpgrades(event.getSource(), (level, upgrades, equipmentInUse) ->
        {
            upgrades.applyDamageEntityEffects(LTXIUpgradeEffectComponents.PRE_ATTACK, level, context, EffectTarget.ATTACKER, equipmentInUse);
            upgrades.applyReductionBreaches(context, event);
        });
    }

    // Run bubble shield checks on lowest to first apply damage modifiers
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingIncomingDamageLowest(final LivingIncomingDamageEvent event)
    {
        LivingEntity hurtEntity = event.getEntity();
        if (!hurtEntity.level().isClientSide())
        {
            EntityBubbleShield shield = hurtEntity.getCapability(LTXICapabilities.ENTITY_BUBBLE_SHIELD);
            if (shield != null)
            {
                float newDamage = shield.blockDamage(hurtEntity, hurtEntity.level(), event.getSource(), event.getAmount());
                event.setAmount(newDamage);
                if (newDamage <= 0) event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onDamageAttributesAndTags(final DamageAttributeModifiersEvent event)
    {
        LTXIUpgradeUtil.iterateDamageUpgrades(event.getDamageSource(), ($1, upgrades, equipmentInUse) ->
        {
            List<TagKey<DamageType>> extraTags = upgrades.listEffectStream(LTXIUpgradeEffectComponents.EXTRA_DAMAGE_TAGS).toList();
            if (!extraTags.isEmpty()) event.getDamageSource().limaCore$addExtraTags(extraTags);

            upgrades.forEachEffect(LTXIUpgradeEffectComponents.ADD_DAMAGE_ATTRIBUTES, (effect, rank) -> event.addAttributeModifier(effect.attribute(), effect.createModifier(rank)));
        });
    }

    @SubscribeEvent
    public static void onLivingDeath(final LivingDeathEvent event)
    {
        LTXIUpgradeUtil.iterateDamageUpgrades(event.getSource(), (level, upgrades, equipmentInUse) ->
        {
            LootContext context = LimaLootUtil.entityLootContext(level, event.getEntity(), event.getSource());
            upgrades.applyDamageEntityEffects(LTXIUpgradeEffectComponents.ENTITY_KILLED, level, context, EffectTarget.ATTACKER, equipmentInUse);
        });
    }
}