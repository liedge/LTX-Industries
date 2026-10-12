package liedge.ltxindustries.entity;

import liedge.limacore.util.LimaLootUtil;
import liedge.ltxindustries.data.LTXIReloadListeners;
import liedge.ltxindustries.entity.damage.LTXIDamageSource;
import liedge.ltxindustries.item.UpgradableEquipmentItem;
import liedge.ltxindustries.lib.upgrades.Upgrades;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public interface SmartProjectileEntity extends TraceableEntity
{
    Upgrades getUpgrades();

    ItemStack getWeaponItem();

    @Override
    @Nullable LivingEntity getOwner();

    void setOwner(@Nullable LivingEntity owner);

    default TargetPredicate getTargets()
    {
        return TargetPredicate.ALL;
    }

    default List<Entity> getEntities(ServerLevel level, AABB bb, @Nullable LivingEntity owner, @Nullable Entity directHit)
    {
        return level.getEntities((Entity) this, bb, e -> LTXIEntityUtil.isValidContextTarget(e, owner, getTargets()) && !Objects.equals(directHit, e));
    }

    default List<Entity> getEntities(ServerLevel level, Vec3 hitLocation, double radius, @Nullable LivingEntity owner, @Nullable Entity directHit)
    {
        radius *= 2;
        return getEntities(level, AABB.ofSize(hitLocation, radius, radius, radius), owner, directHit);
    }

    default boolean hurtEntity(ServerLevel level, ResourceKey<DamageType> damageType, Entity target, @Nullable LivingEntity owner, double baseDamage)
    {
        Upgrades upgrades = getUpgrades();
        ItemStack weaponItem = getWeaponItem();
        Entity thisEntity = (Entity) this;
        DamageSource source = new LTXIDamageSource(thisEntity.registryAccess().holderOrThrow(damageType), thisEntity, owner, upgrades, weaponItem, true);

        LootContext context = LimaLootUtil.entityLootContext(level, target, source);

        double damage = weaponItem.getItem() instanceof UpgradableEquipmentItem uei ? uei.getUpgradedDamage(upgrades, context, baseDamage) : baseDamage;
        damage = LTXIReloadListeners.equipmentDamageModifiers().apply(weaponItem, context, baseDamage, damage);

        return damage >= 1e-4 && target.hurtServer(level, source, (float) damage);
    }
}