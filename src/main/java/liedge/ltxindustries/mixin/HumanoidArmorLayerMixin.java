package liedge.ltxindustries.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import liedge.ltxindustries.item.UpgradableEquipmentItem;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin
{
    @ModifyReturnValue(method = "shouldRender(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;)Z", at = @At(value = "TAIL"))
    private static boolean forceRenderWonderlandArmor(boolean original, ItemStack stack, EquipmentSlot slot)
    {
        if (!original)
        {
            if (stack.getItem() instanceof UpgradableEquipmentItem equipmentItem && equipmentItem.getEquipmentSlot() == slot) return true;
        }

        return original;
    }
}