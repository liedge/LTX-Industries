package liedge.limatech.lib.upgrades.effect;

import com.google.common.collect.Iterators;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public abstract class UpgradeEffectMap implements Iterable<UpgradeEffectMap.TypedEntry<?>>
{
    public static final UpgradeEffectMap EMPTY = new UpgradeEffectMap()
    {
        @Override
        public <T> @Nullable T get(UpgradeEffectDataType<? extends T> type)
        {
            return null;
        }

        @Override
        public int size()
        {
            return 0;
        }

        @Override
        protected Reference2ObjectMap<UpgradeEffectDataType<?>, Object> toMap()
        {
            return Reference2ObjectMaps.emptyMap();
        }

        @NotNull
        @Override
        public Iterator<TypedEntry<?>> iterator()
        {
            return Collections.emptyIterator();
        }
    };

    private static final Codec<Map<UpgradeEffectDataType<?>, Object>> BASE_MAP_CODEC = Codec.dispatchedMap(UpgradeEffectDataType.CODEC, UpgradeEffectDataType::codec);
    public static final Codec<UpgradeEffectMap> CODEC = BASE_MAP_CODEC.xmap(
            map -> map.isEmpty() ? EMPTY : new MapBacked(new Reference2ObjectOpenHashMap<>(map)),
            UpgradeEffectMap::toMap);

    public static Builder builder()
    {
        return new Builder();
    }

    private UpgradeEffectMap() { }

    @Nullable
    public abstract <T> T get(UpgradeEffectDataType<? extends T> type);

    public abstract int size();

    protected abstract Reference2ObjectMap<UpgradeEffectDataType<?>, Object> toMap();

    public boolean has(UpgradeEffectDataType<?> type)
    {
        return get(type) != null;
    }

    public boolean isEmpty()
    {
        return size() == 0;
    }

    public <T> List<T> getListEffect(UpgradeEffectDataType<? extends List<T>> type)
    {
        return getOrDefault(type, List.of());
    }

    public <T> List<T> getListEffect(Supplier<? extends UpgradeEffectDataType<List<T>>> typeSupplier)
    {
        return getListEffect(typeSupplier.get());
    }

    public <T> T getOrDefault(UpgradeEffectDataType<? extends T> type, T defaultValue)
    {
        T val = get(type);
        return val != null ? val : defaultValue;
    }

    private static class MapBacked extends UpgradeEffectMap
    {
        private final Reference2ObjectMap<UpgradeEffectDataType<?>, Object> map;

        private MapBacked(Reference2ObjectMap<UpgradeEffectDataType<?>, Object> map)
        {
            this.map = map;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> @Nullable T get(UpgradeEffectDataType<? extends T> type)
        {
            return (T) map.get(type);
        }

        @Override
        public int size()
        {
            return map.size();
        }

        @Override
        protected Reference2ObjectMap<UpgradeEffectDataType<?>, Object> toMap()
        {
            return map;
        }

        @NotNull
        @Override
        public Iterator<TypedEntry<?>> iterator()
        {
            return Iterators.transform(Reference2ObjectMaps.fastIterator(map), mapEntry -> TypedEntry.unchecked(mapEntry.getKey(), mapEntry.getValue()));
        }
    }

    public static class Builder
    {
        private final Reference2ObjectMap<UpgradeEffectDataType<?>, Object> map = new Reference2ObjectOpenHashMap<>();

        private Builder() {}

        public <T> Builder add(UpgradeEffectDataType<T> type, T effect)
        {
            setUnchecked(type, effect);
            return this;
        }

        public <T> Builder remove(UpgradeEffectDataType<T> type)
        {
            setUnchecked(type, null);
            return this;
        }

        public UpgradeEffectMap build()
        {
            return new MapBacked(map);
        }

        private <T> void setUnchecked(UpgradeEffectDataType<T> type, @Nullable Object value)
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

    public record TypedEntry<T>(UpgradeEffectDataType<T> type, T effect)
    {
        @SuppressWarnings("unchecked")
        private static <T> TypedEntry<T> unchecked(UpgradeEffectDataType<T> type, Object value)
        {
            return new TypedEntry<>(type, (T) value);
        }

        public void appendEffectTooltip(int upgradeRank, List<Component> lines)
        {
            type.appendTooltipLines(effect, upgradeRank, lines);
        }
    }
}