package liedge.limatech.util.datagen;

import it.unimi.dsi.fastutil.floats.FloatList;
import liedge.limacore.data.generation.loot.LimaBlockLootSubProvider;
import liedge.limacore.data.generation.loot.LimaLootSubProvider;
import liedge.limacore.data.generation.loot.LimaLootTableProvider;
import liedge.limacore.util.LimaLootUtil;
import liedge.limacore.util.LimaMathUtil;
import liedge.limacore.world.loot.*;
import liedge.limatech.LimaTech;
import liedge.limatech.registry.LimaTechEnchantments;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.FillPlayerHead;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;

import java.util.concurrent.CompletableFuture;

import static liedge.limacore.util.LimaLootUtil.defaultEntityLootTable;
import static liedge.limacore.util.LimaLootUtil.needsEntityType;
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
            Holder<Enchantment> ammoScavengerEnchantment = registries.holderOrThrow(LimaTechEnchantments.AMMO_SCAVENGER);

            // GLM generic drops
            LootPool.Builder phantomDrops = LootPool.lootPool()
                    .when(defaultEntityLootTable(EntityType.PHANTOM))
                    .when(LootItemRandomChanceCondition.randomChance(0.1f))
                    .add(LootItem.lootTableItem(TARGETING_TECH_SALVAGE));

            addTable(ENEMY_AMMO_DROPS, LootTable.lootTable().withPool(LootPool.lootPool()
                    .when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().subPredicate(HostileEntitySubPredicate.INSTANCE)))
                    .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(registries, 0.1f, 0.02f))
                    .add(LootItem.lootTableItem(AUTO_AMMO_CANISTER).setWeight(80))
                    .add(DynamicWeightLootEntry.dynamicWeightItem(SPECIALIST_AMMO_CANISTER).setWeight(EntityEnchantmentLevelProvider.playerEnchantLevelOrElse(ammoScavengerEnchantment, LevelBasedValue.perLevel(15f, 6f), 15)))
                    .add(DynamicWeightLootEntry.dynamicWeightItem(EXPLOSIVES_AMMO_CANISTER).setWeight(EntityEnchantmentLevelProvider.playerEnchantLevelOrElse(ammoScavengerEnchantment, LevelBasedValue.perLevel(5f, 3f), 5)))
                    .add(DynamicWeightLootEntry.dynamicWeightItem(MAGNUM_AMMO_CANISTER).setWeight(EntityEnchantmentLevelProvider.playerEnchantLevelOrElse(ammoScavengerEnchantment, LevelBasedValue.perLevel(2f, 2f), 2)))
                    .setRolls(RoundingNumberProvider.roundValue(LimaMathUtil.RoundingStrategy.RANDOM, EntityEnchantmentLevelProvider.playerEnchantLevelOrElse(
                            ammoScavengerEnchantment, LevelBasedValue.lookup(FloatList.of(1, 1, 1, 1.5f, 2), LevelBasedValue.constant(2)), 1)))));

            addTable(ENTITY_EXTRA_DROPS, LootTable.lootTable().withPool(phantomDrops));

            // Razor drops
            Holder<Enchantment> razorEnchant = registries.holderOrThrow(LimaTechEnchantments.RAZOR);
            LootPool.Builder razorPool = LootPool.lootPool().when(LimaLootUtil.randomChanceLinearEnchantBonus(razorEnchant, 0f, 0.1f))
                    .add(lootItem(Items.ZOMBIE_HEAD).when(needsEntityType(EntityType.ZOMBIE)))
                    .add(lootItem(Items.SKELETON_SKULL).when(needsEntityType(EntityType.SKELETON)))
                    .add(lootItem(Items.CREEPER_HEAD).when(needsEntityType(EntityType.CREEPER)))
                    .add(lootItem(Items.PIGLIN_HEAD).when(needsEntityType(EntityType.PIGLIN)))
                    .add(lootItem(Items.WITHER_SKELETON_SKULL).when(needsEntityType(EntityType.WITHER_SKELETON)))
                    .add(lootItem(Items.PLAYER_HEAD).when(needsEntityType(EntityType.PLAYER)).apply(FillPlayerHead.fillPlayerHead(LootContext.EntityTarget.THIS)))
                    .add(lootItem(Items.DRAGON_HEAD).when(needsEntityType(EntityType.ENDER_DRAGON)).when(EntityEnchantmentLevelsCondition.playerRequiresAtLeast(razorEnchant, 5)));
            addTable(RAZOR_LOOT_TABLE, singlePoolTable(razorPool));
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

            dropSelfWithEntity(ENERGY_STORAGE_ARRAY);
            dropSelfWithEntity(INFINITE_ENERGY_STORAGE_ARRAY);
            dropSelfWithEntity(DIGITAL_FURNACE);
            dropSelfWithEntity(GRINDER);
            dropSelfWithEntity(RECOMPOSER);
            dropSelfWithEntity(MATERIAL_FUSING_CHAMBER);
            dropSelfWithEntity(FABRICATOR);
            dropSelfWithEntity(EQUIPMENT_UPGRADE_STATION);

            add(ROCKET_TURRET, singlePoolTable(applyExplosionCondition(ROCKET_TURRET, singleItemPool(ROCKET_TURRET))
                    .when(matchStateProperty(ROCKET_TURRET, DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER))
                    .apply(SaveBlockEntityFunction.saveBlockEntityData())));
        }
    }
}