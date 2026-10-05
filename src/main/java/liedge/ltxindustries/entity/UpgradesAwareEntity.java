package liedge.ltxindustries.entity;

import liedge.limacore.LimaCommonConstants;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.limacore.util.LimaCoreObjects;
import liedge.ltxindustries.item.UpgradableEquipmentItem;
import liedge.ltxindustries.lib.upgrades.Upgrades;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class UpgradesAwareEntity extends Entity implements SmartProjectileEntity
{
    public static final int DEFAULT_LIFETIME = 1200;

    private int age;
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

    @Override
    public Upgrades getUpgrades()
    {
        return UpgradableEquipmentItem.getUpgradesFrom(getWeaponItem());
    }

    @Override
    public TargetPredicate getTargets()
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

    @Override
    public void setOwner(@Nullable LivingEntity owner)
    {
        this.owner = owner;
        this.ownerId = owner != null ? owner.getUUID() : null;
    }

    @Override
    public void tick()
    {
        super.tick();

        Level level = level();

        if (level instanceof ServerLevel serverLevel)
        {
            if (age++ >= getLifetime())
            {
                onExpire(serverLevel, age);
            }

            tickServer(serverLevel);
        }
        else
        {
            tickClient(level);
        }

        updateMotion();
    }

    protected int getLifetime()
    {
        return DEFAULT_LIFETIME;
    }

    protected void onExpire(ServerLevel level, int age)
    {
        discard();
    }

    protected void tickServer(ServerLevel level) { }

    protected void tickClient(Level level) { }

    protected void updateMotion()
    {
        Vec3 delta = getDeltaMovement();

        double gravity = getGravity();
        if (gravity > 0)
        {
            delta = delta.add(0, -gravity, 0);
            setDeltaMovement(delta);
        }

        setXRot(Mth.rotLerp(0.5f, xRotO, LimaCoreMath.getXRot(delta)));
        setYRot(Mth.rotLerp(0.5f, yRotO, LimaCoreMath.getYRot(delta)));
        setPos(position().add(delta));
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
        this.age = input.getShortOr(LimaCommonConstants.KEY_AGE, (short) 0);
        this.ownerId = input.read(LimaCommonConstants.KEY_OWNER, UUIDUtil.CODEC).orElse(null);
        this.weaponItem = input.read("weapon_item", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output)
    {
        output.putShort(LimaCommonConstants.KEY_AGE, (short) age);
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