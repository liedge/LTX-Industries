package liedge.ltxindustries;

import liedge.limacore.util.LimaCoreUtil;
import liedge.ltxindustries.entity.BubbleShieldUser;
import liedge.ltxindustries.entity.damage.DropsRedirect;
import liedge.ltxindustries.entity.damage.UpgradableDamageSource;
import liedge.ltxindustries.item.UpgradableEquipmentItem;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.lib.upgrades.UpgradesContainerBase;
import liedge.ltxindustries.lib.upgrades.effect.equipment.DirectDropsUpgradeEffect;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrades;
import liedge.ltxindustries.network.packet.ClientboundEntityShieldPacket;
import liedge.ltxindustries.network.packet.ClientboundPlayerShieldPacket;
import liedge.ltxindustries.registry.game.LTXIAttachmentTypes;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import liedge.ltxindustries.registry.game.LTXIMobEffects;
import liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents;
import liedge.ltxindustries.util.LTXIUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.VanillaGameEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = LTXIndustries.MODID)
public final class LTXIEventHandler
{
    private LTXIEventHandler() {}

    @SubscribeEvent
    public static void onVanillaGameEvent(final VanillaGameEvent event)
    {
        // Players only
        if (event.getCause() instanceof Player player)
        {
            boolean cancelEvent = false;

            for (EquipmentSlot slot : EquipmentSlot.values())
            {
                EquipmentUpgrades upgrades = UpgradableEquipmentItem.getEquipmentUpgradesFromStack(player.getItemBySlot(slot));
                if (upgrades.anyMatch(LTXIUpgradeEffectComponents.PREVENT_VIBRATION.get(), (effect, ignored) -> effect.apply(slot, event.getVanillaEvent())))
                {
                    cancelEvent = true;
                    break;
                }
            }

            event.setCanceled(cancelEvent);
        }
    }

