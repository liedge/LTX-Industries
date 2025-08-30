package liedge.ltxindustries.lib.upgrades;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.ltxindustries.lib.upgrades.effect.DoubleLevelBasedValue;
import liedge.ltxindustries.lib.upgrades.effect.TooltipValueFormat;
import liedge.ltxindustries.lib.upgrades.effect.ValueSentiment;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

public sealed interface TooltipArgument permits TooltipArgument.StaticArgument, TooltipArgument.RankValueArgument
{
    static TooltipArgument of(Component component)
    {
        return new StaticArgument(component);
    }

    static TooltipArgument of(DoubleLevelBasedValue value, ValueSentiment sentiment, TooltipValueFormat format, int zero)
    {
        return new RankValueArgument(value, sentiment, format, zero);
    }

    static TooltipArgument of(DoubleLevelBasedValue value, ValueSentiment sentiment, TooltipValueFormat format)
    {
        return of(value, sentiment, format, 0);
    }

    Codec<TooltipArgument> CODEC = LimaCoreCodecs.xorSubclassCodec(StaticArgument.CODEC, RankValueArgument.CODEC, StaticArgument.class, RankValueArgument.class);

    Component get(int upgradeRank);

    record StaticArgument(Component component) implements TooltipArgument
    {
        private static final Codec<StaticArgument> CODEC = ComponentSerialization.CODEC.xmap(StaticArgument::new, StaticArgument::component);

        @Override
        public Component get(int upgradeRank)
        {
            return component;
        }
    }

    record RankValueArgument(DoubleLevelBasedValue value, ValueSentiment sentiment, TooltipValueFormat format, int zero) implements TooltipArgument
    {
        private static final Codec<RankValueArgument> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                DoubleLevelBasedValue.CODEC.fieldOf("value").forGetter(RankValueArgument::value),
                ValueSentiment.CODEC.optionalFieldOf("sentiment", ValueSentiment.NEUTRAL).forGetter(RankValueArgument::sentiment),
                TooltipValueFormat.CODEC.optionalFieldOf("format", TooltipValueFormat.SIGNED_FLAT_NUMBER).forGetter(RankValueArgument::format),
                Codec.INT.optionalFieldOf("zero", 0).forGetter(RankValueArgument::zero))
                .apply(instance, RankValueArgument::new));

        @Override
        public Component get(int upgradeRank)
        {
            double amount = value.calculate(upgradeRank);
            return format.apply(amount, sentiment, zero);
        }
    }
}