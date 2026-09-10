package liedge.ltxindustries.data;

import com.google.common.collect.Iterators;
import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.*;

public final class LightChannels implements Iterable<LightColors.Channel>
{
    public static final LightChannels NONE = new LightChannels(Set.of());
    public static final Codec<LightChannels> CODEC = LightColors.Channel.CODEC.listOf().xmap(LightChannels::of, o -> List.copyOf(o.set));
    public static final StreamCodec<FriendlyByteBuf, LightChannels> STREAM_CODEC = LightColors.Channel.STREAM_CODEC.apply(ByteBufCodecs.list()).map(LightChannels::of, o -> List.copyOf(o.set));

    public static final LightChannels DEFAULT_WEAPON_CHANNELS = of(LightColors.Channel.PRIMARY, LightColors.Channel.ENERGY);
    public static final LightChannels DEFAULT_TOOL_CHANNELS = of(LightColors.Channel.ENERGY);

    public static LightChannels of(Collection<LightColors.Channel> channels)
    {
        Set<LightColors.Channel> set = channels.isEmpty() ? Set.of() : EnumSet.copyOf(channels);
        return createInternal(set);
    }

    public static LightChannels of(LightColors.Channel... channels)
    {
        return of(List.of(channels));
    }

    private static LightChannels createInternal(Set<LightColors.Channel> set)
    {
        return set.isEmpty() ? NONE : new LightChannels(set);
    }

    // Class def

    private final Set<LightColors.Channel> set;

    private LightChannels(Set<LightColors.Channel> set)
    {
        this.set = set;
    }

    public boolean contains(LightColors.Channel channel)
    {
        return set.contains(channel);
    }

    public Set<LightColors.Channel> copy()
    {
        return Set.copyOf(set);
    }

    @Override
    public Iterator<LightColors.Channel> iterator()
    {
        return Iterators.unmodifiableIterator(set.iterator());
    }

    @Override
    public int hashCode()
    {
        return set.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
            return true;
        else if (!(obj instanceof LightChannels other))
            return false;
        else
            return this.set.equals(other.set);
    }
}