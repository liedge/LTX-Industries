package liedge.ltxindustries.lib.upgrades;

import liedge.ltxindustries.entity.LTXIEntityUtil;
import liedge.ltxindustries.entity.TargetPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record UpgradedEquipmentInUse(ItemStack stack, UpgradesContainerBase<?, ?> upgrades, @Nullable EquipmentSlot slot, @Nullable LivingEntity owner, TargetPredicate targetPredicate)
{
    public static UpgradedEquipmentInUse create(ServerLevel level, ItemStack stack, UpgradesContainerBase<?, ?> upgrades, @Nullable EquipmentSlot slot, @Nullable LivingEntity owner)
    {
        TargetPredicate predicate = TargetPredicate.create(level, upgrades);
        return new UpgradedEquipmentInUse(stack, upgrades, slot, owner, predicate);
    }

    public boolean canAttack(Entity targetEntity)
    {
        return LTXIEntityUtil.checkBaseTargetValidity(owner, targetEntity) && !targetPredicate.test(targetEntity, owner).isFalse();
    }
}