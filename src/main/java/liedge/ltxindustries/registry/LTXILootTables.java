package liedge.ltxindustries.registry;

import liedge.ltxindustries.LTXIndustries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public final class LTXILootTables
{
    private LTXILootTables() {}

    private static ResourceKey<LootTable> key(String path)
    {
        return LTXIndustries.RESOURCES.resourceKey(Registries.LOOT_TABLE, path);
    }

    // Entity extra drops
    public static final ResourceKey<LootTable> ENEMY_AMMO_DROPS = key("entity/enemy_ammo_drops");
    public static final ResourceKey<LootTable> ENTITY_EXTRA_DROPS = key("entity/extra_drops");
    public static final ResourceKey<LootTable> RAZOR_LOOT_TABLE = key("entity/razor");
}