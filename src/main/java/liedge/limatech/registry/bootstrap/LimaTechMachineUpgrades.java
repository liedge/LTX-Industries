package liedge.limatech.registry.bootstrap;

import liedge.limatech.LimaTechConstants;
import liedge.limatech.LimaTechTags;
import liedge.limatech.lib.CompoundValueOperation;
import liedge.limatech.lib.upgrades.effect.equipment.DirectDropsUpgradeEffect;
import liedge.limatech.lib.upgrades.effect.equipment.EnchantmentUpgradeEffect;
import liedge.limatech.lib.upgrades.effect.value.DoubleLevelBasedValue;
import liedge.limatech.lib.upgrades.effect.value.ValueUpgradeEffect;
import liedge.limatech.lib.upgrades.machine.MachineUpgrade;
import liedge.limatech.registry.LimaTechRegistries;
import liedge.limatech.registry.game.LimaTechBlockEntities;
import liedge.limatech.registry.game.LimaTechUpgradeEffectComponents;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.holdersets.AnyHolderSet;

import static liedge.limatech.LimaTech.RESOURCES;
import static liedge.limatech.LimaTechTags.MachineUpgrades.MACHINE_TIER;
import static liedge.limatech.lib.upgrades.UpgradeIcon.sprite;
import static liedge.limatech.registry.game.LimaTechUpgradeEffectComponents.*;

public final class LimaTechMachineUpgrades
{
    private LimaTechMachineUpgrades() {}

    // Built-in upgrade resource keys
    public static final ResourceKey<MachineUpgrade> ESA_CAPACITY_UPGRADE = key("esa_capacity");
    public static final ResourceKey<MachineUpgrade> STANDARD_MACHINE_SYSTEMS = key("standard_machine_systems");
    public static final ResourceKey<MachineUpgrade> ULTIMATE_MACHINE_SYSTEMS = key("ultimate_machine_systems");
    public static final ResourceKey<MachineUpgrade> FABRICATOR_UPGRADE = key("fabricator_upgrade");

    public static final ResourceKey<MachineUpgrade> TURRET_LOOTING = key("turret_looting");
    public static final ResourceKey<MachineUpgrade> TURRET_RAZOR = key("turret_razor");
    public static final ResourceKey<MachineUpgrade> TURRET_LOOT_COLLECTOR = key("turret_loot_collector");

    private static ResourceKey<MachineUpgrade> key(String name)
    {
        return RESOURCES.resourceKey(LimaTechRegistries.Keys.MACHINE_UPGRADES, name);
    }

