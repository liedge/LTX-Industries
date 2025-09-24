package liedge.ltxindustries.entity.damage;

import liedge.limacore.util.LimaCoreUtil;
import liedge.ltxindustries.item.UpgradableEquipmentItem;
import liedge.ltxindustries.lib.upgrades.effect.equipment.DirectDropsUpgradeEffect;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrades;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class UpgradableEquipmentDamageSource extends UpgradableDamageSource
{
    public static UpgradableEquipmentDamageSource directDamage(ResourceKey<DamageType> typeKey, Entity attacker, ItemStack weaponItem)
    {
        return new UpgradableEquipmentDamageSource(typeKey, attacker, attacker, weaponItem);
    }

    public static UpgradableEquipmentDamageSource projectileDamage(ResourceKey<DamageType> typeKey, Entity projectile, @Nullable Entity attacker, ItemStack weaponItem)
    {
        return new UpgradableEquipmentDamageSource(typeKey, projectile, attacker, weaponItem);
    }

    private final ItemStack weaponItem;
    private final EquipmentUpgrades upgrades;

    private UpgradableEquipmentDamageSource(ResourceKey<DamageType> typeKey, Entity directEntity, @Nullable Entity causingEntity, ItemStack weaponItem)
    {
        super(directEntity.level().registryAccess().holderOrThrow(typeKey), directEntity, causingEntity);
        this.weaponItem = weaponItem;
        this.upgrades = UpgradableEquipmentItem.getEquipmentUpgradesFromStack(weaponItem);
    }

    @Override
    public @NotNull ItemStack getWeaponItem()
    {
        return weaponItem;
    }

    @Override
    public EquipmentUpgrades getUpgrades()
    {
        return upgrades;
    }

    @Override
    public @Nullable DropsRedirect createDropsRedirect()
    {
        Player player = LimaCoreUtil.castOrNull(Player.class, getEntity());
        return player != null ? DropsRedirect.forPlayer(player, upgrades, DirectDropsUpgradeEffect.Type.ENTITY_DROPS) : null;
    }
}