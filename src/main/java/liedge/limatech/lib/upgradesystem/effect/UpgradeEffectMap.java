package liedge.limatech.lib.upgradesystem.effect;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class UpgradeEffectMap
{
    public static final UpgradeEffectMap EMPTY = new UpgradeEffectMap()
    {
        @Override
        public <T> @Nullable T get(UpgradeEffectType<? extends T> type)
        {
            return null;
        }

        @Override
        public int size()
        {
            return 0;
        }

        @Override
        protected Reference2ObjectMap<UpgradeEffectType<?>, Object> toMap()
        {
            return Reference2ObjectMaps.emptyMap();
        }
    };

    private static final Codec<Map<UpgradeEffectType<?>, Object>> BASE_MAP_CODEC = Codec.dispatchedMap(UpgradeEffectType.CODEC, UpgradeEffectType::codec);
    public static final Codec<UpgradeEffectMap> CODEC = BASE_MAP_CODEC.xmap(
            map -> map.isEmpty() ? EMPTY : new MapBacked(new Reference2ObjectOpenHashMap<>(map)),
            UpgradeEffectMap::toMap);

    public static Builder builder()
    {
        return new Builder();
    }

    private UpgradeEffectMap() { }

    @Nullable
    public abstract <T> T get(UpgradeEffectType<? extends T> type);

    public abstract int size();

    protected abstract Reference2ObjectMap<UpgradeEffectType<?>, Object> toMap();

    public boolean isEmpty()
    {
        return size() == 0;
    }

    public <T> T getOrDefault(UpgradeEffectType<? extends T> type, T defaultValue)
    {
        T val = get(type);
        return val != null ? val : defaultValue;
    }

    private static class MapBacked extends UpgradeEffectMap
    {
        private final Reference2ObjectMap<UpgradeEffectType<?>, Object> map;

        private MapBacked(Reference2ObjectMap<UpgradeEffectType<?>, Object> map)
        {
            this.map = map;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> @Nullable T get(UpgradeEffectType<? extends T> type)
        {
            return (T) map.get(type);
        }

        @Override
        public int size()
        {
            return map.size();
        }

        @Override
        protected Reference2ObjectMap<UpgradeEffectType<?>, Object> toMap()
        {
            return map;
        }
    }

    public static class Builder
    {
        private final Reference2ObjectMap<UpgradeEffectType<?>, Object> map = new Reference2ObjectOpenHashMap<>();

        private Builder() {}

        public <T> Builder add(UpgradeEffectType<T> type, T effect)
        {
            setUnchecked(type, effect);
            return this;
        }

        public <T> Builder remove(UpgradeEffectType<T> type)
        {
            setUnchecked(type, null);
            return this;
        }

        public UpgradeEffectMap build()
        {
            return new MapBacked(map);
        }

        private <T> void setUnchecked(UpgradeEffectType<T> type, @Nullable Object value)
        {
            if (value != null)
            {
                map.put(type, value);
            }
            else
            {
                map.remove(type);
            }
        }
    }
}