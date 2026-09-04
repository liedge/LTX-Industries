package liedge.ltxindustries.data;

import com.mojang.serialization.Codec;
import liedge.limacore.data.LimaEnumCodec;
import liedge.limacore.data.MapLikeData;
import liedge.limacore.lib.LimaColor;
import liedge.ltxindustries.LTXIConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jspecify.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public final class LightColors extends MapLikeData<LightColors.Channel, Integer>
{
    public static final Codec<LightColors> CODEC = Codec.unboundedMap(Channel.CODEC, ExtraCodecs.ARGB_COLOR_CODEC).xmap(LightColors::decode, MapLikeData::getMap);
    private static final StreamCodec<FriendlyByteBuf, Map<Channel, Integer>> MAP_STREAM_CODEC = ByteBufCodecs.map(_ -> new EnumMap<>(Channel.class), Channel.STREAM_CODEC, ByteBufCodecs.VAR_INT);
    public static final StreamCodec<FriendlyByteBuf, LightColors> STREAM_CODEC = MAP_STREAM_CODEC.map(LightColors::new, MapLikeData::getMap);

    // Useful colors
    public static final LightColors EMPTY = new LightColors(Map.of());
    public static final LightColors DEFAULT_TOOL_COLORS = EMPTY.setColor(Channel.ENERGY, LTXIConstants.LIME_GREEN);
    public static final LightColors DEFAULT_WEAPON_COLORS = DEFAULT_TOOL_COLORS.setColor(Channel.PRIMARY, LTXIConstants.LIME_GREEN);

    private static LightColors decode(Map<Channel, Integer> map)
    {
        return map.isEmpty() ? EMPTY : new LightColors(new EnumMap<>(map));
    }

    private LightColors(Map<Channel, Integer> map)
    {
        super(map);
    }

    @Override
    public Map<Channel, Integer> getMap()
    {
        return map.isEmpty() ? new EnumMap<>(Channel.class) : new EnumMap<>(map);
    }

    public @Nullable Integer getColor(Channel channel)
    {
        return map.get(channel);
    }

    public LightColors clearColor(Channel channel)
    {
        if (map.containsKey(channel))
        {
            var newMap = getMap();
            newMap.remove(channel);
            return new LightColors(newMap);
        }
        else
        {
            return this;
        }
    }

    public LightColors setColor(Channel channel, int color)
    {
        color = ARGB.opaque(color);

        Integer current = getColor(channel);

        if (current == null || color != current)
        {
            var newMap = getMap();
            newMap.put(channel, color);
            return new LightColors(newMap);
        }
        else
        {
            return this;
        }
    }

    public LightColors setColor(Channel channel, LimaColor color)
    {
        return setColor(channel, color.argb32());
    }

    public enum Channel implements StringRepresentable
    {
        PRIMARY("primary"),
        SECONDARY("secondary"),
        ENERGY("energy");

        public static final Codec<Channel> CODEC = LimaEnumCodec.create(Channel.class);
        public static final StreamCodec<FriendlyByteBuf, Channel> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(Channel.class);

        private final String name;

        Channel(String name)
        {
            this.name = name;
        }

        @Override
        public String getSerializedName()
        {
            return name;
        }
    }
}