package liedge.ltxindustries.entity;

import liedge.limacore.LimaCommonConstants;
import liedge.limacore.util.LimaCoreObjects;
import liedge.ltxindustries.item.UpgradableEquipmentItem;
import liedge.ltxindustries.lib.upgrades.Upgrades;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public abstract class UpgradesAwareEntity extends Entity implements TraceableEntity
{
    private @Nullable UUID ownerId;
    private @Nullable LivingEntity owner;
    private ItemStack weaponItem = ItemStack.EMPTY;
    private @Nullable TargetPredicate predicate;

    protected UpgradesAwareEntity(EntityType<?> entityType, Level level)
    {
        super(entityType, level);
    }

    @Override
    public ItemStack getWeaponItem()
    {
        return weaponItem;
    }

    public void setWeaponItem(ItemStack weaponItem)
    {
        this.weaponItem = weaponItem;
    }

    public Upgrades getUpgrades()
    {
        return UpgradableEquipmentItem.getUpgradesFrom(getWeaponItem());
    }

    protected TargetPredicate getOrCreateTargetFilter()
    {
        if (predicate == null)
        {
            predicate = TargetPredicate.create(getUpgrades());
        }

        return predicate;
    }

    @Override
    public @Nullable LivingEntity getOwner()
    {
        if (owner == null && ownerId != null && level() instanceof ServerLevel level)
        {
            owner = LimaCoreObjects.tryCast(LivingEntity.class, level.getEntity(ownerId));
        }

        return owner;
    }

    public void setOwner(@Nullable LivingEntity owner)
    {
        this.owner = owner;
        this.ownerId = owner != null ? owner.getUUID() : null;
    }

    protected List<Entity> getEntitiesInAOE(Level level, AABB bb, @Nullable LivingEntity owner, @Nullable Entity directHit)
    {
        return level.getEntities(this, bb, e -> LTXIEntityUtil.isValidContextTarget(e, owner, getOrCreateTargetFilter()) && !Objects.equals(directHit, e));
    }

    protected List<Entity> getEntitiesInAOE(Level level, Vec3 hitLocation, double radius, @Nullable LivingEntity owner, @Nullable Entity directHit)
    {
        radius *= 2;
        return getEntitiesInAOE(level, AABB.ofSize(hitLocation, radius, radius, radius), owner, directHit);
    }

    @Override
    public boolean fireImmune()
    {
        return true;
    }

    @Override
    public PushReaction getPistonPushReaction()
    {
        return PushReaction.IGNORE;
    }

    @Override
    public boolean isPushedByFluid(FluidType type)
    {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input)
    {
        this.tickCount = input.getIntOr("age", 0);
        this.ownerId = input.read(LimaCommonConstants.KEY_OWNER, UUIDUtil.CODEC).orElse(null);
        this.weaponItem = input.read("weapon_item", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output)
    {
        output.putInt("age", tickCount);
        output.storeNullable(LimaCommonConstants.KEY_OWNER, UUIDUtil.CODEC, ownerId);
        output.store("weapon_item", ItemStack.OPTIONAL_CODEC, weaponItem);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount)
    {
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) { }
}