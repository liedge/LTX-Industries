package liedge.ltxindustries.data.generation;

import liedge.limacore.advancement.ComparableBounds;
import liedge.limacore.data.generation.loot.LimaBlockLootSubProvider;
import liedge.limacore.data.generation.loot.LimaLootSubProvider;
import liedge.limacore.data.generation.loot.LimaLootTableProvider;
import liedge.limacore.lib.MobHostility;
import liedge.limacore.lib.math.LimaRoundingMode;
import liedge.limacore.util.LimaLootUtil;
import liedge.limacore.world.loot.DynamicWeightLootEntry;
import liedge.limacore.world.loot.condition.EntityHostilityLootCondition;
import liedge.limacore.world.loot.level.RangedLookupLevelBasedValue;
import liedge.limacore.world.loot.number.EntityEnchantmentLevelProvider;
import liedge.limacore.world.loot.number.LevelBasedNumberProvider;
import liedge.limacore.world.loot.number.RoundingNumberProvider;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.lib.weapons.GrenadeType;
import liedge.ltxindustries.registry.bootstrap.LTXIEnchantments;
import liedge.ltxindustries.registry.game.LTXIBlocks;
import liedge.ltxindustries.registry.game.LTXIMobEffects;
import liedge.ltxindustries.world.GrenadeSubPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.MobEffectsPredicate;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.FillPlayerHead;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.loot.CanItemPerformAbility;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static liedge.limacore.util.LimaLootUtil.needsEntityTag;
import static liedge.limacore.util.LimaLootUtil.needsEntityType;
import static liedge.ltxindustries.registry.LTXILootTables.*;
import static liedge.ltxindustries.registry.game.LTXIBlocks.*;
import static liedge.ltxindustries.registry.game.LTXIBlocks.SPARK_FRUIT;
import static liedge.ltxindustries.registry.game.LTXIItems.*;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.AGE_2;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.BERRIES;

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
        protected void generateTables()
        {
            Holder<Enchantment> ammoScavengerEnchantment = registries.holderOrThrow(LTXIEnchantments.AMMO_SCAVENGER);
            Holder<Enchantment> razorEnchantment = registries.holderOrThrow(LTXIEnchantments.RAZOR);

            // GLM generic drops
            LootPool.Builder phantomDrops = LootPool.lootPool()
                    .when(needsEntityType(EntityType.PHANTOM))
                    .when(LootItemRandomChanceCondition.randomChance(0.1f))
                    .add(lootItem(TARGETING_TECH_SALVAGE));

            LootItemCondition.Builder acidFinalBlow = LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER, EntityPredicate.Builder.entity().subPredicate(new GrenadeSubPredicate(GrenadeType.ACID)));
            LootItemCondition.Builder corrodingCheck = LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().effects(MobEffectsPredicate.Builder.effects().and(LTXIMobEffects.CORROSIVE)));
            LootPool.Builder wardenDrops = LootPool.lootPool()
                    .when(needsEntityType(EntityType.WARDEN))
                    .when(AnyOfCondition.anyOf(acidFinalBlow, corrodingCheck))
                    .add(lootItem(NEURO_CHEMICAL));

            addTable(ENTITY_EXTRA_DROPS, LootTable.lootTable()
                    .withPool(phantomDrops)
                    .withPool(wardenDrops));

            // Ammo drops table
            Function<LevelBasedValue, NumberProvider> ammoWeights = lbv -> LevelBasedNumberProvider.of(EntityEnchantmentLevelProvider.enchantLevel(LootContext.EntityTarget.ATTACKER, ammoScavengerEnchantment), lbv);
            LootPool.Builder ammoDrops = LootPool.lootPool()
                    .when(EntityHostilityLootCondition.create(ComparableBounds.atLeast(MobHostility.NEUTRAL_ENEMY)))
                    .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(registries, 0.1f, 0.02f))
                    .add(lootItem(LIGHTWEIGHT_WEAPON_ENERGY).setWeight(80))
                    .add(DynamicWeightLootEntry.dynamicWeightItem(SPECIALIST_WEAPON_ENERGY, 15).setReplaceWeight(false).setDynamicWeight(ammoWeights.apply(LevelBasedValue.perLevel(6))))
                    .add(DynamicWeightLootEntry.dynamicWeightItem(EXPLOSIVES_WEAPON_ENERGY, 5).setReplaceWeight(false).setDynamicWeight(ammoWeights.apply(LevelBasedValue.perLevel(3))))
                    .add(DynamicWeightLootEntry.dynamicWeightItem(HEAVY_WEAPON_ENERGY, 1).setReplaceWeight(false).setDynamicWeight(ammoWeights.apply(LevelBasedValue.perLevel(2))))
                    .setRolls(RoundingNumberProvider.of(ammoWeights.apply(RangedLookupLevelBasedValue.lookupAfterLevel(3, 1f, 1.5f, 2f)), LimaRoundingMode.RANDOM));

            addTable(ENEMY_AMMO_DROPS, LootTable.lootTable().withPool(ammoDrops));

            // Razor enchantment loot table
            LootPool.Builder razorGeneralHeads = LootPool.lootPool()
                    .when(LimaLootUtil.randomChanceLinearEnchantBonus(razorEnchantment, 0f, 0.1f))
                    .add(lootItem(Items.ZOMBIE_HEAD).when(needsEntityTag(EntityTypeTags.ZOMBIES)))
                    .add(lootItem(Items.SKELETON_SKULL).when(needsEntityTag(EntityTypeTags.SKELETONS)).when(needsEntityType(EntityType.WITHER_SKELETON).invert()))
                    .add(lootItem(Items.CREEPER_HEAD).when(needsEntityType(EntityType.CREEPER)))
                    .add(lootItem(Items.PIGLIN_HEAD).when(needsEntityType(EntityType.PIGLIN)))
                    .add(lootItem(Items.WITHER_SKELETON_SKULL).when(needsEntityType(EntityType.WITHER_SKELETON)))
                    .add(lootItem(Items.PLAYER_HEAD).when(needsEntityType(EntityType.PLAYER)).apply(FillPlayerHead.fillPlayerHead(LootContext.EntityTarget.THIS)));
            LootPool.Builder razorDragonHead = LootPool.lootPool()
                    .when(needsEntityType(EntityType.ENDER_DRAGON))
                    .when(LimaLootUtil.randomChanceWithEnchantBonus(razorEnchantment, 0f, RangedLookupLevelBasedValue.lookupAfterLevelOrBelow(4, 0f, 0.5f, 1f)))
                    .add(lootItem(Items.DRAGON_HEAD));
            LootPool.Builder razorRabbitFoot = LootPool.lootPool()
                    .when(needsEntityType(EntityType.RABBIT))
                    .when(LimaLootUtil.randomChanceLinearEnchantBonus(razorEnchantment, 0f, 0.33f))
                    .add(lootItem(Items.RABBIT_FOOT));
            addTable(RAZOR_LOOT_TABLE, LootTable.lootTable().withPool(razorGeneralHeads).withPool(razorDragonHead).withPool(razorRabbitFoot));
        }
    }

    private static class BlockDrops extends LimaBlockLootSubProvider
    {
        BlockDrops(HolderLookup.Provider provider)
        {
            super(provider, LTXIndustries.MODID);
        }

        @Override
        protected void generate()
        {
            // Enchantments
            Holder<Enchantment> fortune = registries.holderOrThrow(Enchantments.FORTUNE);

            oreDrop(TITANIUM_ORE, RAW_TITANIUM);
            oreDrop(DEEPSLATE_TITANIUM_ORE, RAW_TITANIUM);
            oreDrop(NIOBIUM_ORE, RAW_NIOBIUM);

            dropSelf(RAW_TITANIUM_BLOCK,
                    RAW_NIOBIUM_BLOCK,
                    TITANIUM_BLOCK,
                    NIOBIUM_BLOCK,
                    SLATESTEEL_BLOCK);
            oreCluster(RAW_TITANIUM_CLUSTER, RAW_TITANIUM);
            oreCluster(RAW_NIOBIUM_CLUSTER, RAW_NIOBIUM);

            dropSelf(NEON_LIGHTS.values());
            dropSelf(TITANIUM_PANEL, SMOOTH_TITANIUM_PANEL, TITANIUM_GLASS, SLATESTEEL_PANEL, SMOOTH_SLATESTEEL_PANEL);
            add(SPARK_FRUIT, block -> {
                LootItemCondition.Builder fullGrown = LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(AGE_2, 2));
                return singleItemTable(applyExplosionDecay(block, lootItem(block)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(3)).when(fullGrown))
                        .apply(ApplyBonusCount.addUniformBonusCount(fortune).when(fullGrown))));
            });
            berryVines(BILEVINE);
            berryVines(BILEVINE_PLANT);
            dropSelf(LTXIBlocks.GLOOM_SHROOM);
            dropSelfWithEntity(ENERGY_CELL_ARRAY);
            dropSelfWithEntity(INFINITE_ENERGY_CELL_ARRAY);
            dropSelfWithEntity(DIGITAL_FURNACE);
            dropSelfWithEntity(DIGITAL_SMOKER);
            dropSelfWithEntity(DIGITAL_BLAST_FURNACE);
            dropSelfWithEntity(GRINDER);
            dropSelfWithEntity(MATERIAL_FUSING_CHAMBER);
            dropSelfWithEntity(ELECTROCENTRIFUGE);
            dropSelfWithEntity(MIXER);
            dropSelfWithEntity(VOLTAIC_INJECTOR);
            dropSelfWithEntity(CHEM_LAB);
            dropSelfWithEntity(ASSEMBLER);
            dropSelfWithEntity(GEO_SYNTHESIZER);
            dropSelfWithEntity(FABRICATOR);
            dropSelfWithEntity(AUTO_FABRICATOR);
            dropSelfWithEntity(EQUIPMENT_UPGRADE_STATION);
            dropSelfWithEntity(MOLECULAR_RECONSTRUCTOR);
            dropSelfWithEntity(DIGITAL_GARDEN);

            dropSelfWithEntity(ROCKET_TURRET);
            dropSelfWithEntity(RAILGUN_TURRET);
        }

        private void oreCluster(Holder<Block> oreCluster, ItemLike rawOre)
        {
            add(oreCluster, block -> createSilkTouchDispatchTable(block, applyExplosionDecay(block, lootItem(rawOre).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 5))))));
        }

        private void berryVines(Holder<Block> holder)
        {
            add(holder, block -> singleItemTable(lootItem(VITRIOL_BERRIES).when(AnyOfCondition.anyOf(
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(BERRIES, true)),
                    CanItemPerformAbility.canItemPerformAbility(ItemAbilities.SHEARS_DIG)))));
        }
    }
}