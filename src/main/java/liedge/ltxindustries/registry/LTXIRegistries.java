package liedge.ltxindustries.registry;

import liedge.ltxindustries.lib.upgrades.effect.entity.EntityUpgradeEffectType;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrade;
import liedge.ltxindustries.lib.upgrades.machine.MachineUpgrade;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceKey;

import static liedge.ltxindustries.LTXIndustries.RESOURCES;

public final class LTXIRegistries
{
    private LTXIRegistries() {}

    public static final Registry<DataComponentType<?>> UPGRADE_COMPONENT_TYPES = RESOURCES.registryBuilder(Keys.UPGRADE_COMPONENT_TYPES).sync(true).create();
    public static final Registry<EntityUpgradeEffectType<?>> ENTITY_UPGRADE_EFFECT_TYPES = RESOURCES.registryBuilder(Keys.ENTITY_UPGRADE_EFFECT_TYPES).sync(true).create();

    public static final class Keys
    {
        private Keys() {}

        // Game registries
        public static final ResourceKey<Registry<DataComponentType<?>>> UPGRADE_COMPONENT_TYPES = RESOURCES.registryResourceKey("upgrade_component");
        public static final ResourceKey<Registry<EntityUpgradeEffectType<?>>> ENTITY_UPGRADE_EFFECT_TYPES = RESOURCES.registryResourceKey("entity_upgrade_effect");

        // Data registries
        public static final ResourceKey<Registry<EquipmentUpgrade>> EQUIPMENT_UPGRADES = RESOURCES.registryResourceKey("equipment_upgrade");
        public static final ResourceKey<Registry<MachineUpgrade>> MACHINE_UPGRADES = RESOURCES.registryResourceKey("machine_upgrade");
    }
}