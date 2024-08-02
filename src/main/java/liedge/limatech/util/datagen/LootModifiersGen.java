package liedge.limatech.util.datagen;

import liedge.limacore.data.generation.loot.LimaLootModifierProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.concurrent.CompletableFuture;

import static liedge.limatech.LimaTech.RESOURCES;
import static liedge.limatech.registry.LimaTechLootTables.*;

class LootModifiersGen extends LimaLootModifierProvider
{
    LootModifiersGen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries)
    {
        super(packOutput, registries, RESOURCES);
    }

    @Override
    protected void start()
    {
        rollTable(ENTITY_EXTRA_DROPS).killedByPlayer().build("extra_drops");

        rollTable(EXPLOSIVES_SALVAGE)
                .requires(forTable(BuiltInLootTables.NETHER_BRIDGE))
                .build("explosives_salvage_chest");

        rollTable(LEGENDARY_AMMO)
                .requires(forTable(BuiltInLootTables.TRIAL_CHAMBERS_REWARD_OMINOUS))
                .build("legendary_ammo_chest");
    }

    private LootItemCondition.Builder forTable(ResourceKey<LootTable> key)
    {
        return LootTableIdCondition.builder(key.location());
    }
}