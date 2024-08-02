package liedge.limatech.blockentity;

import liedge.limacore.LimaCommonConstants;
import liedge.limacore.blockentity.BlockEntityWithOwner;
import liedge.limacore.blockentity.LimaBlockEntity;
import liedge.limacore.blockentity.LimaBlockEntityType;
import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.limacore.network.sync.SimpleDataWatcher;
import liedge.limacore.util.LimaNbtUtil;
import liedge.limatech.LimaTechTags;
import liedge.limatech.entity.LimaTechProjectile;
import liedge.limatech.entity.MissileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.*;

import static liedge.limacore.util.LimaMathUtil.toDeg;
import static liedge.limacore.util.LimaMathUtil.vec2Length;

public class RocketTurretBlockEntity extends LimaBlockEntity implements BlockEntityWithOwner
{
    private final Vec3 projectileStart;
    private final Queue<Entity> currentTargets = new ArrayDeque<>();
    private final AABB targetArea;

    private @Nullable UUID ownerUUID;
    private @Nullable Player owner;
    private int ticker0 = 0;
    private int ticker = 0;

    // Client properties
    private @Nullable Entity remoteTarget;
    private float turretYRot0;
    private float turretYRot;
    private float turretXRot0;
    private float turretXRot;

    public RocketTurretBlockEntity(LimaBlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.projectileStart = new Vec3(pos.getX() + 0.5d, pos.getY() + 1.6875d, pos.getZ() + 0.5d);
        this.targetArea = AABB.ofSize(Vec3.atCenterOf(pos), 100, 100, 100);
    }

    public AABB getTargetArea()
    {
        return targetArea;
    }

    @Override
    public @Nullable Player getOwner()
    {
        if (owner != null && !owner.isRemoved())
        {
            return owner;
        }
        else if (ownerUUID != null && level instanceof ServerLevel serverLevel)
        {
            return serverLevel.getPlayerByUUID(ownerUUID);
        }
        else
        {
            return null;
        }
    }

    @Override
    public void setOwner(@Nullable Player owner)
    {
        if (owner != null)
        {
            this.owner = owner;
            this.ownerUUID = owner.getUUID();
            setChanged();
        }
    }

    @Override
    protected List<LimaDataWatcher<?>> defineDataWatchers()
    {
        return List.of(SimpleDataWatcher.keepOptionalRemoteEntitySynced(() -> remoteTarget, e -> {
            if (e != null && !checkAlive(e)) e = null;
            this.remoteTarget = e;
            this.ticker0 = 0;
            this.ticker = 0;
        }));
    }

    @Override
    protected void tickServer(Level level, BlockPos pos, BlockState state)
    {
        if (currentTargets.isEmpty() && ticker > 40)
        {
            Player owner = getOwner();

            level.getEntities(owner, targetArea, e -> isValidTarget(level, owner, e))
                    .stream()
                    .sorted(Comparator.comparingDouble(e -> e.distanceToSqr(projectileStart)))
                    .limit(10)
                    .forEach(currentTargets::add);

            remoteTarget = currentTargets.peek();
            ticker = 0;
        }
        else if (!currentTargets.isEmpty() && ticker > 30)
        {
            Entity next = currentTargets.poll();
            if (next != null && checkAlive(next))
            {
                MissileEntity missile = new MissileEntity(level);
                missile.setOwner(getOwner());
                missile.setPos(projectileStart);
                missile.aimTowardsEntity(next, 2f, 0);
                missile.setTargetEntity(next);
                level.addFreshEntity(missile);
            }

            remoteTarget = currentTargets.peek();
            ticker = 0;
        }
        else
        {
            ticker++;
        }
    }

    @Override
    protected void tickClient(Level level, BlockPos pos, BlockState state)
    {
        if (remoteTarget != null && checkAlive(remoteTarget))
        {
            double dx = remoteTarget.getX() - projectileStart.x();
            double dy = remoteTarget.getEyePosition().y() - projectileStart.y();
            double dz = remoteTarget.getZ() - projectileStart.z();

            turretYRot0 = turretYRot;
            turretXRot0 = turretXRot;
            turretYRot = Mth.approachDegrees(turretYRot, toDeg(Mth.atan2(dz, dx)) + 90f, 20f);
            turretXRot = Mth.approachDegrees(turretXRot, toDeg(Mth.atan2(dy, vec2Length(dx, dz))), 10f);

            ticker0 = ticker;
            ticker++;
        }
        else
        {
            ticker = 0;
            ticker0 = 0;

            turretXRot0 = turretXRot;
            turretXRot = Mth.approachDegrees(turretXRot, 0, 5f);
            turretYRot0 = turretYRot;
            turretYRot = (turretYRot - 2) % 360;
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);
        this.ownerUUID = LimaNbtUtil.getOptionalUUID(tag, LimaCommonConstants.KEY_OWNER);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
        LimaNbtUtil.putOptionalUUID(tag, LimaCommonConstants.KEY_OWNER, ownerUUID);
    }

    private boolean isValidTarget(Level level, @Nullable Player owner, Entity entity)
    {
        return LimaTechProjectile.canHitEntity(owner, entity) && entity.getType().is(LimaTechTags.EntityTypes.FLYING_MOBS) && level.clip(new ClipContext(projectileStart, entity.getBoundingBox().getCenter(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity)).getType() != HitResult.Type.BLOCK;
    }

    private boolean checkAlive(Entity entity)
    {
        if (entity instanceof EnderDragon)
        {
            return ((EnderDragon) entity).getHealth() > 1.0f;
        }
        else
        {
            return entity.isAlive();
        }
    }

    @Nullable
    public Entity getRemoteTarget()
    {
        return remoteTarget;
    }

    public float lerpTicker(float partialTick)
    {
        return Math.min(Mth.lerp(partialTick, ticker0, ticker) / 24f, 1f);
    }

    public float lerpYRot(float partialTick)
    {
        return -Mth.rotLerp(partialTick, turretYRot0, turretYRot);
    }

    public float lerpXRot(float partialTick)
    {
        return -Mth.rotLerp(partialTick, turretXRot0, turretXRot);
    }
}