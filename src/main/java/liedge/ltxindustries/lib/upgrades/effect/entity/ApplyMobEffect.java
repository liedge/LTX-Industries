package liedge.ltxindustries.lib.upgrades.effect.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.ltxindustries.registry.game.LTXIEntityUpgradeEffects;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public record ApplyMobEffect(Holder<MobEffect> effect, LevelBasedValue duration, LevelBasedValue amplifier) implements EntityUpgradeEffect
{
    public static final MapCodec<ApplyMobEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            MobEffect.CODEC.fieldOf("effect").forGetter(ApplyMobEffect::effect),
            LevelBasedValue.CODEC.fieldOf("duration").forGetter(ApplyMobEffect::duration),
            LevelBasedValue.CODEC.optionalFieldOf("amplifier", LevelBasedValue.constant(0)).forGetter(ApplyMobEffect::amplifier))
            .apply(instance, ApplyMobEffect::new));

    public static ApplyMobEffect applyEffect(Holder<MobEffect> effect, LevelBasedValue duration, LevelBasedValue amplifier)
    {
        return new ApplyMobEffect(effect, duration, amplifier);
    }

    public static ApplyMobEffect applyEffect(Holder<MobEffect> effect, LevelBasedValue duration)
    {
        return applyEffect(effect, duration, LevelBasedValue.constant(0));
    }

    @Override
    public void applyEntityEffect(ServerLevel level, Entity entity, int upgradeRank, LootContext context)
    {
        if (entity instanceof LivingEntity livingEntity)
        {
            int duration = Math.round(this.duration.calculate(upgradeRank));
            int amplifier = Math.round(this.amplifier.calculate(upgradeRank));

            livingEntity.addEffect(new MobEffectInstance(effect, duration, amplifier), context.getParamOrNull(LootContextParams.ATTACKING_ENTITY));
        }
    }

    @Override
    public EntityUpgradeEffectType<?> getType()
    {
        return LTXIEntityUpgradeEffects.APPLY_MOB_EFFECT.get();
    }
}