package liedge.ltxindustries.lib.upgrades;

import liedge.ltxindustries.entity.LTXIEntityUtil;
import liedge.ltxindustries.entity.TargetPredicate;
import liedge.ltxindustries.item.UpgradableEquipmentItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record UpgradedEquipmentInUse(UpgradesContainerBase<?, ?> upgrades, ItemStack stack, @Nullable UpgradableEquipmentItem item, @Nullable EquipmentSlot slot, @Nullable LivingEntity owner, TargetPredicate filter)
{
    public static UpgradedEquipmentInUse create(ServerLevel level, UpgradesContainerBase<?, ?> upgrades, ItemStack stack, @Nullable UpgradableEquipmentItem item, @Nullable EquipmentSlot slot, @Nullable LivingEntity owner)
    {
        return new UpgradedEquipmentInUse(upgrades, stack, item, slot, owner, TargetPredicate.create(level, upgrades));
    }

    public boolean canAttack(Entity targetEntity)
    {
        return LTXIEntityUtil.checkBaseTargetValidity(owner, targetEntity) && !filter.test(targetEntity, owner).isFalse();
    }

    public boolean useEnergyActions(int actions)
    {
        if (item == null || owner == null) return false;

        return item.consumeEnergyActions(owner, stack, actions);
    }
}