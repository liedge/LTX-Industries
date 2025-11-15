package liedge.ltxindustries.registry.bootstrap;

import liedge.limacore.lib.math.MathOperation;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.LTXITags;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.lib.upgrades.effect.DirectDropsUpgradeEffect;
import liedge.ltxindustries.lib.upgrades.effect.EnchantmentLevelsUpgradeEffect;
import liedge.ltxindustries.lib.upgrades.effect.MinimumSpeedUpgradeEffect;
import liedge.ltxindustries.lib.upgrades.effect.value.*;
import liedge.ltxindustries.lib.upgrades.machine.MachineUpgrade;
import liedge.ltxindustries.lib.upgrades.tooltip.UpgradeTooltip;
import liedge.ltxindustries.lib.upgrades.tooltip.ValueArgument;
import liedge.ltxindustries.lib.upgrades.tooltip.ValueFormat;
import liedge.ltxindustries.lib.upgrades.tooltip.ValueSentiment;
import liedge.ltxindustries.registry.LTXIRegistries;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.registry.game.LTXIItems;
import liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents;
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

import static liedge.ltxindustries.LTXIConstants.REM_BLUE;
import static liedge.ltxindustries.LTXITags.MachineUpgrades.MACHINE_TIER;
import static liedge.ltxindustries.LTXIndustries.RESOURCES;
import static liedge.ltxindustries.data.generation.LTXIBootstrapUtil.*;
import static liedge.ltxindustries.lib.upgrades.UpgradeIcon.sprite;
import static liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents.*;

public final class LTXIMachineUpgrades
{
    private LTXIMachineUpgrades() {}

    // Built-in upgrade resource keys
    public static final ResourceKey<MachineUpgrade> ECA_CAPACITY_UPGRADE = key("eca_capacity");
    public static final ResourceKey<MachineUpgrade> STANDARD_MACHINE_SYSTEMS = key("standard_machine_systems");
    public static final ResourceKey<MachineUpgrade> ULTIMATE_MACHINE_SYSTEMS = key("ultimate_machine_systems");
    public static final ResourceKey<MachineUpgrade> FABRICATOR_UPGRADE = key("fabricator_upgrade");

    public static final ResourceKey<MachineUpgrade> TURRET_LOOTING = key("turret_looting");
    public static final ResourceKey<MachineUpgrade> TURRET_RAZOR = key("turret_razor");
    public static final ResourceKey<MachineUpgrade> TURRET_LOOT_COLLECTOR = key("turret_loot_collector");

    private static ResourceKey<MachineUpgrade> key(String name)
    {
        return RESOURCES.resourceKey(LTXIRegistries.Keys.MACHINE_UPGRADES, name);
    }

