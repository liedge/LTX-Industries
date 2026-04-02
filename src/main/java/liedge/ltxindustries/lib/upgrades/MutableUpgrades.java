package liedge.ltxindustries.lib.upgrades;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;

public final class MutableUpgrades
{
    public static MutableUpgrades create()
    {
        return new MutableUpgrades(Upgrades.EMPTY);
    }

    private final Object2IntMap<Holder<Upgrade>> map = new Object2IntLinkedOpenHashMap<>();

    MutableUpgrades(Upgrades source)
    {
        map.putAll(source.getMapForCloning());
    }

    public MutableUpgrades set(Holder<Upgrade> upgrade, int upgradeRank)
    {
        map.put(upgrade, clampRank(upgradeRank));
        return this;
    }

    public MutableUpgrades set(Holder<Upgrade> upgrade)
    {
        return set(upgrade, 1);
    }

    public MutableUpgrades set(UpgradeEntry entry)
    {
        return set(entry.upgrade(), entry.rank());
    }

    public int merge(Holder<Upgrade> upgrade, int upgradeRank)
    {
        upgradeRank = clampRank(upgradeRank);
        int previousRank = map.getOrDefault(upgrade, 0);
        if (upgradeRank > previousRank)
        {
            map.put(upgrade, upgradeRank);
            return previousRank;
        }

        return -1;
    }

    public int merge(UpgradeEntry entry)
    {
        return merge(entry.upgrade(), entry.rank());
    }

    public MutableUpgrades remove(Holder<Upgrade> holder)
    {
        map.removeInt(holder);
        return this;
    }

    public Upgrades build()
    {
        return map.isEmpty() ? Upgrades.EMPTY : new Upgrades(map);
    }

    private int clampRank(int upgradeRank)
    {
        return Math.clamp(upgradeRank, 0, Upgrade.MAX_RANK);
    }
}