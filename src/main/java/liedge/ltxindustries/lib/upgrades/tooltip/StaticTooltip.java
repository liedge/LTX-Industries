package liedge.ltxindustries.lib.upgrades.tooltip;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

public record StaticTooltip(Component component) implements UpgradeComponentLike
{
    static final Codec<StaticTooltip> INLINE_CODEC = ComponentSerialization.CODEC.xmap(StaticTooltip::new, StaticTooltip::component);
    static final MapCodec<StaticTooltip> CODEC = INLINE_CODEC.fieldOf("component");

    public static StaticTooltip of(Component component)
    {
        return new StaticTooltip(component);
    }

    @Override
    public Component get(int upgradeRank)
    {
        return component;
    }

    @Override
    public Type getType()
    {
        return Type.STATIC_COMPONENT;
    }
}