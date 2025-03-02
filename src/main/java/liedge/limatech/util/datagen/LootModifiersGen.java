package liedge.limatech.util.datagen;

import liedge.limacore.data.generation.loot.LimaLootModifierProvider;
import liedge.limacore.world.loot.AddItemLootModifier;
import liedge.limacore.world.loot.EntityEnchantmentLevelsCondition;
import liedge.limatech.lib.weapons.GrenadeType;
import liedge.limatech.registry.LimaTechItems;
import liedge.limatech.world.GrenadeSubPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;

import java.util.concurrent.CompletableFuture;

import static liedge.limacore.util.LimaLootUtil.defaultEntityLootTable;
import static liedge.limacore.util.LimaLootUtil.specificLootTable;
import static liedge.limacore.world.loot.LootModifierBuilder.rollLootTable;
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
        add("enemy_ammo_drops", rollLootTable(ENEMY_AMMO_DROPS).killedByPlayer());
        add("extra_drops", rollLootTable(ENTITY_EXTRA_DROPS).killedByPlayer());
        add("razor_drops", rollLootTable(RAZOR_LOOT_TABLE).killedByPlayer());

        add("explosives_salvage_loot", AddItemLootModifier.addItem(LimaTechItems.EXPLOSIVES_WEAPON_TECH_SALVAGE)
                .requires(specificLootTable(BuiltInLootTables.NETHER_BRIDGE))
                .requires(LootItemRandomChanceCondition.randomChance(0.5f)));

        add("warden_echo_shard_drop", AddItemLootModifier.addItem(Items.ECHO_SHARD)
                .killedByPlayer()
                .requires(defaultEntityLootTable(EntityType.WARDEN))
                .requires(AnyOfCondition.anyOf(
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER, EntityPredicate.Builder.entity().subPredicate(new GrenadeSubPredicate(GrenadeType.ACID))),
                        EntityEnchantmentLevelsCondition.playerRequiresAtLeast(registries.holderOrThrow(Enchantments.LOOTING), 4))));
    }
}