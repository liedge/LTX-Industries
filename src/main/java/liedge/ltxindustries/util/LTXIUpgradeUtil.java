package liedge.ltxindustries.util;

import liedge.ltxindustries.entity.damage.UpgradesAwareDamageSource;
import liedge.ltxindustries.item.UpgradableEquipmentItem;
import liedge.ltxindustries.lib.upgrades.UpgradedEquipmentInUse;
import liedge.ltxindustries.lib.upgrades.UpgradesContainerBase;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrades;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class LTXIUpgradeUtil
{
    private LTXIUpgradeUtil() {}

    public static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    public static void iterateEquipmentUpgrades(LivingEntity sourceEntity, EquipmentSlot[] slots, UpgradeContainerSlotVisitor visitor)
    {
        if (sourceEntity.level() instanceof ServerLevel level)
        {
            for (EquipmentSlot slot : slots)
            {
                ItemStack stack = sourceEntity.getItemBySlot(slot);
                if (stack.getItem() instanceof UpgradableEquipmentItem item)
                {
                    EquipmentUpgrades upgrades = item.getUpgrades(stack);
                    visitor.accept(level, upgrades, stack, slot, sourceEntity);
                }
            }
        }
    }

    public static void iterateDamageUpgrades(DamageSource source, UpgradeContainerSlotVisitor visitor)
    {
        if (source.getEntity() instanceof LivingEntity attacker && attacker.level() instanceof ServerLevel level)
        {
            if (source instanceof UpgradesAwareDamageSource upgradesAwareSource)
            {
                if (!upgradesAwareSource.canApplyEffects()) return;

                ItemStack stack = Objects.requireNonNullElse(upgradesAwareSource.getWeaponItem(), ItemStack.EMPTY);
                UpgradesContainerBase<?, ?> upgrades = upgradesAwareSource.getUpgrades();
                EquipmentSlot slot = stack.isEmpty() ? null : EquipmentSlot.MAINHAND;

                visitor.accept(level, upgrades, stack, slot, attacker);
            }
            else if (attacker == source.getDirectEntity())
            {
                ItemStack stack = attacker.getMainHandItem();
                EquipmentUpgrades upgrades = UpgradableEquipmentItem.getEquipmentUpgradesFromStack(stack);

                visitor.accept(level, upgrades, stack, EquipmentSlot.MAINHAND, attacker);
            }
        }
    }

    @FunctionalInterface
    public interface UpgradeContainerSlotVisitor
    {
        void accept(ServerLevel level, UpgradesContainerBase<?, ?> upgrades, UpgradedEquipmentInUse equipmentInUse);

        default void accept(ServerLevel level, UpgradesContainerBase<?, ?> upgrades, ItemStack stack, @Nullable EquipmentSlot slot, @Nullable LivingEntity owner)
        {
            accept(level, upgrades, UpgradedEquipmentInUse.create(level, stack, upgrades, slot, owner));
        }
    }
}