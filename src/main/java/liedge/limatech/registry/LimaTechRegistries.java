package liedge.limatech.registry;

import liedge.limatech.LimaTech;
import liedge.limatech.upgradesystem.EquipmentUpgrade;
import liedge.limatech.upgradesystem.effect.UpgradeEffectType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public final class LimaTechRegistries
{
    private LimaTechRegistries() {}

    // Custom registries
    public static final ResourceKey<Registry<UpgradeEffectType<?>>> UPGRADE_EFFECT_TYPE_KEY = LimaTech.RESOURCES.registryResourceKey("upgrade_type");
    public static final Registry<UpgradeEffectType<?>> UPGRADE_EFFECT_TYPE = LimaTech.RESOURCES.registryBuilder(UPGRADE_EFFECT_TYPE_KEY).sync(true).create();

    // Data pack registries
    public static final ResourceKey<Registry<EquipmentUpgrade>> EQUIPMENT_UPGRADES_KEY = LimaTech.RESOURCES.registryResourceKey("equipment_upgrade");
}