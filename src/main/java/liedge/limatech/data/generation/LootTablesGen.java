package liedge.limatech.data.generation;

import liedge.limacore.data.generation.loot.LimaBlockLootSubProvider;
import liedge.limacore.data.generation.loot.LimaLootSubProvider;
import liedge.limacore.data.generation.loot.LimaLootTableProvider;
import liedge.limacore.lib.math.LimaRoundingMode;
import liedge.limacore.util.LimaLootUtil;
import liedge.limacore.world.loot.DynamicWeightLootEntry;
import liedge.limacore.world.loot.EnchantmentLevelSubPredicate;
import liedge.limacore.world.loot.HostileEntitySubPredicate;
import liedge.limacore.world.loot.SaveBlockEntityFunction;
import liedge.limacore.world.loot.number.EnhancedLookupLevelBasedValue;
import liedge.limacore.world.loot.number.RoundingNumberProvider;
import liedge.limacore.world.loot.number.TargetedEnchantmentLevelProvider;
import liedge.limatech.LimaTech;
import liedge.limatech.block.TurretBlock;
import liedge.limatech.lib.weapons.GrenadeType;
import liedge.limatech.registry.bootstrap.LimaTechEnchantments;
import liedge.limatech.world.GrenadeSubPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.FillPlayerHead;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static liedge.limacore.util.LimaLootUtil.*;
import static liedge.limatech.registry.LimaTechLootTables.*;
import static liedge.limatech.registry.game.LimaTechBlocks.*;
import static liedge.limatech.registry.game.LimaTechItems.*;
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
        addSubProvider(EnchantedDamageTables::new, LootContextParamSets.ENCHANTED_DAMAGE);
    }

    private static class EnchantedDamageTables extends LimaLootSubProvider
    {
        EnchantedDamageTables(HolderLookup.Provider registries)
        {
            super(registries);
        }

        @Override
        protected void generateTables(HolderLookup.Provider registries)
        {
            Holder<Enchantment> razorEnchant = registries.holderOrThrow(LimaTechEnchantments.RAZOR);

            LootPool.Builder razorDragonHead = LootPool.lootPool()
                    .when(needsEntityType(EntityType.ENDER_DRAGON))
                    .when(LimaLootUtil.randomChanceWithEnchantBonus(razorEnchant, 0f, EnhancedLookupLevelBasedValue.offsetLookup(4, 0f, 1f, 0.5f)))
                    .add(lootItem(Items.DRAGON_HEAD));

            LootPool.Builder razorGeneralHeads = LootPool.lootPool().when(LimaLootUtil.randomChanceLinearEnchantBonus(razorEnchant, 0f, 0.1f))
                    .add(lootItem(Items.ZOMBIE_HEAD).when(needsEntityTag(EntityTypeTags.ZOMBIES)))
                    .add(lootItem(Items.SKELETON_SKULL).when(needsEntityTag(EntityTypeTags.SKELETONS)).when(needsEntityType(EntityType.WITHER_SKELETON).invert()))
                    .add(lootItem(Items.CREEPER_HEAD).when(needsEntityType(EntityType.CREEPER)))
                    .add(lootItem(Items.PIGLIN_HEAD).when(needsEntityType(EntityType.PIGLIN)))
                    .add(lootItem(Items.WITHER_SKELETON_SKULL).when(needsEntityType(EntityType.WITHER_SKELETON)))
                    .add(lootItem(Items.PLAYER_HEAD).when(needsEntityType(EntityType.PLAYER)).apply(FillPlayerHead.fillPlayerHead(LootContext.EntityTarget.THIS)));

            addTable(RAZOR_LOOT_TABLE, LootTable.lootTable().withPool(razorDragonHead).withPool(razorGeneralHeads));
        }
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
            Holder<Enchantment> razorEnchantment = registries.holderOrThrow(LimaTechEnchantments.RAZOR);
            Holder<Enchantment> lootingEnchantment = registries.holderOrThrow(Enchantments.LOOTING);

            // GLM generic drops
            LootPool.Builder phantomDrops = LootPool.lootPool()
                    .when(needsEntityType(EntityType.PHANTOM))
                    .when(LootItemRandomChanceCondition.randomChance(0.1f))
                    .add(lootItem(TARGETING_TECH_SALVAGE));

            LootPool.Builder wardenDrops = LootPool.lootPool()
                    .when(needsEntityType(EntityType.WARDEN))
                    .when(AnyOfCondition.anyOf(
                            LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER, EntityPredicate.Builder.entity().subPredicate(new GrenadeSubPredicate(GrenadeType.ACID))),
                            entityEnchantmentLevels(LootContext.EntityTarget.ATTACKER, EnchantmentLevelSubPredicate.atLeast(razorEnchantment, 3))))
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                    .add(lootItem(Items.ECHO_SHARD));

            addTable(ENTITY_EXTRA_DROPS, LootTable.lootTable()
                    .withPool(phantomDrops)
                    .withPool(wardenDrops));

            LootPool.Builder ammoDrops = LootPool.lootPool()
                    .when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().subPredicate(HostileEntitySubPredicate.INSTANCE)))
                    .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(registries, 0.1f, 0.02f))
                    .add(lootItem(AUTO_AMMO_CANISTER).setWeight(80))
                    .add(DynamicWeightLootEntry.dynamicWeightItem(SPECIALIST_AMMO_CANISTER, 15).setReplaceWeight(false).setDynamicWeight(TargetedEnchantmentLevelProvider.of(LootContext.EntityTarget.ATTACKER, ammoScavengerEnchantment, LevelBasedValue.perLevel(6f))))
                    .add(DynamicWeightLootEntry.dynamicWeightItem(EXPLOSIVES_AMMO_CANISTER, 5).setReplaceWeight(false).setDynamicWeight(TargetedEnchantmentLevelProvider.of(LootContext.EntityTarget.ATTACKER, ammoScavengerEnchantment, LevelBasedValue.perLevel(3))))
                    .add(DynamicWeightLootEntry.dynamicWeightItem(ROCKET_LAUNCHER_AMMO, 3).setReplaceWeight(false).setDynamicWeight(TargetedEnchantmentLevelProvider.of(LootContext.EntityTarget.ATTACKER, ammoScavengerEnchantment, LevelBasedValue.perLevel(3))))
                    .add(DynamicWeightLootEntry.dynamicWeightItem(MAGNUM_AMMO_CANISTER, 1).setReplaceWeight(false).setDynamicWeight(TargetedEnchantmentLevelProvider.of(LootContext.EntityTarget.ATTACKER, ammoScavengerEnchantment, LevelBasedValue.perLevel(2))))
                    .setRolls(RoundingNumberProvider.of(TargetedEnchantmentLevelProvider.of(LootContext.EntityTarget.ATTACKER, ammoScavengerEnchantment, EnhancedLookupLevelBasedValue.offsetLookup(4, 1, 2, 1.5f)), LimaRoundingMode.RANDOM));

            addTable(ENEMY_AMMO_DROPS, LootTable.lootTable().withPool(ammoDrops));
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

            turretDrop(ROCKET_TURRET);
            turretDrop(RAILGUN_TURRET);
        }

        private void turretDrop(Supplier<? extends TurretBlock> turretSupplier)
        {
            TurretBlock block = turretSupplier.get();
            add(block, singlePoolTable(applyExplosionCondition(block, singleItemPool(block))
                    .when(matchStateProperty(block, DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER))
                    .apply(SaveBlockEntityFunction.saveBlockEntityData())));
        }
    }
}