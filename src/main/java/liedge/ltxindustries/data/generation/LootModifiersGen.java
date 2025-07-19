package liedge.ltxindustries.data.generation;

import liedge.limacore.data.generation.loot.LimaLootModifierProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

import static liedge.limacore.world.loot.LootModifierBuilder.rollLootTable;
import static liedge.ltxindustries.LTXIndustries.RESOURCES;
import static liedge.ltxindustries.registry.LTXILootTables.*;

class LootModifiersGen extends LimaLootModifierProvider
{
    LootModifiersGen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries)
    {
        super(packOutput, registries, RESOURCES);
    }

    @Override
    protected void start()
    {
        add("enemy_ammo_drops", rollLootTable(ENEMY_AMMO_DROPS).killedByPlayer());
        add("extra_drops", rollLootTable(ENTITY_EXTRA_DROPS).killedByPlayer());
        add("razor_loot_table", rollLootTable(RAZOR_LOOT_TABLE).killedByPlayer());
    }
}