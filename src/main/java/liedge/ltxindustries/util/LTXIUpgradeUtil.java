package liedge.ltxindustries.util;

import liedge.limacore.util.LimaCoreUtil;
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

    public static boolean iterateEquipmentSlots(ServerLevel level, LivingEntity sourceEntity, EquipmentSlot[] slots, EquipmentSlotVisitor visitor)
    {
        for (EquipmentSlot slot : slots)
        {
            boolean result = iterateEquipmentSlot(level, sourceEntity, slot, visitor);
            if (result) return true;
        }

        return false;
    }

    public static void runOnEquipmentSlots(ServerLevel level, LivingEntity sourceEntity, EquipmentSlot[] slots, EquipmentSlotRunner runner)
    {
        for (EquipmentSlot slot : slots)
        {
            iterateEquipmentSlot(level, sourceEntity, slot, runner);
        }
    }

    public static boolean iterateEquipmentSlot(ServerLevel level, LivingEntity sourceEntity, EquipmentSlot slot, EquipmentSlotVisitor visitor)
    {
        ItemStack stack = sourceEntity.getItemBySlot(slot);
        if (stack.getItem() instanceof UpgradableEquipmentItem equipmentItem && equipmentItem.isInCorrectSlot(slot))
        {
            EquipmentUpgrades upgrades = equipmentItem.getUpgrades(stack);
            UpgradedEquipmentInUse equipmentInUse = UpgradedEquipmentInUse.create(level, upgrades, stack, equipmentItem, slot, sourceEntity);
            return visitor.run(upgrades, equipmentInUse);
        }

        return false;
    }

    public static void iterateDamageUpgrades(DamageSource source, DamageUpgradesVisitor visitor)
    {
        if (source.getEntity() instanceof LivingEntity attacker && attacker.level() instanceof ServerLevel level)
        {
            if (source instanceof UpgradesAwareDamageSource upgradesAwareSource)
            {
                if (!upgradesAwareSource.canApplyEffects()) return;

                UpgradesContainerBase<?, ?> upgrades = upgradesAwareSource.getUpgrades();
                ItemStack stack = Objects.requireNonNullElse(upgradesAwareSource.getWeaponItem(), ItemStack.EMPTY);
                UpgradableEquipmentItem equipmentItem = LimaCoreUtil.castOrNull(UpgradableEquipmentItem.class, stack.getItem());
                EquipmentSlot slot = equipmentItem != null ? EquipmentSlot.MAINHAND : null;

                visitor.accept(level, upgrades, stack, equipmentItem, slot, attacker);
            }
            else if (attacker == source.getDirectEntity())
            {
                ItemStack stack = attacker.getMainHandItem();
                if (stack.getItem() instanceof UpgradableEquipmentItem equipmentItem)
                {
                    EquipmentUpgrades upgrades = equipmentItem.getUpgrades(stack);
                    visitor.accept(level, upgrades, stack, equipmentItem, EquipmentSlot.MAINHAND, attacker);
                }
            }
        }
    }

    @FunctionalInterface
    public interface EquipmentSlotVisitor
    {
        boolean run(UpgradesContainerBase<?, ?> upgrades, UpgradedEquipmentInUse equipmentInUse);
    }

    @FunctionalInterface
    public interface EquipmentSlotRunner extends EquipmentSlotVisitor
    {
        void accept(UpgradesContainerBase<?, ?> upgrades, UpgradedEquipmentInUse equipmentInUse);

        @Override
        default boolean run(UpgradesContainerBase<?, ?> upgrades, UpgradedEquipmentInUse equipmentInUse)
        {
            accept(upgrades, equipmentInUse);
            return false;
        }
    }

    @FunctionalInterface
    public interface DamageUpgradesVisitor
    {
        void accept(ServerLevel level, UpgradesContainerBase<?, ?> upgrades, UpgradedEquipmentInUse equipmentInUse);

        default void accept(ServerLevel level, UpgradesContainerBase<?, ?> upgrades, ItemStack stack, @Nullable UpgradableEquipmentItem item, @Nullable EquipmentSlot slot, LivingEntity attacker)
        {
            accept(level, upgrades, UpgradedEquipmentInUse.create(level, upgrades, stack, item, slot, attacker));
        }
    }
}