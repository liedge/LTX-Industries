package liedge.ltxindustries.entity.damage;

import liedge.limacore.util.LimaCoreUtil;
import liedge.ltxindustries.item.UpgradableEquipmentItem;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrades;
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
    private final EquipmentUpgrades upgrades;

    private EquipmentDamageSource(ResourceKey<DamageType> typeKey, Entity directEntity, @Nullable Entity causingEntity, ItemStack weaponItem)
    {
        super(directEntity.level().registryAccess().holderOrThrow(typeKey), directEntity, causingEntity);
        this.weaponItem = weaponItem;
        this.upgrades = UpgradableEquipmentItem.getEquipmentUpgradesFromStack(weaponItem);
    }

    @Override
    public ItemStack getWeaponItem()
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
        return player != null ? DropsRedirect.forMobDrops(player, upgrades) : null;
    }
}