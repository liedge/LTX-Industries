package liedge.limatech.entity;

import liedge.limacore.util.LimaNbtUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class AutoTrackingProjectile extends LimaTechProjectile
{
    private @Nullable UUID targetUUID;
    private @Nullable Entity targeted;

    protected AutoTrackingProjectile(EntityType<?> type, Level level)
    {
        super(type, level);
    }

    public @Nullable Entity getTargeted()
    {
        if (targeted != null && targeted.isAlive())
        {
            return targeted;
        }
        else if (targetUUID != null && level() instanceof ServerLevel serverLevel)
        {
            targeted = serverLevel.getEntity(targetUUID);
            return targeted;
        }
        else
        {
            return null;
        }
    }

    public void setTargetEntity(@Nullable Entity targeted)
    {
        if (targeted != null)
        {
            this.targetUUID = targeted.getUUID();
            this.targeted = targeted;
        }
    }

    @Override
    protected void tickProjectile(Level level, boolean isClientSide)
    {
        if (!isClientSide)
        {
            Entity target = getTargeted();
            if (target != null)
            {
                aimTowardsEntity(target, getDeltaMovement().length(), 0, 30);
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        targetUUID = LimaNbtUtil.getOptionalUUID(tag, "target");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        LimaNbtUtil.putOptionalUUID(tag, "target", targetUUID);
    }
}