package liedge.ltxindustries.entity;

import liedge.limacore.LimaCommonConstants;
import liedge.limacore.util.LimaCoreObjects;
import liedge.limacore.util.LimaNbtUtil;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.item.UpgradableEquipmentItem;
import liedge.ltxindustries.lib.upgrades.UpgradesContainerBase;
import liedge.ltxindustries.lib.upgrades.machine.MachineUpgrades;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
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
    private TargetPredicate predicate;

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

    public UpgradesContainerBase<?, ?> getUpgrades()
    {
        return UpgradableEquipmentItem.getEquipmentUpgradesFromStack(getWeaponItem());
    }

    protected TargetPredicate getOrCreateTargetFilter()
    {
        if (predicate == null)
        {
            predicate = TargetPredicate.create(level(), getUpgrades());
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
        return level.getEntities(this, bb, e -> LTXIEntityUtil.checkWeaponTargetValidity(owner, e, getOrCreateTargetFilter()) && !Objects.equals(directHit, e));
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
    protected void readAdditionalSaveData(CompoundTag tag)
    {
        this.tickCount = tag.getInt("age");
        this.ownerId = LimaNbtUtil.getOptionalUUID(tag, LimaCommonConstants.KEY_OWNER);
        this.weaponItem = ItemStack.parseOptional(registryAccess(), tag.getCompound("weapon_item"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag)
    {
        tag.putInt("age", tickCount);
        LimaNbtUtil.putOptionalUUID(tag, LimaCommonConstants.KEY_OWNER, ownerId);
        if (!weaponItem.isEmpty()) tag.put("weapon_item", weaponItem.save(registryAccess()));
    }

    protected MachineUpgrades readMachineUpgrades(CompoundTag tag)
    {
        return LimaNbtUtil.tryDecode(MachineUpgrades.CODEC, registryOps(), tag, LTXIConstants.KEY_UPGRADES_CONTAINER, MachineUpgrades.EMPTY);
    }

    protected void writeMachineUpgrades(MachineUpgrades upgrades, CompoundTag tag)
    {
        LimaNbtUtil.tryEncodeTo(MachineUpgrades.CODEC, registryOps(), upgrades, tag, LTXIConstants.KEY_UPGRADES_CONTAINER);
    }

    private RegistryOps<Tag> registryOps()
    {
        return RegistryOps.create(NbtOps.INSTANCE, registryAccess());
    }
}