package liedge.limatech.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import liedge.limatech.entity.LimaTechEntityUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
    @WrapOperation(method = "forceAddEffect", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/common/CommonHooks;canMobEffectBeApplied(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/effect/MobEffectInstance;)Z"))
    private boolean neuroEffectCheck2(LivingEntity entity, MobEffectInstance effect, Operation<Boolean> original, @Nullable @Local(argsOnly = true) Entity applyingEntity)
    {
        if (LimaTechEntityUtil.checkIfEntityHasNeuroEffect(applyingEntity)) return false;
        return original.call(entity, effect);
    }

    @WrapOperation(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/common/CommonHooks;canMobEffectBeApplied(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/effect/MobEffectInstance;)Z"))
    private boolean neuroEffectCheck1(LivingEntity entity, MobEffectInstance effect, Operation<Boolean> original, @Nullable @Local(argsOnly = true) Entity applyingEntity)
    {
        if (LimaTechEntityUtil.checkIfEntityHasNeuroEffect(applyingEntity)) return false;
        return original.call(entity, effect);
    }
}