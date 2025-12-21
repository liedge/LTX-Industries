package liedge.ltxindustries.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.lib.damage.DamageReductionType;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.lib.upgrades.tooltip.UpgradeTooltipsProvider;
import liedge.ltxindustries.lib.upgrades.tooltip.ValueFormat;
import liedge.ltxindustries.lib.upgrades.tooltip.ValueSentiment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.enchantment.LevelBasedValue;

import java.util.function.Consumer;

public record BreachDamageReduction(DamageReductionType reduction, LevelBasedValue amount) implements UpgradeTooltipsProvider
{
    public static final Codec<BreachDamageReduction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DamageReductionType.CODEC.fieldOf("reduction").forGetter(BreachDamageReduction::reduction),
            LevelBasedValue.CODEC.fieldOf("amount").forGetter(BreachDamageReduction::amount))
            .apply(instance, BreachDamageReduction::new));

    public static BreachDamageReduction create(DamageReductionType reductionType, LevelBasedValue amount)
    {
        return new BreachDamageReduction(reductionType, amount);
    }

    public float get(int upgradeRank)
    {
        return Math.max(amount.calculate(upgradeRank), 0f);
    }

    @Override
    public void addUpgradeTooltips(int upgradeRank, Consumer<Component> lines)
    {
        lines.accept(LTXILangKeys.REDUCTION_MODIFIER_EFFECT.translateArgs(
                ValueFormat.PERCENTAGE.apply(get(upgradeRank), ValueSentiment.POSITIVE),
                reduction.translate().withStyle(ChatFormatting.LIGHT_PURPLE)));
    }
}