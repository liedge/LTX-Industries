package liedge.ltxindustries.lib.upgrades.tooltip;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.lib.Translatable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.util.List;

public record TranslatableTooltip(String key, Style style, List<UpgradeComponentLike> args) implements UpgradeComponentLike
{
    static MapCodec<TranslatableTooltip> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("key").forGetter(TranslatableTooltip::key),
            Style.Serializer.CODEC.optionalFieldOf("style", Style.EMPTY).forGetter(TranslatableTooltip::style),
            UpgradeComponentLike.CODEC.listOf(1, 8).fieldOf("args").forGetter(TranslatableTooltip::args))
            .apply(instance, TranslatableTooltip::new));

    public static TranslatableTooltip create(String key, Style style, UpgradeComponentLike... args)
    {
        return new TranslatableTooltip(key, style, List.of(args));
    }

    public static TranslatableTooltip create(String key, UpgradeComponentLike... args)
    {
        return create(key, Style.EMPTY, args);
    }

    public static TranslatableTooltip create(Translatable translatable, Style style, UpgradeComponentLike... args)
    {
        return create(translatable.descriptionId(), style, args);
    }

    public static TranslatableTooltip create(Translatable translatable, UpgradeComponentLike... args)
    {
        return create(translatable.descriptionId(), args);
    }

    @Override
    public Component get(int upgradeRank)
    {
        Object[] resolvedArgs = args.stream().map(o -> o.get(upgradeRank)).toArray();
        return Component.translatable(key, resolvedArgs).withStyle(style);
    }

    @Override
    public Type getType()
    {
        return Type.CUSTOM_TRANSLATABLE;
    }
}