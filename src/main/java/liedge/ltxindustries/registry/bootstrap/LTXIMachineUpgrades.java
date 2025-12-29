package liedge.ltxindustries.registry.bootstrap;

import liedge.limacore.lib.math.MathOperation;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.LTXITags;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.lib.upgrades.effect.AddEnchantmentLevels;
import liedge.ltxindustries.lib.upgrades.effect.CaptureMobDrops;
import liedge.ltxindustries.lib.upgrades.effect.MinimumMachineSpeed;
import liedge.ltxindustries.lib.upgrades.effect.ValueOperation;
import liedge.ltxindustries.lib.upgrades.machine.MachineUpgrade;
import liedge.ltxindustries.lib.upgrades.tooltip.TranslatableTooltip;
import liedge.ltxindustries.lib.upgrades.tooltip.ValueComponent;
import liedge.ltxindustries.lib.upgrades.tooltip.ValueFormat;
import liedge.ltxindustries.lib.upgrades.tooltip.ValueSentiment;
import liedge.ltxindustries.lib.upgrades.value.ConstantDouble;
import liedge.ltxindustries.lib.upgrades.value.ExponentialDouble;
import liedge.ltxindustries.lib.upgrades.value.LinearDouble;
import liedge.ltxindustries.lib.upgrades.value.UpgradeDoubleValue;
import liedge.ltxindustries.registry.LTXIRegistries;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.registry.game.LTXIBlocks;
import liedge.ltxindustries.registry.game.LTXIItems;
import liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static liedge.ltxindustries.LTXIConstants.REM_BLUE;
import static liedge.ltxindustries.LTXITags.MachineUpgrades.MACHINE_TIER;
import static liedge.ltxindustries.LTXITags.MachineUpgrades.PARALLEL_OPS_UPGRADES;
import static liedge.ltxindustries.LTXIndustries.RESOURCES;
import static liedge.ltxindustries.data.generation.LTXIBootstrapUtil.*;
import static liedge.ltxindustries.lib.upgrades.UpgradeIcon.itemIcon;
import static liedge.ltxindustries.lib.upgrades.UpgradeIcon.sprite;
import static liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents.*;

public final class LTXIMachineUpgrades
{
    private LTXIMachineUpgrades() {}

    // Built-in upgrade resource keys
    public static final ResourceKey<MachineUpgrade> ECA_CAPACITY_UPGRADE = key("eca_capacity");
    public static final ResourceKey<MachineUpgrade> STANDARD_MACHINE_SYSTEMS = key("standard_machine_systems");
    public static final ResourceKey<MachineUpgrade> ULTIMATE_MACHINE_SYSTEMS = key("ultimate_machine_systems");
    public static final ResourceKey<MachineUpgrade> GPM_PARALLEL = key("gpm_parallel");
    public static final ResourceKey<MachineUpgrade> FABRICATOR_UPGRADE = key("fabricator_upgrade");

    // Machine unique
    public static final ResourceKey<MachineUpgrade> GEO_SYNTHESIZER_PARALLEL = key("geo_synthesizer_parallel");

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

        UpgradeDoubleValue ecaScaling = ExponentialDouble.of(2, LinearDouble.oneIncrement(3));
        MachineUpgrade.builder(ECA_CAPACITY_UPGRADE)
                .createDefaultTitle(REM_BLUE)
                .supports(LTXIBlockEntities.ENERGY_CELL_ARRAY)
                .withEffect(LTXIUpgradeEffectComponents.ENERGY_CAPACITY, ValueOperation.of(ecaScaling, MathOperation.MULTIPLY))
                .withEffect(LTXIUpgradeEffectComponents.ENERGY_TRANSFER_RATE, ValueOperation.of(ecaScaling, MathOperation.MULTIPLY))
                .tooltip(energyCapacityTooltip(ecaScaling, ValueFormat.MULTIPLICATIVE, ValueSentiment.POSITIVE))
                .tooltip(energyTransferTooltip(ecaScaling, ValueFormat.MULTIPLICATIVE, ValueSentiment.POSITIVE))
                .setMaxRank(5)
                .effectIcon(sprite("extra_energy"))
                .register(context);

