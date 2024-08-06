package liedge.limatech.upgradesystem.effect;

import com.mojang.serialization.Codec;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.lib.weapons.WeaponAttribute;
import liedge.limatech.lib.weapons.WeaponDamageSource;
import liedge.limatech.registry.LimaTechRegistries;
import liedge.limatech.upgradesystem.ItemEquipmentUpgrades;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

public interface EquipmentUpgradeEffect
{
    Codec<EquipmentUpgradeEffect> CODEC = LimaTechRegistries.UPGRADE_EFFECT_TYPE.byNameCodec().dispatch(EquipmentUpgradeEffect::getType, UpgradeEffectType::codec);

    UpgradeEffectType<?> getType();

    // General equipment
    /**
     * Called before the upgrade is installed onto an equipment item stack inside the equipment modification table.
     * @param upgrades The equipment upgrades, not yet containing the upgrade to be installed. DO NOT MODIFY
     * @param equipmentItem The equipment item stack.
     * @param upgradeRank The rank of the upgrade to be installed.
     */
    default void preUpgradeInstall(ItemEquipmentUpgrades upgrades, ItemStack equipmentItem, int upgradeRank) {}

    /**
     * Called after the upgrade is installed onto an equipment item stack inside the equipment modification table.
     * @param upgrades The equipment upgrades containing the upgrade installed. DO NOT MODIFY
     * @param equipmentItem The equipment item stack.
     * @param upgradeRank The rank of the upgrade installed.
     */
    default void postUpgradeInstall(ItemEquipmentUpgrades upgrades, ItemStack equipmentItem, int upgradeRank) {}

    /**
     * Called before the upgrade is removed from an equipment item stack inside the equipment modification table.
     * @param upgrades The equipment upgrades still containing the upgrade to be removed. DO NOT MODIFY
     * @param equipmentItem The equipment item stack.
     * @param upgradeRank The rank of the upgrade removed.
     */
    default void preUpgradeRemoved(ItemEquipmentUpgrades upgrades, ItemStack equipmentItem, int upgradeRank) {}

    /**
     * Called after the upgrade is removed from an equipment item stack inside the equipment modification table.
     * @param upgrades The equipment upgrades no longer containing the upgrade removed. DO NOT MODIFY
     * @param equipmentItem The equipment item.
     * @param upgradeRank The rank of the upgrade removed.
     */
    default void postUpgradeRemoved(ItemEquipmentUpgrades upgrades, ItemStack equipmentItem, int upgradeRank) {}

    default int addToEnchantmentLevel(Holder<Enchantment> enchantment, int upgradeRank)
    {
        return 0;
    }

    // Weapon exclusive
    default double modifyWeaponAttribute(WeaponItem weaponItem, WeaponAttribute attribute, @Nullable Entity targetEntity, int upgradeRank, double baseValue)
    {
        return 0d;
    }

    default void modifyDamageSource(WeaponDamageSource damageSource, Entity targetEntity, int upgradeRank) { }

    default boolean preventsWeaponVibrationEvent(int upgradeRank)
    {
        return false;
    }

    default void onWeaponPlayerKill(WeaponDamageSource damageSource, Player player, LivingEntity targetEntity, int upgradeRank) {}
}