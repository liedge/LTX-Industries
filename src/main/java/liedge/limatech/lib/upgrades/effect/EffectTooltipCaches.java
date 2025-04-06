package liedge.limatech.lib.upgrades.effect;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public final class EffectTooltipCaches implements ResourceManagerReloadListener
{
    private static final EffectTooltipCaches INSTANCE = new EffectTooltipCaches();

    public static EffectTooltipCaches getInstance()
    {
        return INSTANCE;
    }

    private final Set<LoadingCache<?, ?>> caches = ConcurrentHashMap.newKeySet();

    private EffectTooltipCaches() {}

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager)
    {
        caches.forEach(Cache::invalidateAll);
    }

    public <K> LoadingCache<K, Component> create(int maxSize, Function<? super K, ? extends Component> mapper)
    {
        Objects.requireNonNull(mapper, "Tooltip mapper cannot be null.");
        LoadingCache<K, Component> cache = CacheBuilder.newBuilder()
                .maximumSize(maxSize)
                .build(new CacheLoader<>()
                {
                    @Override
                    public Component load(K key)
                    {
                        return mapper.apply(key);
                    }
                });
        caches.add(cache);
        return cache;
    }
}