    public static void bootstrap(BootstrapContext<MachineUpgrade> context)
    {
        HolderGetter<MachineUpgrade> holders = context.lookup(LimaTechRegistries.Keys.MACHINE_UPGRADES);
        HolderGetter<Enchantment> enchantments = context.lookup(Registries.ENCHANTMENT);
        HolderGetter<BlockEntityType<?>> blockEntities = context.lookup(Registries.BLOCK_ENTITY_TYPE);

        // AnyHolderSets
        HolderSet<Item> anyItemHolderSet = new AnyHolderSet<>(BuiltInRegistries.ITEM.asLookup());

        MachineUpgrade.builder(ESA_CAPACITY_UPGRADE)
                .supports(LimaTechBlockEntities.ENERGY_STORAGE_ARRAY)
                .withEffect(LimaTechUpgradeEffectComponents.ENERGY_CAPACITY, ValueUpgradeEffect.createSimple(DoubleLevelBasedValue.exponential(2, DoubleLevelBasedValue.linear(3, 1)), CompoundValueOperation.MULTIPLY))
                .withEffect(LimaTechUpgradeEffectComponents.ENERGY_TRANSFER_RATE, ValueUpgradeEffect.createSimple(DoubleLevelBasedValue.exponential(2, DoubleLevelBasedValue.linear(3, 1)), CompoundValueOperation.MULTIPLY))
                .setMaxRank(4)
                .effectIcon(sprite("extra_energy"))
                .register(context);

        MachineUpgrade.builder(STANDARD_MACHINE_SYSTEMS)
                .supports(blockEntities, LimaTechTags.BlockEntities.GENERAL_PROCESSING_MACHINES)
                .exclusiveWith(holders, MACHINE_TIER)
                .withEffect(ENERGY_CAPACITY, ValueUpgradeEffect.createSimple(DoubleLevelBasedValue.linear(0.5d), CompoundValueOperation.ADD_MULTIPLIED_BASE))
                .withEffect(ENERGY_TRANSFER_RATE, ValueUpgradeEffect.createSimple(DoubleLevelBasedValue.linear(0.5d), CompoundValueOperation.ADD_MULTIPLIED_BASE))
                .withEffect(ENERGY_USAGE, ValueUpgradeEffect.createSimple(DoubleLevelBasedValue.linearExponent(1.5d), CompoundValueOperation.MULTIPLY, true))
                .withEffect(TICKS_PER_OPERATION, ValueUpgradeEffect.createSimple(DoubleLevelBasedValue.exponential(0.725d, DoubleLevelBasedValue.linear(1)), CompoundValueOperation.MULTIPLY, true))
                .setMaxRank(6)
                .effectIcon(sprite("standard_gear"))
                .category("gpm")
                .register(context);

        MachineUpgrade.builder(ULTIMATE_MACHINE_SYSTEMS)
                .createDefaultTitle(LimaTechConstants.LIME_GREEN)
                .supports(blockEntities, LimaTechTags.BlockEntities.GENERAL_PROCESSING_MACHINES)
                .exclusiveWith(holders, MACHINE_TIER)
                .withEffect(ENERGY_CAPACITY, ValueUpgradeEffect.createSimple(DoubleLevelBasedValue.constant(8), CompoundValueOperation.MULTIPLY))
                .withEffect(ENERGY_TRANSFER_RATE, ValueUpgradeEffect.createSimple(DoubleLevelBasedValue.constant(16), CompoundValueOperation.MULTIPLY))
                .withEffect(ENERGY_USAGE, ValueUpgradeEffect.createSimple(DoubleLevelBasedValue.constant(256), CompoundValueOperation.MULTIPLY, true))
                .withEffect(TICKS_PER_OPERATION, ValueUpgradeEffect.createSimple(DoubleLevelBasedValue.constant(-1), CompoundValueOperation.ADD_MULTIPLIED_TOTAL, true))
                .effectIcon(sprite("ultimate_gear"))
                .category("gpm")
                .register(context);

        MachineUpgrade.builder(FABRICATOR_UPGRADE)
                .supports(LimaTechBlockEntities.FABRICATOR)
                .withEffect(ENERGY_CAPACITY, ValueUpgradeEffect.createSimple(DoubleLevelBasedValue.exponential(2, DoubleLevelBasedValue.linear(2, 1)), CompoundValueOperation.MULTIPLY))
                .withEffect(ENERGY_TRANSFER_RATE, ValueUpgradeEffect.createSimple(DoubleLevelBasedValue.exponential(2, DoubleLevelBasedValue.linear(2, 1)), CompoundValueOperation.MULTIPLY))
                .setMaxRank(4)
                .effectIcon(sprite("fabricator_upgrade"))
                .register(context);

        MachineUpgrade.builder(TURRET_LOOTING)
                .supports(blockEntities, LimaTechTags.BlockEntities.TURRETS)
                .withEffect(ENCHANTMENT_LEVEL, EnchantmentUpgradeEffect.oneLevelPerRank(enchantments.getOrThrow(Enchantments.LOOTING)))
                .setMaxRank(3)
                .effectIcon(sprite("clover"))
                .category("turret")
                .register(context);

        MachineUpgrade.builder(TURRET_RAZOR)
                .supports(blockEntities, LimaTechTags.BlockEntities.TURRETS)
                .setMaxRank(2)
                .withEffect(ENCHANTMENT_LEVEL, EnchantmentUpgradeEffect.oneLevelPerRank(enchantments.getOrThrow(LimaTechEnchantments.RAZOR)))
                .effectIcon(sprite("razor_enchant"))
                .category("turret")
                .register(context);

        MachineUpgrade.builder(TURRET_LOOT_COLLECTOR)
                .supports(blockEntities, LimaTechTags.BlockEntities.TURRETS)
                .withEffect(DIRECT_DROPS, DirectDropsUpgradeEffect.entityDrops(anyItemHolderSet))
                .effectIcon(sprite("magnet"))
                .category("turret")
                .register(context);
    }
}