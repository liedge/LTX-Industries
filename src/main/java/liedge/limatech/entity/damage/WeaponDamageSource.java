package liedge.limatech.entity.damage;

import liedge.limacore.util.LimaCoreUtil;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.lib.upgrades.UpgradesContainerBase;
import liedge.limatech.lib.upgrades.equipment.EquipmentUpgrades;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.PlayerMainInvWrapper;
import org.jetbrains.annotations.Nullable;

public class WeaponDamageSource extends UpgradeAwareDamageSource
{
    public static WeaponDamageSource handheldInstantDamage(ResourceKey<DamageType> typeKey, Entity shooter, WeaponItem weaponItem, EquipmentUpgrades upgrades)
    {
        return new WeaponDamageSource(typeKey, shooter, shooter, weaponItem, upgrades);
    }

    public static WeaponDamageSource projectileDamage(ResourceKey<DamageType> typeKey, Entity projectile, @Nullable Entity shooter, WeaponItem weaponItem, EquipmentUpgrades upgrades)
    {
        return new WeaponDamageSource(typeKey, projectile, shooter, weaponItem, upgrades);
    }

    private final WeaponItem weaponItem;
    private final EquipmentUpgrades upgrades;

    private WeaponDamageSource(ResourceKey<DamageType> typeKey, Entity directEntity, @Nullable Entity causingEntity, WeaponItem weaponItem, EquipmentUpgrades upgrades)
    {
        super(directEntity.level().registryAccess().holderOrThrow(typeKey), directEntity, causingEntity, null);
        this.weaponItem = weaponItem;
        this.upgrades = upgrades;
    }

    public WeaponItem weaponItem()
    {
        return weaponItem;
    }

    @Override
    protected @Nullable IItemHandler getOrCreateTeleportInventory()
    {
        Player player = LimaCoreUtil.castOrNull(Player.class, getEntity());
        if (player != null)
        {
            return new PlayerMainInvWrapper(player.getInventory());
        }

        return null;
    }

    @Override
    public UpgradesContainerBase<?, ?> getUpgrades()
    {
        return upgrades;
    }

    @Override
    public @Nullable Vec3 directTeleportDropsLocation()
    {
        Player player = LimaCoreUtil.castOrNull(Player.class, getEntity());
        return player != null ? player.getEyePosition() : null;
    }
}