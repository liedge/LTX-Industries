package liedge.ltxindustries.util;

import com.google.common.base.Preconditions;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.limacore.util.LimaCoreObjects;
import liedge.ltxindustries.entity.damage.UpgradesAwareDamageSource;
import liedge.ltxindustries.item.UpgradableEquipmentItem;
import liedge.ltxindustries.lib.upgrades.EffectRankPair;
import liedge.ltxindustries.lib.upgrades.UpgradedEquipmentInUse;
import liedge.ltxindustries.lib.upgrades.Upgrades;
import liedge.ltxindustries.lib.upgrades.effect.MinimumMachineSpeed;
import liedge.ltxindustries.lib.upgrades.effect.ValueOperation;
import liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.IntUnaryOperator;

public final class LTXIUpgradeUtil
{
    private LTXIUpgradeUtil() {}

    public static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    public static double runValuePairs(Collection<EffectRankPair<ValueOperation>> collection, LootContext context, double base, double initialValue)
    {
        double result = initialValue;

        for (EffectRankPair<ValueOperation> pair : collection)
        {
            ValueOperation effect = pair.effect();
            result = effect.apply(context, pair.upgradeRank(), base, result);
        }

        return result;
    }

    public static int calculateMachineSpeed(Upgrades upgrades, LootContext context, int baseSpeed, int minimumSpeed)
    {
        Preconditions.checkArgument(minimumSpeed >= 0, "Minimum speed must be at least 0");

        List<EffectRankPair<ValueOperation>> list = upgrades.getValuePairs(LTXIUpgradeEffectComponents.TICKS_PER_OPERATION.get());

        if (list.isEmpty() || baseSpeed <= minimumSpeed) return baseSpeed;

        double calculated = runValuePairs(list, context, baseSpeed, baseSpeed);
        return Math.max(minimumSpeed, LimaCoreMath.roundInt(calculated));
    }

    public static int calculateMachineSpeed(Upgrades upgrades, LootContext context, int baseSpeed)
    {
        int minimumSpeed = upgrades.effectStream(LTXIUpgradeEffectComponents.MINIMUM_MACHINE_SPEED).mapToInt(MinimumMachineSpeed::minimumSpeed).min().orElse(0);
        return calculateMachineSpeed(upgrades, context, baseSpeed, minimumSpeed);
    }

    public static IntUnaryOperator createMachineSpeedFunction(Upgrades upgrades, LootContext context)
    {
        List<EffectRankPair<ValueOperation>> list = upgrades.getValuePairs(LTXIUpgradeEffectComponents.TICKS_PER_OPERATION.get());
        if (list.isEmpty()) return IntUnaryOperator.identity();

        final int minimumSpeed = upgrades.effectStream(LTXIUpgradeEffectComponents.MINIMUM_MACHINE_SPEED).mapToInt(MinimumMachineSpeed::minimumSpeed).min().orElse(0);
        return baseSpeed ->
        {
            if (baseSpeed <= minimumSpeed) return baseSpeed;

            double calculated = runValuePairs(list, context, baseSpeed, baseSpeed);
            return Math.max(minimumSpeed, LimaCoreMath.roundInt(calculated));
        };
    }

    public static boolean iterateEquipmentSlots(LivingEntity sourceEntity, EquipmentSlot[] slots, EquipmentSlotVisitor visitor)
    {
        for (EquipmentSlot slot : slots)
        {
            boolean result = iterateEquipmentSlot(sourceEntity, slot, visitor);
            if (result) return true;
        }

        return false;
    }

    public static void runOnEquipmentSlots(LivingEntity sourceEntity, EquipmentSlot[] slots, EquipmentSlotRunner runner)
    {
        for (EquipmentSlot slot : slots)
        {
            iterateEquipmentSlot(sourceEntity, slot, runner);
        }
    }

    public static boolean iterateEquipmentSlot(LivingEntity sourceEntity, EquipmentSlot slot, EquipmentSlotVisitor visitor)
    {
        ItemStack stack = sourceEntity.getItemBySlot(slot);
        if (stack.getItem() instanceof UpgradableEquipmentItem equipmentItem && equipmentItem.isInCorrectSlot(slot))
        {
            Upgrades upgrades = equipmentItem.getUpgrades(stack);
            UpgradedEquipmentInUse equipmentInUse = UpgradedEquipmentInUse.create(upgrades, stack, equipmentItem, slot, sourceEntity);
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

                Upgrades upgrades = upgradesAwareSource.getUpgrades();
                ItemStack stack = Objects.requireNonNullElse(upgradesAwareSource.getWeaponItem(), ItemStack.EMPTY);
                UpgradableEquipmentItem equipmentItem = LimaCoreObjects.tryCast(UpgradableEquipmentItem.class, stack.getItem());
                EquipmentSlot slot = equipmentItem != null ? EquipmentSlot.MAINHAND : null;

                visitor.accept(level, upgrades, stack, equipmentItem, slot, attacker);
            }
            else if (attacker == source.getDirectEntity())
            {
                ItemStack stack = attacker.getMainHandItem();
                if (stack.getItem() instanceof UpgradableEquipmentItem equipmentItem)
                {
                    Upgrades upgrades = equipmentItem.getUpgrades(stack);
                    visitor.accept(level, upgrades, stack, equipmentItem, EquipmentSlot.MAINHAND, attacker);
                }
            }
        }
    }

    @FunctionalInterface
    public interface EquipmentSlotVisitor
    {
        boolean run(Upgrades upgrades, UpgradedEquipmentInUse equipmentInUse);
    }

    @FunctionalInterface
    public interface EquipmentSlotRunner extends EquipmentSlotVisitor
    {
        void accept(Upgrades upgrades, UpgradedEquipmentInUse equipmentInUse);

        @Override
        default boolean run(Upgrades upgrades, UpgradedEquipmentInUse equipmentInUse)
        {
            accept(upgrades, equipmentInUse);
            return false;
        }
    }

    @FunctionalInterface
    public interface DamageUpgradesVisitor
    {
        void accept(ServerLevel level, Upgrades upgrades, UpgradedEquipmentInUse equipmentInUse);

        default void accept(ServerLevel level, Upgrades upgrades, ItemStack stack, @Nullable UpgradableEquipmentItem item, @Nullable EquipmentSlot slot, LivingEntity attacker)
        {
            accept(level, upgrades, UpgradedEquipmentInUse.create(upgrades, stack, item, slot, attacker));
        }
    }
}