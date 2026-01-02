package liedge.ltxindustries.lib.upgrades;

import liedge.ltxindustries.LTXIndustries;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public final class UpgradeContextKeys
{
    private UpgradeContextKeys() {}

    private static <T>LootContextParam<T> key(String name)
    {
        return new LootContextParam<>(LTXIndustries.RESOURCES.location(name));
    }

    public static final LootContextParam<Integer> UPGRADE_RANK = key("upgrade_rank"); // Unused for now
    public static final LootContextParam<Float> DAMAGE = key("damage");
}