    public static void bootstrap(BootstrapContext<MachineUpgrade> context)
    {
        HolderGetter<MachineUpgrade> holders = context.lookup(LTXIRegistries.Keys.MACHINE_UPGRADES);
        HolderGetter<Enchantment> enchantments = context.lookup(Registries.ENCHANTMENT);
        HolderGetter<BlockEntityType<?>> blockEntities = context.lookup(Registries.BLOCK_ENTITY_TYPE);

        // AnyHolderSets
        HolderSet<Item> anyItemHolderSet = new AnyHolderSet<>(BuiltInRegistries.ITEM.asLookup());

        UpgradeDoubleValue ecaScaling = ExponentialDouble.of(2, LinearDouble.of(3, 1));
        MachineUpgrade.builder(ECA_CAPACITY_UPGRADE)
                .createDefaultTitle(REM_BLUE)
                .supports(LTXIBlockEntities.ENERGY_CELL_ARRAY)
                .withEffect(LTXIUpgradeEffectComponents.ENERGY_CAPACITY, SimpleValueEffect.of(ecaScaling, MathOperation.MULTIPLY))
                .withEffect(LTXIUpgradeEffectComponents.ENERGY_TRANSFER_RATE, SimpleValueEffect.of(ecaScaling, MathOperation.MULTIPLY))
                .tooltip(energyCapacityTooltip(ecaScaling, ValueFormat.MULTIPLICATIVE, ValueSentiment.POSITIVE))
                .tooltip(energyTransferTooltip(ecaScaling, ValueFormat.MULTIPLICATIVE, ValueSentiment.POSITIVE))
                .setMaxRank(5)
                .effectIcon(sprite("extra_energy"))
                .register(context);

        UpgradeDoubleValue smsEnergyStorage = LinearDouble.of(0.5d);
        MachineUpgrade.builder(STANDARD_MACHINE_SYSTEMS)
                .supports(blockEntities, LTXITags.BlockEntities.STANDARD_UPGRADABLE_MACHINES)
                .exclusiveWith(holders, MACHINE_TIER)
                .withEffect(ENERGY_CAPACITY, SimpleValueEffect.of(smsEnergyStorage, MathOperation.ADD_PERCENT_OF_BASE))
                .withEffect(ENERGY_TRANSFER_RATE, SimpleValueEffect.of(smsEnergyStorage, MathOperation.ADD_PERCENT_OF_BASE))
                .withEffect(TICKS_PER_OPERATION, SimpleValueEffect.of(ExponentialDouble.linearExponent(0.725d), MathOperation.MULTIPLY))
                .withEffect(ENERGY_USAGE, SimpleValueEffect.of(ExponentialDouble.linearExponent(1.5d), MathOperation.MULTIPLY))
                .withSpecialEffect(MINIMUM_MACHINE_SPEED, MinimumSpeedUpgradeEffect.atLeast(5))
                .tooltip(energyCapacityTooltip(smsEnergyStorage, ValueFormat.SIGNED_PERCENTAGE, ValueSentiment.POSITIVE))
                .tooltip(energyTransferTooltip(smsEnergyStorage, ValueFormat.SIGNED_PERCENTAGE, ValueSentiment.POSITIVE))
                .tooltip(energyUsageTooltip(ExponentialDouble.linearExponent(1.5d), ValueFormat.MULTIPLICATIVE, ValueSentiment.NEUTRAL))
                .tooltip(UpgradeTooltip.of(LTXILangKeys.MACHINE_SPEED_UPGRADE, ValueArgument.of(ExponentialDouble.linearExponent(0.725d), ValueFormat.MULTIPLICATIVE, ValueSentiment.POSITIVE)))
                .tooltip(UpgradeTooltip.of(LTXILangKeys.ENERGY_PER_RECIPE_UPGRADE, ValueArgument.of(ExponentialDouble.linearExponent(1.0875d), ValueFormat.MULTIPLICATIVE, ValueSentiment.NEGATIVE)))
                .setMaxRank(6)
                .effectIcon(sprite("standard_gear"))
                .category("gpm")
                .register(context);

        UpgradeDoubleValue umsEnergyStorage = ConstantDouble.of(16);
        UpgradeDoubleValue umsEnergyUsage = ConstantDouble.of(512);
        MachineUpgrade.builder(ULTIMATE_MACHINE_SYSTEMS)
                .createDefaultTitle(LTXIConstants.LIME_GREEN)
                .supports(blockEntities, LTXITags.BlockEntities.ULTIMATE_UPGRADABLE_MACHINES)
                .exclusiveWith(holders, MACHINE_TIER)
                .withEffect(ENERGY_CAPACITY, SimpleValueEffect.of(umsEnergyStorage, MathOperation.MULTIPLY))
                .withEffect(ENERGY_TRANSFER_RATE, SimpleValueEffect.of(umsEnergyStorage, MathOperation.MULTIPLY))
                .withEffect(ENERGY_USAGE, SimpleValueEffect.of(umsEnergyUsage, MathOperation.MULTIPLY))
                .withEffect(TICKS_PER_OPERATION, SimpleValueEffect.of(ConstantDouble.of(0), MathOperation.MULTIPLY))
                .withSpecialEffect(MINIMUM_MACHINE_SPEED, MinimumSpeedUpgradeEffect.atLeast(0))
                .tooltip(energyCapacityTooltip(umsEnergyStorage, ValueFormat.MULTIPLICATIVE, ValueSentiment.POSITIVE))
                .tooltip(energyTransferTooltip(umsEnergyStorage, ValueFormat.MULTIPLICATIVE, ValueSentiment.POSITIVE))
                .tooltip(energyUsageTooltip(umsEnergyUsage, ValueFormat.MULTIPLICATIVE, ValueSentiment.NEGATIVE))
                .tooltip(LTXILangKeys.INSTANT_PROCESSING_UPGRADE.translate().withStyle(LTXIConstants.LIME_GREEN.chatStyle()))
                .effectIcon(sprite("ultimate_gear"))
                .category("gpm")
                .register(context);

        UpgradeDoubleValue fabCapacity = LinearDouble.of(2);
        UpgradeDoubleValue fabTransfer = LinearDouble.of(3);
        UpgradeDoubleValue fabUsage = ExponentialDouble.of(2, LinearDouble.of(2, 1));
        MachineUpgrade.builder(FABRICATOR_UPGRADE)
                .supports(LTXIBlockEntities.FABRICATOR, LTXIBlockEntities.AUTO_FABRICATOR)
                .withEffect(ENERGY_CAPACITY, SimpleValueEffect.of(fabCapacity, MathOperation.ADD_PERCENT_OF_BASE))
                .withEffect(ENERGY_TRANSFER_RATE, SimpleValueEffect.of(fabTransfer, MathOperation.ADD_PERCENT_OF_BASE))
                .withEffect(ENERGY_USAGE, SimpleValueEffect.of(fabUsage, MathOperation.MULTIPLY))
                .tooltip(energyCapacityTooltip(fabCapacity, ValueFormat.SIGNED_PERCENTAGE, ValueSentiment.POSITIVE))
                .tooltip(energyTransferTooltip(fabTransfer, ValueFormat.SIGNED_PERCENTAGE, ValueSentiment.POSITIVE))
                .tooltip(energyUsageTooltip(fabUsage, ValueFormat.MULTIPLICATIVE, ValueSentiment.POSITIVE))
                .setMaxRank(4)
                .effectIcon(sprite("fabricator_upgrade"))
                .register(context);

        MachineUpgrade.builder(TURRET_LOOTING)
                .supports(blockEntities, LTXITags.BlockEntities.TURRETS)
                .withEffect(ENCHANTMENT_LEVELS, EnchantmentLevelsUpgradeEffect.rankLinear(enchantments.getOrThrow(Enchantments.LOOTING)))
                .setMaxRank(3)
                .effectIcon(luckOverlayIcon(LTXIItems.LTX_SWORD))
                .category("turret")
                .register(context);

        MachineUpgrade.builder(TURRET_RAZOR)
                .supports(blockEntities, LTXITags.BlockEntities.TURRETS)
                .setMaxRank(2)
                .withEffect(ENCHANTMENT_LEVELS, EnchantmentLevelsUpgradeEffect.rankLinear(enchantments.getOrThrow(LTXIEnchantments.RAZOR)))
                .effectIcon(sprite("razor"))
                .category("turret")
                .register(context);

        MachineUpgrade.builder(TURRET_LOOT_COLLECTOR)
                .supports(blockEntities, LTXITags.BlockEntities.TURRETS)
                .withEffect(DIRECT_DROPS, DirectDropsUpgradeEffect.entityDrops(anyItemHolderSet))
                .effectIcon(sprite("magnet"))
                .category("turret")
                .register(context);
    }
}