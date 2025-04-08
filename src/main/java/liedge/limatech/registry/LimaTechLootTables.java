package liedge.limatech.registry;

import liedge.limatech.LimaTech;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public final class LimaTechLootTables
{
    private LimaTechLootTables() {}

    private static ResourceKey<LootTable> key(String path)
    {
        return LimaTech.RESOURCES.resourceKey(Registries.LOOT_TABLE, path);
    }

    // Entity extra drops
    public static final ResourceKey<LootTable> ENEMY_AMMO_DROPS = key("entity/enemy_ammo_drops");
    public static final ResourceKey<LootTable> ENTITY_EXTRA_DROPS = key("entity/extra_drops");
    public static final ResourceKey<LootTable> RAZOR_LOOT_TABLE = key("entity/razor");
}