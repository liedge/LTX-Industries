package liedge.limatech.entity.damage;

import liedge.limatech.blockentity.BaseTurretBlockEntity;
import liedge.limatech.item.UpgradableEquipmentItem;
import liedge.limatech.item.weapon.WeaponItem;
import net.minecraft.core.component.DataComponentType;

import static liedge.limatech.LimaTech.RESOURCES;

public final class LimaTechDamageComponents
{
    private LimaTechDamageComponents() {}

    public static final DataComponentType<UpgradableEquipmentItem> EQUIPMENT_ITEM = RESOURCES.transientDataComponent("equipment_item");
    public static final DataComponentType<BaseTurretBlockEntity> TURRET_BLOCK_DATA = RESOURCES.transientDataComponent("turret_be");
    public static final DataComponentType<WeaponItem> KILLER_WEAPON_ITEM = RESOURCES.transientDataComponent("weapon_item");
}