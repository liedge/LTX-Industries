package liedge.ltxindustries.lib.upgrades;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.lib.Translatable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.Style;

import java.util.List;

public sealed interface UpgradeTooltip permits UpgradeTooltip.StaticTooltip, UpgradeTooltip.DynamicTooltip
{
    static UpgradeTooltip of(Component component)
    {
        return new StaticTooltip(component);
    }

    static UpgradeTooltip of(String key, Style style, TooltipArgument... args)
    {
        return new DynamicTooltip(key, style, List.of(args));
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

    Codec<UpgradeTooltip> CODEC = LimaCoreCodecs.xorSubclassCodec(StaticTooltip.CODEC, DynamicTooltip.CODEC, StaticTooltip.class, DynamicTooltip.class);

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

    final class DynamicTooltip implements UpgradeTooltip
    {
        private static final Codec<DynamicTooltip> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("key").forGetter(o -> o.key),
                Style.Serializer.CODEC.optionalFieldOf("style", Style.EMPTY).forGetter(o -> o.style),
                TooltipArgument.CODEC.listOf(1, 16).fieldOf("args").forGetter(o -> o.args))
                .apply(instance, DynamicTooltip::new));

        private final String key;
        private final Style style;
        private final List<TooltipArgument> args;

        public DynamicTooltip(String key, Style style, List<TooltipArgument> args)
        {
            this.key = key;
            this.style = style;
            this.args = args;
        }

        @Override
        public Component get(int upgradeRank)
        {
            Object[] varargs = args.stream().map(o -> o.get(upgradeRank)).toArray();
            return Component.translatable(key, varargs).withStyle(style);
        }
    }
}