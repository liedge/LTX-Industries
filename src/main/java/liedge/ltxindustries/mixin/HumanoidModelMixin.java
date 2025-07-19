package liedge.ltxindustries.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import liedge.ltxindustries.item.weapon.WeaponItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin
{
    @WrapOperation(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isUsingItem()Z", ordinal = 0))
    private boolean forcePosingBothArms(LivingEntity entity, Operation<Boolean> original)
    {
        boolean isUsingItem = original.call(entity);

        if (isUsingItem && entity.getUsedItemHand() == InteractionHand.OFF_HAND)
        {
            ItemStack mainHandItem = entity.getMainHandItem();
            return !(mainHandItem.getItem() instanceof WeaponItem weaponItem && weaponItem.isOneHanded(mainHandItem));
        }

        return isUsingItem;
    }
}