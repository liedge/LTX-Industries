package liedge.limatech.registry.bootstrap;

import liedge.limatech.LimaTechConstants;
import liedge.limatech.lib.CompoundValueOperation;
import liedge.limatech.lib.upgrades.effect.EnchantmentUpgradeEffect;
import liedge.limatech.lib.upgrades.effect.value.DoubleLevelBasedValue;
import liedge.limatech.lib.upgrades.effect.value.SimpleValueUpgradeEffect;
import liedge.limatech.lib.upgrades.machine.MachineUpgrade;
import liedge.limatech.registry.LimaTechBlockEntities;
import liedge.limatech.registry.LimaTechRegistries;
import liedge.limatech.registry.LimaTechUpgradeEffectComponents;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import static liedge.limatech.LimaTech.RESOURCES;
import static liedge.limatech.data.generation.LimaTechBootstrap.sprite;
import static liedge.limatech.registry.LimaTechUpgradeEffectComponents.*;
import static liedge.limatech.registry.LimaTechUpgradeEffectComponents.DIRECT_ITEM_TELEPORT;

public final class LimaTechMachineUpgrades
{
    private LimaTechMachineUpgrades() {}

    // Built-in upgrade resource keys
    public static final ResourceKey<MachineUpgrade> ESA_CAPACITY_UPGRADE = key("esa_capacity");
    public static final ResourceKey<MachineUpgrade> ALPHA_MACHINE_SYSTEMS = key("alpha_machine_systems");
    public static final ResourceKey<MachineUpgrade> EPSILON_MACHINE_SYSTEMS = key("epsilon_machine_systems");
    public static final ResourceKey<MachineUpgrade> FABRICATOR_UPGRADE = key("fabricator_upgrade");

    public static final ResourceKey<MachineUpgrade> TURRET_LOOTING = key("turret_looting");
    public static final ResourceKey<MachineUpgrade> TURRET_LOOT_COLLECTOR = key("turret_loot_collector");

    private static ResourceKey<MachineUpgrade> key(String name)
    {
        return RESOURCES.resourceKey(LimaTechRegistries.Keys.MACHINE_UPGRADES, name);
    }

    public static void bootstrap(BootstrapContext<MachineUpgrade> context)
    {
        HolderGetter<Enchantment> enchantments = context.lookup(Registries.ENCHANTMENT);

        MachineUpgrade.builder(ESA_CAPACITY_UPGRADE)
                .supports(LimaTechBlockEntities.ENERGY_STORAGE_ARRAY)
                .withEffect(LimaTechUpgradeEffectComponents.ENERGY_CAPACITY, SimpleValueUpgradeEffect.of(DoubleLevelBasedValue.exponential(2, DoubleLevelBasedValue.linear(3, 1)), CompoundValueOperation.MULTIPLY))
                .withEffect(LimaTechUpgradeEffectComponents.ENERGY_TRANSFER_RATE, SimpleValueUpgradeEffect.of(DoubleLevelBasedValue.exponential(2, DoubleLevelBasedValue.linear(3, 1)), CompoundValueOperation.MULTIPLY))
                .setMaxRank(4)
                .effectIcon(sprite("extra_energy"))
                .buildAndRegister(context);

        MachineUpgrade.builder(ALPHA_MACHINE_SYSTEMS)
                .supports(LimaTechBlockEntities.DIGITAL_FURNACE, LimaTechBlockEntities.GRINDER, LimaTechBlockEntities.RECOMPOSER, LimaTechBlockEntities.MATERIAL_FUSING_CHAMBER)
                .withEffect(ENERGY_CAPACITY, SimpleValueUpgradeEffect.of(DoubleLevelBasedValue.linear(0.5d), CompoundValueOperation.ADD_MULTIPLIED_BASE))
                .withEffect(ENERGY_TRANSFER_RATE, SimpleValueUpgradeEffect.of(DoubleLevelBasedValue.linear(0.5d), CompoundValueOperation.ADD_MULTIPLIED_BASE))
                .withEffect(MACHINE_ENERGY_USAGE, SimpleValueUpgradeEffect.of(DoubleLevelBasedValue.linearExponent(1.5d), CompoundValueOperation.MULTIPLY))
                .withEffect(TICKS_PER_OPERATION, SimpleValueUpgradeEffect.of(DoubleLevelBasedValue.exponential(0.725d, DoubleLevelBasedValue.linear(1)), CompoundValueOperation.MULTIPLY))
                .setMaxRank(6)
                .effectIcon(sprite("alpha_systems"))
                .buildAndRegister(context);

        MachineUpgrade.builder(EPSILON_MACHINE_SYSTEMS)
                .createDefaultTitle(key -> Component.translatable(key).withStyle(LimaTechConstants.LIME_GREEN.chatStyle()))
                .supports(LimaTechBlockEntities.DIGITAL_FURNACE, LimaTechBlockEntities.GRINDER, LimaTechBlockEntities.RECOMPOSER, LimaTechBlockEntities.MATERIAL_FUSING_CHAMBER)
                .withEffect(ENERGY_CAPACITY, SimpleValueUpgradeEffect.of(DoubleLevelBasedValue.constant(8), CompoundValueOperation.MULTIPLY))
                .withEffect(ENERGY_TRANSFER_RATE, SimpleValueUpgradeEffect.of(DoubleLevelBasedValue.constant(16), CompoundValueOperation.MULTIPLY))
                .withEffect(MACHINE_ENERGY_USAGE, SimpleValueUpgradeEffect.of(DoubleLevelBasedValue.constant(256), CompoundValueOperation.MULTIPLY))
                .withEffect(TICKS_PER_OPERATION, SimpleValueUpgradeEffect.of(DoubleLevelBasedValue.constant(-1), CompoundValueOperation.ADD_MULTIPLIED_TOTAL))
                .effectIcon(sprite("epsilon_systems"))
                .buildAndRegister(context);

        MachineUpgrade.builder(FABRICATOR_UPGRADE)
                .supports(LimaTechBlockEntities.FABRICATOR)
                .withEffect(ENERGY_CAPACITY, SimpleValueUpgradeEffect.of(DoubleLevelBasedValue.exponential(2, DoubleLevelBasedValue.linear(2, 1)), CompoundValueOperation.MULTIPLY))
                .withEffect(ENERGY_TRANSFER_RATE, SimpleValueUpgradeEffect.of(DoubleLevelBasedValue.exponential(2, DoubleLevelBasedValue.linear(2, 1)), CompoundValueOperation.MULTIPLY))
                .setMaxRank(4)
                .effectIcon(sprite("fabricator_upgrade"))
                .buildAndRegister(context);

        MachineUpgrade.builder(TURRET_LOOTING)
                .supports(LimaTechBlockEntities.ROCKET_TURRET)
                .withEffect(ENCHANTMENT_LEVEL, EnchantmentUpgradeEffect.oneLevelPerRank(enchantments.getOrThrow(Enchantments.LOOTING)))
                .setMaxRank(3)
                .effectIcon(sprite("looting"))
                .buildAndRegister(context);

        MachineUpgrade.builder(TURRET_LOOT_COLLECTOR)
                .supports(LimaTechBlockEntities.ROCKET_TURRET)
                .withUnitEffect(DIRECT_ITEM_TELEPORT)
                .effectIcon(sprite("magnet"))
                .buildAndRegister(context);
    }
}