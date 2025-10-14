package liedge.ltxindustries.lib.upgrades.tooltip;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.lib.Translatable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.Style;

import java.util.List;

public sealed interface UpgradeTooltip permits UpgradeTooltip.StaticTooltip, UpgradeTooltip.ArgsTooltip
{
    static UpgradeTooltip of(Component component)
    {
        return new StaticTooltip(component);
    }

    static UpgradeTooltip of(String key, Style style, TooltipArgument... args)
    {
        return new ArgsTooltip(key, style, List.of(args));
    }

    static UpgradeTooltip of(String key, TooltipArgument... args)
    {
        return of(key, Style.EMPTY, args);
    }

    static UpgradeTooltip of(Translatable translatable, Style style, TooltipArgument... args)
    {
        return of(translatable.descriptionId(), style, args);
    }

    static UpgradeTooltip of(Translatable translatable, TooltipArgument... args)
    {
        return of(translatable.descriptionId(), args);
    }

    Codec<UpgradeTooltip> CODEC = LimaCoreCodecs.xorSubclassCodec(StaticTooltip.CODEC, ArgsTooltip.CODEC, StaticTooltip.class, ArgsTooltip.class);

    Component get(int upgradeRank);

    record StaticTooltip(Component component) implements UpgradeTooltip
    {
        private static final Codec<StaticTooltip> CODEC = ComponentSerialization.CODEC.xmap(StaticTooltip::new, StaticTooltip::component);

        @Override
        public Component get(int upgradeRank)
        {
            return component;
        }
    }

    record ArgsTooltip(String key, Style style, List<TooltipArgument> args) implements UpgradeTooltip
    {
        private static final Codec<ArgsTooltip> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("key").forGetter(ArgsTooltip::key),
                Style.Serializer.CODEC.optionalFieldOf("style", Style.EMPTY).forGetter(ArgsTooltip::style),
                TooltipArgument.CODEC.listOf(1, 8).fieldOf("args").forGetter(ArgsTooltip::args))
                .apply(instance, ArgsTooltip::new));

        @Override
        public Component get(int upgradeRank)
        {
            Object[] resolvedArgs = args.stream().map(a -> a.get(upgradeRank)).toArray();
            return Component.translatable(key, resolvedArgs).withStyle(style);
        }
    }
}