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
    public static final ResourceKey<LootTable> ENTITY_EXTRA_DROPS = key("entity/extra_drops");

    // Chests
    public static final ResourceKey<LootTable> EXPLOSIVES_SALVAGE = key("chests/explosive_salvage");
    public static final ResourceKey<LootTable> LEGENDARY_AMMO = key("chests/legendary_ammo");
}