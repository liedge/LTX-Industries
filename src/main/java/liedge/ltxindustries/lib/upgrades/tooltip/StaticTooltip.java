package liedge.ltxindustries.lib.upgrades.tooltip;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

public record StaticTooltip(Component component) implements UpgradeComponentLike
{
    static final MapCodec<StaticTooltip> CODEC = ComponentSerialization.CODEC.xmap(StaticTooltip::new, StaticTooltip::component).fieldOf("tooltip");

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