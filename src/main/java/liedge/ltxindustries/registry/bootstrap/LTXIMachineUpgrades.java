package liedge.ltxindustries.registry.bootstrap;

import liedge.limacore.lib.math.MathOperation;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.LTXITags;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.lib.upgrades.TooltipArgument;
import liedge.ltxindustries.lib.upgrades.UpgradeTooltip;
import liedge.ltxindustries.lib.upgrades.effect.DoubleLevelBasedValue;
import liedge.ltxindustries.lib.upgrades.effect.TooltipValueFormat;
import liedge.ltxindustries.lib.upgrades.effect.ValueSentiment;
import liedge.ltxindustries.lib.upgrades.effect.ValueUpgradeEffect;
import liedge.ltxindustries.lib.upgrades.effect.equipment.DirectDropsUpgradeEffect;
import liedge.ltxindustries.lib.upgrades.effect.equipment.EnchantmentUpgradeEffect;
import liedge.ltxindustries.lib.upgrades.machine.MachineUpgrade;
import liedge.ltxindustries.registry.LTXIRegistries;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
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

import static liedge.ltxindustries.LTXITags.MachineUpgrades.MACHINE_TIER;
import static liedge.ltxindustries.LTXIndustries.RESOURCES;
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

        DoubleLevelBasedValue esaScaling = DoubleLevelBasedValue.exponential(2, DoubleLevelBasedValue.linear(3, 1));
        MachineUpgrade.builder(ECA_CAPACITY_UPGRADE)
                .supports(LTXIBlockEntities.ENERGY_CELL_ARRAY)
                .withEffect(LTXIUpgradeEffectComponents.ENERGY_CAPACITY, ValueUpgradeEffect.of(esaScaling, MathOperation.MULTIPLY))
                .withEffect(LTXIUpgradeEffectComponents.ENERGY_TRANSFER_RATE, ValueUpgradeEffect.of(esaScaling, MathOperation.MULTIPLY))
                .tooltip(UpgradeTooltip.of(LTXILangKeys.ENERGY_CAPACITY_UPGRADE, TooltipArgument.of(esaScaling, ValueSentiment.POSITIVE, TooltipValueFormat.MULTIPLICATIVE)))
                .tooltip(UpgradeTooltip.of(LTXILangKeys.ENERGY_TRANSFER_UPGRADE, TooltipArgument.of(esaScaling, ValueSentiment.POSITIVE, TooltipValueFormat.MULTIPLICATIVE)))
                .setMaxRank(4)
                .effectIcon(sprite("extra_energy"))
                .register(context);

        DoubleLevelBasedValue smsEnergyStorage = DoubleLevelBasedValue.linear(0.5d);
        MachineUpgrade.builder(STANDARD_MACHINE_SYSTEMS)
                .supports(blockEntities, LTXITags.BlockEntities.GENERAL_PROCESSING_MACHINES)
                .exclusiveWith(holders, MACHINE_TIER)
                .withEffect(ENERGY_CAPACITY, ValueUpgradeEffect.of(smsEnergyStorage, MathOperation.ADD_PERCENT))
                .withEffect(ENERGY_TRANSFER_RATE, ValueUpgradeEffect.of(smsEnergyStorage, MathOperation.ADD_PERCENT))
                .withEffect(TICKS_PER_OPERATION, ValueUpgradeEffect.of(DoubleLevelBasedValue.linearExponent(0.725d), MathOperation.MULTIPLY))
                .withEffect(ENERGY_USAGE, ValueUpgradeEffect.of(DoubleLevelBasedValue.linearExponent(1.5d), MathOperation.MULTIPLY))
                .withSpecialEffect(MINIMUM_MACHINE_SPEED, 5)
                .tooltip(UpgradeTooltip.of(LTXILangKeys.ENERGY_CAPACITY_UPGRADE, TooltipArgument.of(smsEnergyStorage, ValueSentiment.POSITIVE, TooltipValueFormat.SIGNED_PERCENTAGE)))
                .tooltip(UpgradeTooltip.of(LTXILangKeys.ENERGY_TRANSFER_UPGRADE, TooltipArgument.of(smsEnergyStorage, ValueSentiment.POSITIVE, TooltipValueFormat.SIGNED_PERCENTAGE)))
                .tooltip(UpgradeTooltip.of(LTXILangKeys.MACHINE_SPEED_UPGRADE, TooltipArgument.of(DoubleLevelBasedValue.linearExponent(0.725d), ValueSentiment.POSITIVE, TooltipValueFormat.MULTIPLICATIVE)))
                .tooltip(UpgradeTooltip.of(LTXILangKeys.ENERGY_USAGE_UPGRADE, TooltipArgument.of(DoubleLevelBasedValue.linearExponent(1.5d), ValueSentiment.NEUTRAL, TooltipValueFormat.MULTIPLICATIVE)))
                .tooltip(UpgradeTooltip.of(LTXILangKeys.ENERGY_PER_RECIPE_UPGRADE, TooltipArgument.of(DoubleLevelBasedValue.linearExponent(1.0875d), ValueSentiment.NEGATIVE, TooltipValueFormat.MULTIPLICATIVE)))
                .setMaxRank(6)
                .effectIcon(sprite("standard_gear"))
                .category("gpm")
                .register(context);

        MachineUpgrade.builder(ULTIMATE_MACHINE_SYSTEMS)
                .createDefaultTitle(LTXIConstants.LIME_GREEN)
                .supports(blockEntities, LTXITags.BlockEntities.GENERAL_PROCESSING_MACHINES)
                .exclusiveWith(holders, MACHINE_TIER)
                .withEffect(ENERGY_CAPACITY, ValueUpgradeEffect.of(DoubleLevelBasedValue.constant(8), MathOperation.MULTIPLY))
                .withEffect(ENERGY_TRANSFER_RATE, ValueUpgradeEffect.of(DoubleLevelBasedValue.constant(16), MathOperation.MULTIPLY))
                .withEffect(ENERGY_USAGE, ValueUpgradeEffect.of(DoubleLevelBasedValue.constant(512), MathOperation.MULTIPLY))
                .withEffect(TICKS_PER_OPERATION, ValueUpgradeEffect.of(DoubleLevelBasedValue.constant(0), MathOperation.MULTIPLY))
                .withSpecialEffect(MINIMUM_MACHINE_SPEED, 0)
                .tooltip(UpgradeTooltip.of(LTXILangKeys.ENERGY_CAPACITY_UPGRADE, TooltipArgument.of(DoubleLevelBasedValue.constant(8), ValueSentiment.POSITIVE, TooltipValueFormat.MULTIPLICATIVE)))
                .tooltip(UpgradeTooltip.of(LTXILangKeys.ENERGY_TRANSFER_UPGRADE, TooltipArgument.of(DoubleLevelBasedValue.constant(16), ValueSentiment.POSITIVE, TooltipValueFormat.MULTIPLICATIVE)))
                .tooltip(UpgradeTooltip.of(LTXILangKeys.ENERGY_USAGE_UPGRADE, TooltipArgument.of(DoubleLevelBasedValue.constant(256), ValueSentiment.NEGATIVE, TooltipValueFormat.MULTIPLICATIVE)))
                .tooltip(LTXILangKeys.INSTANT_PROCESSING_UPGRADE.translate().withStyle(LTXIConstants.LIME_GREEN.chatStyle()))
                .effectIcon(sprite("ultimate_gear"))
                .category("gpm")
                .register(context);

        MachineUpgrade.builder(FABRICATOR_UPGRADE)
                .supports(LTXIBlockEntities.FABRICATOR, LTXIBlockEntities.AUTO_FABRICATOR)
                .withEffect(ENERGY_CAPACITY, ValueUpgradeEffect.of(DoubleLevelBasedValue.linear(2), MathOperation.ADD_PERCENT))
                .withEffect(ENERGY_TRANSFER_RATE, ValueUpgradeEffect.of(DoubleLevelBasedValue.linear(3), MathOperation.ADD_PERCENT))
                .withEffect(ENERGY_USAGE, ValueUpgradeEffect.of(DoubleLevelBasedValue.exponential(2, DoubleLevelBasedValue.linear(2, 1)), MathOperation.MULTIPLY))
                .tooltip(UpgradeTooltip.of(LTXILangKeys.ENERGY_CAPACITY_UPGRADE, TooltipArgument.of(DoubleLevelBasedValue.linear(2), ValueSentiment.POSITIVE, TooltipValueFormat.SIGNED_PERCENTAGE)))
                .tooltip(UpgradeTooltip.of(LTXILangKeys.ENERGY_TRANSFER_UPGRADE, TooltipArgument.of(DoubleLevelBasedValue.linear(3), ValueSentiment.POSITIVE, TooltipValueFormat.SIGNED_PERCENTAGE)))
                .tooltip(UpgradeTooltip.of(LTXILangKeys.ENERGY_USAGE_UPGRADE, TooltipArgument.of(DoubleLevelBasedValue.exponential(2, DoubleLevelBasedValue.linear(2, 1)), ValueSentiment.POSITIVE, TooltipValueFormat.MULTIPLICATIVE)))
                .setMaxRank(4)
                .effectIcon(sprite("fabricator_upgrade"))
                .register(context);

        MachineUpgrade.builder(TURRET_LOOTING)
                .supports(blockEntities, LTXITags.BlockEntities.TURRETS)
                .withEffect(ENCHANTMENT_LEVEL, EnchantmentUpgradeEffect.oneLevelPerRank(enchantments.getOrThrow(Enchantments.LOOTING)))
                .setMaxRank(3)
                .effectIcon(sprite("clover"))
                .category("turret")
                .register(context);

        MachineUpgrade.builder(TURRET_RAZOR)
                .supports(blockEntities, LTXITags.BlockEntities.TURRETS)
                .setMaxRank(2)
                .withEffect(ENCHANTMENT_LEVEL, EnchantmentUpgradeEffect.oneLevelPerRank(enchantments.getOrThrow(LTXIEnchantments.RAZOR)))
                .effectIcon(sprite("razor_enchant"))
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