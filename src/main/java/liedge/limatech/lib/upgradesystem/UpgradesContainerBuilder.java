package liedge.limatech.lib.upgradesystem;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.Holder;

import java.util.function.Function;

public final class UpgradesContainerBuilder<U extends UpgradeBase<?, ?, U>, T extends UpgradesContainerBase<?, ?, U>>
{
    private final Object2IntMap<Holder<U>> map;
    private final Function<Object2IntMap<Holder<U>>, T> containerFactory;

    private UpgradesContainerBuilder(Object2IntMap<Holder<U>> map, Function<Object2IntMap<Holder<U>>, T> containerFactory)
    {
        this.map = map;
        this.containerFactory = containerFactory;
    }

    public UpgradesContainerBuilder(Function<Object2IntMap<Holder<U>>, T> containerFactory)
    {
        this(new Object2IntOpenHashMap<>(), containerFactory);
    }

    public UpgradesContainerBuilder(T container, Function<Object2IntMap<Holder<U>>, T> containerFactory)
    {
        this(new Object2IntOpenHashMap<>(container.internalMap), containerFactory);
    }

    public UpgradesContainerBuilder<U, T> add(Holder<U> upgradeHolder, int upgradeRank)
    {
        map.put(upgradeHolder, upgradeRank);
        return this;
    }

    public UpgradesContainerBuilder<U, T> add(Holder<U> upgradeHolder)
    {
        return add(upgradeHolder, 1);
    }

    public UpgradesContainerBuilder<U, T> add(UpgradeBaseEntry<U> entry)
    {
        return add(entry.upgrade(), entry.upgradeRank());
    }

    public UpgradesContainerBuilder<U, T> remove(Holder<U> upgradeHolder)
    {
        map.removeInt(upgradeHolder);
        return this;
    }

    public T build()
    {
        return containerFactory.apply(map);
    }
}