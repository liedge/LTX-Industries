package liedge.ltxindustries.entity;

import liedge.limacore.util.LimaNbtUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class AutoTrackingProjectile extends LimaTraceableProjectile
{
    private @Nullable UUID targetUUID;
    private @Nullable Entity targeted;

    protected AutoTrackingProjectile(EntityType<?> type, Level level)
    {
        super(type, level);
    }

    public @Nullable Entity getTargetEntity()
    {
        // Cache target if UUID is present
        if (targeted == null && targetUUID != null && level() instanceof ServerLevel serverLevel) targeted = serverLevel.getEntity(targetUUID);

        // More aggressively clear dead target references
        if (targeted != null && !LTXIEntityUtil.isEntityAlive(targeted))
        {
            targeted = null;
            targetUUID = null;
        }

        return targeted;
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
            Entity target = getTargetEntity();
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