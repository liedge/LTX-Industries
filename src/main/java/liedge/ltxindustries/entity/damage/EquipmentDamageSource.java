package liedge.ltxindustries.entity.damage;

import liedge.limacore.util.LimaCoreObjects;
import liedge.ltxindustries.item.UpgradableEquipmentItem;
import liedge.ltxindustries.lib.upgrades.DropsCapture;
import liedge.ltxindustries.lib.upgrades.Upgrades;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class EquipmentDamageSource extends UpgradesAwareDamageSource
{
    public static EquipmentDamageSource directDamage(ResourceKey<DamageType> typeKey, Entity attacker, ItemStack weaponItem)
    {
        return new EquipmentDamageSource(typeKey, attacker, attacker, weaponItem);
    }

    public static EquipmentDamageSource projectileDamage(ResourceKey<DamageType> typeKey, Entity projectile, @Nullable Entity attacker, ItemStack weaponItem)
    {
        return new EquipmentDamageSource(typeKey, projectile, attacker, weaponItem);
    }

    private final ItemStack weaponItem;
    private final Upgrades upgrades;

    private EquipmentDamageSource(ResourceKey<DamageType> typeKey, Entity directEntity, @Nullable Entity causingEntity, ItemStack weaponItem)
    {
        super(directEntity.level().registryAccess().holderOrThrow(typeKey), directEntity, causingEntity);
        this.weaponItem = weaponItem;
        this.upgrades = UpgradableEquipmentItem.getUpgradesFrom(weaponItem);
    }

    @Override
    public ItemStack getWeaponItem()
    {
        return weaponItem;
    }

    @Override
    public Upgrades getUpgrades()
    {
        return upgrades;
    }

    @Override
    public @Nullable DropsCapture getDropsCapture()
    {
        Player player = LimaCoreObjects.tryCast(Player.class, getEntity());
        return player != null ? DropsCapture.mobDropsToPlayer(player, upgrades) : null;
    }
}