        UpgradeDoubleValue smsEnergyStorage = LinearDouble.linearIncrement(0.5d);
        MachineUpgrade.builder(STANDARD_MACHINE_SYSTEMS)
                .supports(blockEntities, LTXITags.BlockEntities.STANDARD_UPGRADABLE_MACHINES)
                .exclusiveWith(holders, MACHINE_TIER)
                .withEffect(ENERGY_CAPACITY, ValueOperation.of(smsEnergyStorage, MathOperation.ADD_PERCENT_OF_BASE))
                .withEffect(ENERGY_TRANSFER_RATE, ValueOperation.of(smsEnergyStorage, MathOperation.ADD_PERCENT_OF_BASE))
                .withEffect(TICKS_PER_OPERATION, ValueOperation.of(ExponentialDouble.linearExponent(0.725d), MathOperation.MULTIPLY))
                .withEffect(ENERGY_USAGE, ValueOperation.of(ExponentialDouble.linearExponent(1.5d), MathOperation.MULTIPLY))
                .withSpecialEffect(MINIMUM_MACHINE_SPEED, MinimumMachineSpeed.atLeast(5))
                .tooltip(energyCapacityTooltip(smsEnergyStorage, ValueFormat.SIGNED_PERCENTAGE, ValueSentiment.POSITIVE))
                .tooltip(energyTransferTooltip(smsEnergyStorage, ValueFormat.SIGNED_PERCENTAGE, ValueSentiment.POSITIVE))
                .tooltip(energyUsageTooltip(ExponentialDouble.linearExponent(1.5d), ValueFormat.MULTIPLICATIVE, ValueSentiment.NEUTRAL))
                .tooltip(TranslatableTooltip.create(LTXILangKeys.MACHINE_SPEED_UPGRADE, ValueComponent.of(ExponentialDouble.negativeLinearExponent(0.725d), ValueFormat.MULTIPLICATIVE, ValueSentiment.POSITIVE)))
                .tooltip(TranslatableTooltip.create(LTXILangKeys.ENERGY_PER_RECIPE_UPGRADE, ValueComponent.of(ExponentialDouble.linearExponent(1.0875d), ValueFormat.MULTIPLICATIVE, ValueSentiment.NEGATIVE)))
                .setMaxRank(6)
                .effectIcon(greenArrowOverlay(sprite("titanium_gear")))
                .category("gpm")
                .register(context);

        UpgradeDoubleValue umsEnergyStorage = ConstantDouble.of(16);
        UpgradeDoubleValue umsEnergyUsage = ConstantDouble.of(512);
        MachineUpgrade.builder(ULTIMATE_MACHINE_SYSTEMS)
                .createDefaultTitle(LTXIConstants.LIME_GREEN)
                .supports(blockEntities, LTXITags.BlockEntities.ULTIMATE_UPGRADABLE_MACHINES)
                .exclusiveWith(holders, MACHINE_TIER)
                .withEffect(ENERGY_CAPACITY, ValueOperation.of(umsEnergyStorage, MathOperation.MULTIPLY))
                .withEffect(ENERGY_TRANSFER_RATE, ValueOperation.of(umsEnergyStorage, MathOperation.MULTIPLY))
                .withEffect(ENERGY_USAGE, ValueOperation.of(umsEnergyUsage, MathOperation.MULTIPLY))
                .withEffect(TICKS_PER_OPERATION, ValueOperation.of(ConstantDouble.of(0), MathOperation.MULTIPLY))
                .withSpecialEffect(MINIMUM_MACHINE_SPEED, MinimumMachineSpeed.atLeast(0))
                .tooltip(energyCapacityTooltip(umsEnergyStorage, ValueFormat.MULTIPLICATIVE, ValueSentiment.POSITIVE))
                .tooltip(energyTransferTooltip(umsEnergyStorage, ValueFormat.MULTIPLICATIVE, ValueSentiment.POSITIVE))
                .tooltip(energyUsageTooltip(umsEnergyUsage, ValueFormat.MULTIPLICATIVE, ValueSentiment.NEGATIVE))
                .staticTooltip(LTXILangKeys.INSTANT_PROCESSING_UPGRADE.translate().withStyle(LTXIConstants.LIME_GREEN.chatStyle()))
                .effectIcon(sprite("ultimate_gear"))
                .category("gpm")
                .register(context);

