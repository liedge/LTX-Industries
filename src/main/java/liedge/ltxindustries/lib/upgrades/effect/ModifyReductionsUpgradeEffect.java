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

public record ModifyReductionsUpgradeEffect(DamageReductionType reductionType, LevelBasedValue amount) implements UpgradeTooltipsProvider
{
    public static final Codec<ModifyReductionsUpgradeEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DamageReductionType.CODEC.fieldOf("reduction_type").forGetter(ModifyReductionsUpgradeEffect::reductionType),
            LevelBasedValue.CODEC.fieldOf("amount").forGetter(ModifyReductionsUpgradeEffect::amount))
            .apply(instance, ModifyReductionsUpgradeEffect::new));

    @Override
    public void addUpgradeTooltips(int upgradeRank, Consumer<Component> lines)
    {
        float tooltipAmount = Math.abs(amount.calculate(upgradeRank));
        Component tooltip = LTXILangKeys.REDUCTION_MODIFIER_EFFECT.translateArgs(ValueFormat.PERCENTAGE.apply(tooltipAmount, ValueSentiment.POSITIVE), reductionType.translate().withStyle(ChatFormatting.LIGHT_PURPLE));
        lines.accept(tooltip);
    }
}