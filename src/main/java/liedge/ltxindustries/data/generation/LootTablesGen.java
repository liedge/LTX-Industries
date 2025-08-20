package liedge.ltxindustries.data.generation;

import liedge.limacore.data.generation.loot.LimaBlockLootSubProvider;
import liedge.limacore.data.generation.loot.LimaLootSubProvider;
import liedge.limacore.data.generation.loot.LimaLootTableProvider;
import liedge.limacore.lib.MobHostility;
import liedge.limacore.lib.math.LimaRoundingMode;
import liedge.limacore.util.LimaLootUtil;
import liedge.limacore.world.loot.DynamicWeightLootEntry;
import liedge.limacore.world.loot.EnchantmentLevelEntityPredicate;
import liedge.limacore.world.loot.EntityHostilityLootCondition;
import liedge.limacore.world.loot.number.EnhancedLookupLevelBasedValue;
import liedge.limacore.world.loot.number.RoundingNumberProvider;
import liedge.limacore.world.loot.number.TargetedEnchantmentLevelProvider;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.lib.weapons.GrenadeType;
import liedge.ltxindustries.registry.bootstrap.LTXIEnchantments;
import liedge.ltxindustries.world.GrenadeSubPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.FillPlayerHead;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

import static liedge.limacore.util.LimaLootUtil.*;
import static liedge.ltxindustries.registry.LTXILootTables.*;
import static liedge.ltxindustries.registry.game.LTXIBlocks.*;
import static liedge.ltxindustries.registry.game.LTXIItems.*;
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
        protected void generateTables(HolderLookup.Provider registries)
        {
            Holder<Enchantment> ammoScavengerEnchantment = registries.holderOrThrow(LTXIEnchantments.AMMO_SCAVENGER);
            Holder<Enchantment> razorEnchantment = registries.holderOrThrow(LTXIEnchantments.RAZOR);

            // GLM generic drops
            LootPool.Builder phantomDrops = LootPool.lootPool()
                    .when(needsEntityType(EntityType.PHANTOM))
                    .when(LootItemRandomChanceCondition.randomChance(0.1f))
                    .add(lootItem(TARGETING_TECH_SALVAGE));

            LootPool.Builder wardenDrops = LootPool.lootPool()
                    .when(needsEntityType(EntityType.WARDEN))
                    .when(AnyOfCondition.anyOf(
                            LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER, EntityPredicate.Builder.entity().subPredicate(new GrenadeSubPredicate(GrenadeType.ACID))),
                            entityEnchantmentLevels(LootContext.EntityTarget.ATTACKER, EnchantmentLevelEntityPredicate.atLeast(razorEnchantment, 3))))
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                    .add(lootItem(Items.ECHO_SHARD));

            addTable(ENTITY_EXTRA_DROPS, LootTable.lootTable()
                    .withPool(phantomDrops)
                    .withPool(wardenDrops));

            // Ammo drops table
            LootPool.Builder ammoDrops = LootPool.lootPool()
                    .when(EntityHostilityLootCondition.atLeast(MobHostility.HOSTILE))
                    .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(registries, 0.1f, 0.02f))
                    .add(lootItem(LIGHTWEIGHT_WEAPON_ENERGY).setWeight(80))
                    .add(DynamicWeightLootEntry.dynamicWeightItem(SPECIALIST_WEAPON_ENERGY, 15).setReplaceWeight(false).setDynamicWeight(TargetedEnchantmentLevelProvider.of(LootContext.EntityTarget.ATTACKER, ammoScavengerEnchantment, LevelBasedValue.perLevel(6))))
                    .add(DynamicWeightLootEntry.dynamicWeightItem(EXPLOSIVES_WEAPON_ENERGY, 5).setReplaceWeight(false).setDynamicWeight(TargetedEnchantmentLevelProvider.of(LootContext.EntityTarget.ATTACKER, ammoScavengerEnchantment, LevelBasedValue.perLevel(3))))
                    .add(DynamicWeightLootEntry.dynamicWeightItem(HEAVY_WEAPON_ENERGY, 1).setReplaceWeight(false).setDynamicWeight(TargetedEnchantmentLevelProvider.of(LootContext.EntityTarget.ATTACKER, ammoScavengerEnchantment, LevelBasedValue.perLevel(2))))
                    .setRolls(RoundingNumberProvider.of(TargetedEnchantmentLevelProvider.of(LootContext.EntityTarget.ATTACKER, ammoScavengerEnchantment, EnhancedLookupLevelBasedValue.offsetLookup(4, 1, 2, 1.5f)), LimaRoundingMode.RANDOM));

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
                    .when(LimaLootUtil.randomChanceWithEnchantBonus(razorEnchantment, 0f, EnhancedLookupLevelBasedValue.offsetLookup(4, 0f, 1f, 0.5f)))
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
            oreDrop(TITANIUM_ORE, RAW_TITANIUM);
            oreDrop(DEEPSLATE_TITANIUM_ORE, RAW_TITANIUM);
            oreDrop(NIOBIUM_ORE, RAW_NIOBIUM);

            dropSelf(RAW_TITANIUM_BLOCK,
                    RAW_NIOBIUM_BLOCK,
                    TITANIUM_BLOCK,
                    NIOBIUM_BLOCK,
                    SLATESTEEL_BLOCK);

            dropSelf(NEON_LIGHTS.values());
            dropSelf(TITANIUM_PANEL);
            dropSelf(TITANIUM_GLASS, SLATE_GLASS);
            berryVines(BILEVINE);
            berryVines(BILEVINE_PLANT);
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
            dropSelfWithEntity(FABRICATOR);
            dropSelfWithEntity(AUTO_FABRICATOR);
            dropSelfWithEntity(EQUIPMENT_UPGRADE_STATION);
            dropSelfWithEntity(MOLECULAR_RECONSTRUCTOR);

            dropSelfWithEntity(ROCKET_TURRET);
            dropSelfWithEntity(RAILGUN_TURRET);
        }

        private void berryVines(Holder<Block> holder)
        {
            LootItemCondition.Builder condition = AnyOfCondition.anyOf(
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(holder.value()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(BERRIES, true)),
                    MatchTool.toolMatches(ItemPredicate.Builder.item().of(Tags.Items.TOOLS_SHEAR)));
            add(holder, singlePoolTable(singleItemPool(VITRIOL_BERRIES).when(condition)));
        }
    }
}