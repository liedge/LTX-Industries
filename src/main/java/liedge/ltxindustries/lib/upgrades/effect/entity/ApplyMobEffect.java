package liedge.ltxindustries.lib.upgrades.effect.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.ltxindustries.lib.upgrades.UpgradedEquipmentInUse;
import liedge.ltxindustries.lib.upgrades.value.ConstantDouble;
import liedge.ltxindustries.lib.upgrades.value.ContextlessValue;
import liedge.ltxindustries.lib.upgrades.value.UpgradeValueProvider;
import liedge.ltxindustries.registry.game.LTXIEntityUpgradeEffects;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

import java.util.Set;

public record ApplyMobEffect(Holder<MobEffect> effect, UpgradeValueProvider duration, ContextlessValue amplifier, boolean ambient, boolean visible) implements EntityUpgradeEffect
{
    public static final MapCodec<ApplyMobEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            MobEffect.CODEC.fieldOf("effect").forGetter(ApplyMobEffect::effect),
            UpgradeValueProvider.DIRECT_CODEC.fieldOf("duration").forGetter(ApplyMobEffect::duration),
            ContextlessValue.CODEC.optionalFieldOf("amplifier", ConstantDouble.of(0)).forGetter(ApplyMobEffect::amplifier),
            Codec.BOOL.optionalFieldOf("ambient", false).forGetter(ApplyMobEffect::ambient),
            Codec.BOOL.optionalFieldOf("visible", true).forGetter(ApplyMobEffect::visible))
            .apply(instance, ApplyMobEffect::new));

    public static ApplyMobEffect applyEffect(Holder<MobEffect> effect, UpgradeValueProvider duration, ContextlessValue amplifier, boolean ambient, boolean visible)
    {
        return new ApplyMobEffect(effect, duration, amplifier, ambient, visible);
    }

    public static ApplyMobEffect applyPassiveEffect(Holder<MobEffect> effect, UpgradeValueProvider duration, ContextlessValue amplifier)
    {
        return applyEffect(effect, duration, amplifier, true, false);
    }

    public static ApplyMobEffect applyEffect(Holder<MobEffect> effect, UpgradeValueProvider duration, ContextlessValue amplifier)
    {
        return applyEffect(effect, duration, amplifier, false, true);
    }

    public static ApplyMobEffect applyEffect(Holder<MobEffect> effect, UpgradeValueProvider duration)
    {
        return applyEffect(effect, duration, ConstantDouble.of(0));
    }

    @Override
    public void apply(ServerLevel level, LootContext context, int upgradeRank, Entity affectedEntity, UpgradedEquipmentInUse equipmentInUse)
    {
        if (affectedEntity instanceof LivingEntity livingEntity)
        {
            int length = duration.getInt(context, upgradeRank);
            int amp = amplifier.calculateInt(upgradeRank);

            if (effect.value().isBeneficial() || equipmentInUse.canAttack(livingEntity))
            {
                MobEffectInstance instance = new MobEffectInstance(effect, length, amp, ambient, visible);
                livingEntity.addEffect(instance, equipmentInUse.owner());
            }
        }
    }

    @Override
    public EntityUpgradeEffectType<?> getType()
    {
        return LTXIEntityUpgradeEffects.APPLY_MOB_EFFECT.get();
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams()
    {
        return duration.getReferencedContextParams();
    }
}