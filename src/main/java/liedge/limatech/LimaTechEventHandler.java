package liedge.limatech;

import liedge.limacore.util.LimaCoreUtil;
import liedge.limatech.entity.BubbleShieldUser;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.lib.weapons.WeaponDamageSource;
import liedge.limatech.network.packet.ClientboundBubbleShieldPacket;
import liedge.limatech.registry.LimaTechAttachmentTypes;
import liedge.limatech.registry.LimaTechTriggerTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = LimaTech.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class LimaTechEventHandler
{
    private LimaTechEventHandler() {}

    @SubscribeEvent
    public static void onPlayerTick(final PlayerTickEvent.Pre event)
    {
        if (event.getEntity() instanceof ServerPlayer serverPlayer)
        {
            ItemStack heldItem = serverPlayer.getMainHandItem();
            WeaponItem weaponItem = LimaCoreUtil.castOrNull(WeaponItem.class, heldItem.getItem());
            serverPlayer.getData(LimaTechAttachmentTypes.WEAPON_INPUT).tickInput(serverPlayer, heldItem, weaponItem);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(final PlayerEvent.PlayerChangedDimensionEvent event)
    {
        Player player = event.getEntity();
        if (!player.level().isClientSide())
        {
            ClientboundBubbleShieldPacket.sendShieldToTrackers(player);
        }
    }

    @SubscribeEvent
    public static void onStartTrackingEntity(final PlayerEvent.StartTracking event)
    {
        if (!event.getTarget().level().isClientSide() && event.getTarget() instanceof LivingEntity livingEntity)
        {
            ClientboundBubbleShieldPacket.sendShieldToTrackers(livingEntity);
        }
    }

    @SubscribeEvent
    public static void onLivingTick(final EntityTickEvent.Pre event)
    {
        if (event.getEntity() instanceof LivingEntity livingEntity)
        {
            livingEntity.getExistingData(LimaTechAttachmentTypes.BUBBLE_SHIELD).ifPresent(shield -> shield.tickShield(livingEntity));
        }
    }


    @SubscribeEvent
    public static void onLivingIncomingDamage(final LivingIncomingDamageEvent event)
    {
        LivingEntity hurtEntity = event.getEntity();
        DamageSource source = event.getSource();

        if (!hurtEntity.level().isClientSide())
        {
            BubbleShieldUser shieldUser = hurtEntity.getCapability(LimaTechCapabilities.ENTITY_BUBBLE_SHIELD);
            if (shieldUser != null)
            {
                if (shieldUser.blockDamage(hurtEntity.level(), source, event.getAmount())) event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(final LivingDeathEvent event)
    {
        if (event.getSource() instanceof WeaponDamageSource damageSource && damageSource.getEntity() instanceof LivingEntity killer)
        {
            LivingEntity killedEntity = event.getEntity();
            damageSource.getKillerWeapon().killedByWeapon(killer, killedEntity);
            if (killer instanceof ServerPlayer serverPlayer)
            {
                LimaTechTriggerTypes.KILLED_WITH_WEAPON.get().trigger(serverPlayer, killedEntity, damageSource);
            }
        }
    }
}