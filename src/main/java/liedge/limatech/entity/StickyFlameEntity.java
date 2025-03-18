package liedge.limatech.entity;

import liedge.limacore.util.LimaMathUtil;
import liedge.limatech.registry.bootstrap.LimaTechDamageTypes;
import liedge.limatech.registry.LimaTechEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class StickyFlameEntity extends BaseTraceableEntity
{
    private boolean stuckOnBlock;

    public StickyFlameEntity(EntityType<?> entityType, Level level)
    {
        super(entityType, level);
    }

    public StickyFlameEntity(Level level)
    {
        this(LimaTechEntities.STICKY_FLAME.get(), level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    public void tick()
    {
        Level level = level();

        // Discard if lifetime is complete
        if (tickCount > 80)
        {
            discard();
        }

        // Do fluid calculations
        updateFluidHeightOnly(level);

        // Discard if in rain or water
        if (isInWaterOrRain())
        {
            discard();
            level.playSound(null, getX(), getY(), getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.NEUTRAL);
        }

        // Handle motion
        final double x = getX();
        final double y = getY();
        final double z = getZ();

        this.xo = x;
        this.yo = y;
        this.zo = z;

        if (stuckOnBlock && level.noBlockCollision(this, this.getBoundingBox().inflate(0.001d)))
        {
            this.stuckOnBlock = false;
        }
        else
        {
            Vec3 delta = getDeltaMovement();
            setDeltaMovement(delta.x, delta.y - 0.05d, delta.z);

            Vec3 start = position();
            Vec3 end = start.add(delta);

            BlockHitResult hitResult = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.WATER, this));

            if (hitResult.getType() != HitResult.Type.MISS)
            {
                setPos(hitResult.getLocation());
                setDeltaMovement(Vec3.ZERO);
                stuckOnBlock = true;
            }
            else
            {
                setPos(end);
            }
        }

        if (!level.isClientSide)
        {
            if (tickCount % 4 == 0)
            {
                level.getEntities(this, getBoundingBox(), hit -> LimaTechEntityUtil.isValidWeaponTarget(getOwner(), hit)).forEach(hit -> {
                    DamageSource source = level.damageSources().source(LimaTechDamageTypes.STICKY_FLAME, this, getOwner());

                    hit.setRemainingFireTicks(200);
                    hit.hurt(source, 4f);
                });
            }
        }
        else
        {
            if (LimaMathUtil.rollRandomChance(0.05d))
            {
                level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, getRandomX(0.5f), getRandomY(), getRandomZ(0.5f), 0, 0.2d, 0);
            }
        }
    }

    @Override
    public PushReaction getPistonPushReaction()
    {
        return PushReaction.IGNORE;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        stuckOnBlock = tag.getBoolean("stuck");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("stuck", stuckOnBlock);
    }
}