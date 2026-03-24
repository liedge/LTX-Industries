package liedge.ltxindustries.entity;

import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class HomingProjectileEntity extends LTXIProjectileEntity
{
    private @Nullable UUID targetUUID;
    private @Nullable Entity targeted;

    protected HomingProjectileEntity(EntityType<?> type, Level level)
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
    protected void tickServer(ServerLevel level, @Nullable LivingEntity owner)
    {
        Entity target = getTargetEntity();
        if (target != null)
        {
            aimAtEntity(target, getDeltaMovement().length(), 20f);
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input)
    {
        super.readAdditionalSaveData(input);
        targetUUID = input.read("target", UUIDUtil.CODEC).orElse(null);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output)
    {
        super.addAdditionalSaveData(output);
        output.storeNullable("target", UUIDUtil.CODEC, targetUUID);
    }
}