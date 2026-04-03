package liedge.ltxindustries.registry.bootstrap;

import liedge.limacore.lib.MinMaxRange;
import liedge.limacore.lib.MobHostility;
import liedge.limacore.world.loot.condition.EntityHostilityCondition;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.LTXITags;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.lib.upgrades.Upgrade;
import liedge.ltxindustries.registry.LTXIRegistries;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.predicates.AllOfCondition;

import static liedge.ltxindustries.LTXITags.Upgrades.TARGET_PREDICATES;
import static liedge.ltxindustries.lib.upgrades.UpgradeIcon.sprite;

public final class LTXIUpgrades
{
    private LTXIUpgrades() {}

    public static final ResourceKey<Upgrade> ALL_ENTITIES_TARGETING = key("targeting/all");
    public static final ResourceKey<Upgrade> NEUTRAL_ENEMY_TARGETING = key("targeting/neutral_enemy");
    public static final ResourceKey<Upgrade> HOSTILE_TARGETING = key("targeting/hostile_only");

    public static void bootstrap(BootstrapContext<Upgrade> context)
    {
        HolderGetter<Upgrade> holders = context.lookup(LTXIRegistries.Keys.UPGRADES);
        HolderGetter<Item> items = context.lookup(Registries.ITEM);
        HolderGetter<BlockEntityType<?>> blockEntities = context.lookup(Registries.BLOCK_ENTITY_TYPE);

        // Common holder sets
        HolderSet<Item> allWeapons = items.getOrThrow(LTXITags.Items.WEAPON_EQUIPMENT);
        HolderSet<BlockEntityType<?>> turrets = blockEntities.getOrThrow(LTXITags.BlockEntities.TURRETS);

        Upgrade.builder(ALL_ENTITIES_TARGETING)
                .createDefaultTitle(LTXIConstants.HOSTILE_ORANGE)
                .forEquipment(allWeapons)
                .forMachines(turrets)
                .exclusiveWith(holders, TARGET_PREDICATES)
                .targetRestriction(AllOfCondition.allOf())
                .effectIcon(sprite("all_targets"))
                .category("target_predicates")
                .register(context);
        Upgrade.builder(NEUTRAL_ENEMY_TARGETING)
                .createDefaultTitle(LTXIConstants.HOSTILE_ORANGE)
                .forEquipment(allWeapons)
                .forMachines(turrets)
                .exclusiveWith(holders, TARGET_PREDICATES)
                .targetRestriction(EntityHostilityCondition.attackerIs(MinMaxRange.atLeast(MobHostility.NEUTRAL_ENEMY)))
                .effectIcon(sprite("neutral_enemy_targets"))
                .category("target_predicates")
                .register(context);
        Upgrade.builder(HOSTILE_TARGETING)
                .createDefaultTitle(LTXIConstants.HOSTILE_ORANGE)
                .forEquipment(allWeapons)
                .forMachines(turrets)
                .exclusiveWith(holders, TARGET_PREDICATES)
                .targetRestriction(EntityHostilityCondition.attackerIs(MinMaxRange.atLeast(MobHostility.HOSTILE)))
                .effectIcon(sprite("hostile_targets"))
                .category("target_predicates")
                .register(context);
    }

    private static ResourceKey<Upgrade> key(String name)
    {
        return LTXIndustries.RESOURCES.resourceKey(LTXIRegistries.Keys.UPGRADES, name);
    }
}