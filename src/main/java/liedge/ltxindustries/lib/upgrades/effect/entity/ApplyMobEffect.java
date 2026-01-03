package liedge.ltxindustries.lib.upgrades.effect.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.ltxindustries.lib.upgrades.UpgradedEquipmentInUse;
import liedge.ltxindustries.registry.game.LTXIEntityUpgradeEffects;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.LootContext;

public record ApplyMobEffect(Holder<MobEffect> effect, LevelBasedValue duration, LevelBasedValue amplifier, boolean ambient, boolean visible) implements EntityUpgradeEffect
{
    public static final MapCodec<ApplyMobEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            MobEffect.CODEC.fieldOf("effect").forGetter(ApplyMobEffect::effect),
            LevelBasedValue.CODEC.fieldOf("duration").forGetter(ApplyMobEffect::duration),
            LevelBasedValue.CODEC.optionalFieldOf("amplifier", LevelBasedValue.constant(0)).forGetter(ApplyMobEffect::amplifier),
            Codec.BOOL.optionalFieldOf("ambient", false).forGetter(ApplyMobEffect::ambient),
            Codec.BOOL.optionalFieldOf("visible", true).forGetter(ApplyMobEffect::visible))
            .apply(instance, ApplyMobEffect::new));

    public static ApplyMobEffect applyEffect(Holder<MobEffect> effect, LevelBasedValue duration, LevelBasedValue amplifier, boolean ambient, boolean visible)
    {
        return new ApplyMobEffect(effect, duration, amplifier, ambient, visible);
    }

    public static ApplyMobEffect applyEffect(Holder<MobEffect> effect, LevelBasedValue duration, LevelBasedValue amplifier)
    {
        return applyEffect(effect, duration, amplifier, false, true);
    }

    public static ApplyMobEffect applyEffect(Holder<MobEffect> effect, LevelBasedValue duration)
    {
        return applyEffect(effect, duration, LevelBasedValue.constant(0));
    }

    @Override
    public void apply(ServerLevel level, LootContext context, int upgradeRank, Entity affectedEntity, UpgradedEquipmentInUse equipmentInUse)
    {
        if (affectedEntity instanceof LivingEntity livingEntity)
        {
            int length = Math.round(duration.calculate(upgradeRank));
            int amp = Math.round(amplifier.calculate(upgradeRank));

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
}