    @SubscribeEvent
    public static void onEquipmentChanged(final LivingEquipmentChangeEvent event)
    {
        // Only run serverside and on upgradable equipment items
        if (event.getEntity().level() instanceof ServerLevel level && event.getTo().getItem() instanceof UpgradableEquipmentItem equipmentItem)
        {
            ItemStack stack = event.getTo();
            EquipmentUpgrades upgrades = equipmentItem.getUpgrades(stack);
            Integer lastHash = stack.get(LTXIDataComponents.LAST_EQUIPMENT_HASH);

            // Run check if last upgrades hash-print is null or different
            if (lastHash == null || lastHash != upgrades.hashCode())
            {
                equipmentItem.onUpgradeRefresh(LTXIUtil.emptyLootContext(level), stack, upgrades);
                stack.set(LTXIDataComponents.LAST_EQUIPMENT_HASH, upgrades.hashCode());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(final PlayerTickEvent.Pre event)
    {
        Player player = event.getEntity();
        ItemStack heldItem = player.getMainHandItem();
        WeaponItem weaponItem = LimaCoreUtil.castOrNull(WeaponItem.class, heldItem.getItem());
        player.getData(LTXIAttachmentTypes.WEAPON_CONTROLS).tickInput(player, heldItem, weaponItem);
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(final PlayerEvent.PlayerChangedDimensionEvent event)
    {
        ClientboundPlayerShieldPacket.sendPacketToPlayer(event.getEntity());
    }

    @SubscribeEvent
    public static void onStartTrackingEntity(final PlayerEvent.StartTracking event)
    {
        Entity entity = event.getTarget();

        if (!entity.level().isClientSide())
        {
            BubbleShieldUser shield = entity.getCapability(LTXICapabilities.ENTITY_BUBBLE_SHIELD);
            if (shield != null)
            {
                ClientboundEntityShieldPacket packet = new ClientboundEntityShieldPacket(entity.getId(), shield.getShieldHealth());
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, packet);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(final PlayerEvent.PlayerLoggedInEvent event)
    {
        ClientboundPlayerShieldPacket.sendPacketToPlayer(event.getEntity());
    }

    @SubscribeEvent
    public static void onPostEntityTick(final EntityTickEvent.Post event)
    {
        Entity entity = event.getEntity();
        Level level = entity.level();

        if (!level.isClientSide())
        {
            // Bubble shield tick
            if (entity instanceof LivingEntity livingEntity)
            {
                livingEntity.getExistingData(LTXIAttachmentTypes.BUBBLE_SHIELD).ifPresent(shield -> shield.tickShield(livingEntity));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockDrops(final BlockDropsEvent event)
    {
        // Only for players and qualifying upgradable equipment
        if (event.getBreaker() instanceof Player player && event.getTool().getItem() instanceof UpgradableEquipmentItem equipmentItem)
        {
            EquipmentUpgrades upgrades = equipmentItem.getUpgrades(event.getTool());
            DropsRedirect redirect = DropsRedirect.forPlayer(player, upgrades, DirectDropsUpgradeEffect.Type.BLOCK_DROPS);
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
        if (event.getEffectSource() instanceof LivingEntity livingEntity && livingEntity.hasEffect(LTXIMobEffects.NEURO_SUPPRESSED))
        {
            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDrops(final LivingDropsEvent event)
    {
        DamageSource damageSource = event.getSource();
        DropsRedirect dropsRedirect = null;

        // First attempt to get from upgradable damage source. Most likely case
        if (damageSource instanceof UpgradableDamageSource upgradableSource)
        {
            dropsRedirect = upgradableSource.createDropsRedirect();
        }
        // Otherwise make drops redirect (if possible) from a melee attacks
        else if (damageSource.getDirectEntity() instanceof Player player && player.getWeaponItem().getItem() instanceof UpgradableEquipmentItem equipmentItem && damageSource.is(DamageTypeTags.IS_PLAYER_ATTACK))
        {
            EquipmentUpgrades upgrades = equipmentItem.getUpgrades(player.getWeaponItem());
            dropsRedirect = DropsRedirect.forPlayer(player, upgrades, DirectDropsUpgradeEffect.Type.ENTITY_DROPS);
        }

        // Perform redirection if one was found
        if (dropsRedirect != null) dropsRedirect.captureAndRelocateDrops(event.getDrops());
    }

    // Run this on highest so damage source components in LimaCore can apply properly
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingIncomingDamageHighest(final LivingIncomingDamageEvent event)
    {
        DamageSource source = event.getSource();

        // Process pre-attack upgrade effects for melee attacks here
        if (source.getDirectEntity() instanceof LivingEntity attacker && !attacker.level().isClientSide() && attacker.getWeaponItem().getItem() instanceof UpgradableEquipmentItem equipmentItem && source.is(DamageTypeTags.IS_PLAYER_ATTACK))
        {
            EquipmentUpgrades upgrades = equipmentItem.getUpgrades(attacker.getWeaponItem());
            upgrades.applyDamageContextEffects(LTXIUpgradeEffectComponents.EQUIPMENT_PRE_ATTACK, (ServerLevel) attacker.level(), EnchantmentTarget.ATTACKER, event.getEntity(), attacker, source);
        }
    }

    // Run bubble shield checks on lowest to first apply damage modifiers
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingIncomingDamageLowest(final LivingIncomingDamageEvent event)
    {
        LivingEntity hurtEntity = event.getEntity();

        // Process bubble shield checks
        if (!hurtEntity.level().isClientSide())
        {
            BubbleShieldUser shieldUser = hurtEntity.getCapability(LTXICapabilities.ENTITY_BUBBLE_SHIELD);
            if (shieldUser != null)
            {
                if (shieldUser.blockDamage(hurtEntity.level(), event.getSource(), event.getAmount())) event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(final LivingDeathEvent event)
    {
        DamageSource damageSource = event.getSource();

        if (damageSource.getEntity() instanceof LivingEntity attacker && !attacker.level().isClientSide())
        {
            UpgradesContainerBase<?, ?> upgrades = null;

            // Get upgrades from upgradable sources
            if (damageSource instanceof UpgradableDamageSource upgradableSource)
            {
                upgrades = upgradableSource.getUpgrades();
            }
            // Otherwise get them from held item if melee attack
            else if (damageSource.getDirectEntity() == attacker && attacker.getWeaponItem().getItem() instanceof UpgradableEquipmentItem equipmentItem && damageSource.is(DamageTypeTags.IS_PLAYER_ATTACK))
            {
                upgrades = equipmentItem.getUpgrades(attacker.getWeaponItem());
            }

            // Apply on kill effects
            if (upgrades != null) upgrades.applyDamageContextEffects(LTXIUpgradeEffectComponents.EQUIPMENT_KILL, (ServerLevel) attacker.level(), EnchantmentTarget.ATTACKER, event.getEntity(), attacker, damageSource);
        }
    }
}