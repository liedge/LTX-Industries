package liedge.ltxindustries.lib.upgrades.effect.equipment;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.registry.game.LTXIEquipmentUpgradeEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public record MobEffectUpgradeEffect(Holder<MobEffect> effect, LevelBasedValue duration, LevelBasedValue amplifier) implements EquipmentUpgradeEffect
{
    public static final MapCodec<MobEffectUpgradeEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            MobEffect.CODEC.fieldOf("effect").forGetter(MobEffectUpgradeEffect::effect),
            LevelBasedValue.CODEC.fieldOf("duration").forGetter(MobEffectUpgradeEffect::duration),
            LevelBasedValue.CODEC.optionalFieldOf("amplifier", LevelBasedValue.constant(0)).forGetter(MobEffectUpgradeEffect::amplifier))
            .apply(instance, MobEffectUpgradeEffect::new));

    public static MobEffectUpgradeEffect create(Holder<MobEffect> effect, LevelBasedValue duration)
    {
        return new MobEffectUpgradeEffect(effect, duration, LevelBasedValue.constant(0f));
    }

    @Override
    public void applyEquipmentEffect(ServerLevel level, Entity entity, int upgradeRank, LootContext context)
    {
        if (entity instanceof LivingEntity livingEntity)
        {
            int duration = Math.round(this.duration.calculate(upgradeRank));
            int amplifier = Math.round(this.amplifier.calculate(upgradeRank));

            livingEntity.addEffect(new MobEffectInstance(effect, duration, amplifier), context.getParamOrNull(LootContextParams.ATTACKING_ENTITY));
        }
    }

    @Override
    public MapCodec<? extends EquipmentUpgradeEffect> codec()
    {
        return LTXIEquipmentUpgradeEffects.MOB_EFFECT_EQUIPMENT_EFFECT.get();
    }

    @Override
    public Component getEffectTooltip(int upgradeRank)
    {
        MutableComponent nameComponent = effect.value().getDisplayName().copy();

        int amplifier = Math.round(this.amplifier.calculate(upgradeRank));
        if (amplifier > 0 && amplifier < 10)
        {
            nameComponent.append(CommonComponents.SPACE).append(Component.translatable("enchantment.level." + (amplifier + 1)));
        }

        int duration = Math.round(this.duration.calculate(upgradeRank));
        Component durationComponent = Component.literal(StringUtil.formatTickDuration(duration, 20));

        return LTXILangKeys.MOB_EFFECT_UPGRADE_EFFECT.translateArgs(nameComponent, durationComponent).withStyle(ChatFormatting.GOLD);
    }
}