        UpgradeDoubleValue gpmParallelOps = LinearDouble.linearIncrement(2);
        MachineUpgrade.builder(GPM_PARALLEL)
                .createDefaultTitle(o -> o.withStyle(ChatFormatting.LIGHT_PURPLE))
                .supports(blockEntities, LTXITags.BlockEntities.GENERAL_PROCESSING_MACHINES)
                .exclusiveWith(holders, PARALLEL_OPS_UPGRADES)
                .withEffect(PARALLEL_OPERATIONS, ValueOperation.of(gpmParallelOps, MathOperation.REPLACE))
                .tooltip(parallelOpsTooltip(gpmParallelOps, ValueFormat.FLAT_NUMBER, ValueSentiment.POSITIVE))
                .setMaxRank(4)
                .effectIcon(plusOverlay(sprite("titanium_gear")))
                .category("gpm")
                .register(context);

        UpgradeDoubleValue fabCapacity = LinearDouble.linearIncrement(2);
        UpgradeDoubleValue fabTransfer = LinearDouble.linearIncrement(3);
        UpgradeDoubleValue fabUsage = ExponentialDouble.of(2, LinearDouble.oneIncrement(2));
        MachineUpgrade.builder(FABRICATOR_UPGRADE)
                .supports(LTXIBlockEntities.FABRICATOR, LTXIBlockEntities.AUTO_FABRICATOR)
                .withEffect(ENERGY_CAPACITY, ValueOperation.of(fabCapacity, MathOperation.ADD_PERCENT_OF_BASE))
                .withEffect(ENERGY_TRANSFER_RATE, ValueOperation.of(fabTransfer, MathOperation.ADD_PERCENT_OF_BASE))
                .withEffect(ENERGY_USAGE, ValueOperation.of(fabUsage, MathOperation.MULTIPLY))
                .tooltip(energyCapacityTooltip(fabCapacity, ValueFormat.SIGNED_PERCENTAGE, ValueSentiment.POSITIVE))
                .tooltip(energyTransferTooltip(fabTransfer, ValueFormat.SIGNED_PERCENTAGE, ValueSentiment.POSITIVE))
                .tooltip(energyUsageTooltip(fabUsage, ValueFormat.MULTIPLICATIVE, ValueSentiment.POSITIVE))
                .setMaxRank(4)
                .effectIcon(sprite("fabricator_upgrade"))
                .register(context);

        UpgradeDoubleValue geoSynthParallel = ExponentialDouble.of(2, LinearDouble.oneIncrement(4));
        MachineUpgrade.builder(GEO_SYNTHESIZER_PARALLEL)
                .createDefaultTitle(o -> o.withStyle(ChatFormatting.AQUA))
                .supports(LTXIBlockEntities.GEO_SYNTHESIZER)
                .exclusiveWith(holders, PARALLEL_OPS_UPGRADES)
                .withEffect(PARALLEL_OPERATIONS, ValueOperation.of(geoSynthParallel, MathOperation.REPLACE))
                .tooltip(parallelOpsTooltip(geoSynthParallel, ValueFormat.FLAT_NUMBER, ValueSentiment.POSITIVE))
                .setMaxRank(3)
                .effectIcon(plusOverlay(itemIcon(LTXIBlocks.GEO_SYNTHESIZER)))
                .category("machine_unique")
                .register(context);

        MachineUpgrade.builder(TURRET_LOOTING)
                .supports(blockEntities, LTXITags.BlockEntities.TURRETS)
                .withEffect(ENCHANTMENT_LEVELS, AddEnchantmentLevels.rankLinear(enchantments.getOrThrow(Enchantments.LOOTING)))
                .setMaxRank(3)
                .effectIcon(luckOverlay(LTXIItems.LTX_SWORD))
                .category("turret")
                .register(context);

        MachineUpgrade.builder(TURRET_RAZOR)
                .supports(blockEntities, LTXITags.BlockEntities.TURRETS)
                .setMaxRank(2)
                .withEffect(ENCHANTMENT_LEVELS, AddEnchantmentLevels.rankLinear(enchantments.getOrThrow(LTXIEnchantments.RAZOR)))
                .effectIcon(sprite("razor"))
                .category("turret")
                .register(context);

        MachineUpgrade.builder(TURRET_LOOT_COLLECTOR)
                .supports(blockEntities, LTXITags.BlockEntities.TURRETS)
                .withSpecialEffect(CAPTURE_MOB_DROPS, CaptureMobDrops.INSTANCE)
                .effectIcon(sprite("magnet"))
                .category("turret")
                .register(context);
    }
}