package liedge.limatech.util.datagen;

import liedge.limacore.data.generation.loot.LimaBlockLootSubProvider;
import liedge.limacore.data.generation.loot.LimaLootSubProvider;
import liedge.limacore.data.generation.loot.LimaLootTableProvider;
import liedge.limacore.world.loot.HostileEntitySubPredicate;
import liedge.limacore.world.loot.SaveBlockEntityFunction;
import liedge.limatech.LimaTech;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.concurrent.CompletableFuture;

import static liedge.limatech.registry.LimaTechBlocks.*;
import static liedge.limatech.registry.LimaTechItems.*;
import static liedge.limatech.registry.LimaTechLootTables.*;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.DOUBLE_BLOCK_HALF;

class LootTablesGen extends LimaLootTableProvider
{
    LootTablesGen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries)
    {
        super(packOutput, registries);
    }

    @Override
    protected void createSubProviders()
    {
        addSubProvider(EntityBonusDrops::new, LootContextParamSets.ENTITY);
        addSubProvider(ChestDrops::new, LootContextParamSets.CHEST);
        addSubProvider(BlockDrops::new, LootContextParamSets.BLOCK);

    }

    private static class EntityBonusDrops extends LimaLootSubProvider
    {
        EntityBonusDrops(HolderLookup.Provider registries)
        {
            super(registries);
        }

        @Override
        protected void generateTables(HolderLookup.Provider registries)
        {
            // GLM generic drops
            LootPool.Builder ammoDropsPool = LootPool.lootPool()
                    .when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().subPredicate(HostileEntitySubPredicate.INSTANCE)))
                    .when(LootItemRandomChanceCondition.randomChance(0.2f))
                    .setRolls(UniformGenerator.between(1, 2))
                    .add(LootItem.lootTableItem(AUTO_AMMO_CANISTER).setWeight(80))
                    .add(LootItem.lootTableItem(SPECIALIST_AMMO_CANISTER).setWeight(15))
                    .add(LootItem.lootTableItem(EXPLOSIVES_AMMO_CANISTER).setWeight(5));

            LootPool.Builder phantomDrops = LootPool.lootPool()
                    .when(LootTableIdCondition.builder(EntityType.PHANTOM.getDefaultLootTable().location()))
                    .when(LootItemRandomChanceCondition.randomChance(0.1f))
                    .add(LootItem.lootTableItem(TARGETING_TECH_SALVAGE));

            addTable(ENTITY_EXTRA_DROPS, LootTable.lootTable().withPool(ammoDropsPool).withPool(phantomDrops));
        }
    }

    private static class ChestDrops extends LimaLootSubProvider
    {
        ChestDrops(HolderLookup.Provider registries)
        {
            super(registries);
        }

        @Override
        protected void generateTables(HolderLookup.Provider registries)
        {
            addTable(LEGENDARY_AMMO, singleItemTable(LootItem.lootTableItem(LEGENDARY_AMMO_CANISTER).when(LootItemRandomChanceCondition.randomChance(0.5f))));
            addTable(EXPLOSIVES_SALVAGE, singleItemTable(LootItem.lootTableItem(EXPLOSIVES_WEAPON_TECH_SALVAGE).when(LootItemRandomChanceCondition.randomChance(0.5f))));
        }
    }

    private static class BlockDrops extends LimaBlockLootSubProvider
    {
        BlockDrops(HolderLookup.Provider provider)
        {
            super(provider, LimaTech.MODID);
        }

        @Override
        protected void generate()
        {
            oreDrop(TITANIUM_ORE, RAW_TITANIUM);
            oreDrop(DEEPSLATE_TITANIUM_ORE, RAW_TITANIUM);
            oreDrop(NIOBIUM_ORE, RAW_NIOBIUM);

            dropSelf(RAW_TITANIUM_BLOCK,
                    RAW_NIOBIUM_BLOCK,
                    TITANIUM_BLOCK,
                    NIOBIUM_BLOCK,
                    SLATE_ALLOY_BLOCK);

            dropSelf(GLOW_BLOCKS.values());

            dropSelfWithEntity(GRINDER);
            dropSelfWithEntity(MATERIAL_FUSING_CHAMBER);
            dropSelfWithEntity(FABRICATOR);

            add(ROCKET_TURRET, singlePoolTable(applyExplosionCondition(ROCKET_TURRET, singleItemPool(ROCKET_TURRET))
                    .when(matchStateProperty(ROCKET_TURRET, DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER))
                    .apply(SaveBlockEntityFunction.saveBlockEntityData())));
        }
    }
}