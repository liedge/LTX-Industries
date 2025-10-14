package liedge.ltxindustries.lib.upgrades.tooltip;

import com.mojang.serialization.Codec;
import liedge.limacore.data.LimaCoreCodecs;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

public sealed interface TooltipArgument permits TooltipArgument.StaticArgument, ValueArgument
{
    Codec<TooltipArgument> CODEC = LimaCoreCodecs.xorSubclassCodec(StaticArgument.CODEC, ValueArgument.CODEC, StaticArgument.class, ValueArgument.class);

    static TooltipArgument of(Component component)
    {
        return new StaticArgument(component);
    }

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
}