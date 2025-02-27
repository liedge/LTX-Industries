package liedge.limatech.blockentity;

import liedge.limacore.LimaCommonConstants;
import liedge.limacore.blockentity.BlockEntityWithOwner;
import liedge.limacore.blockentity.LimaBlockEntity;
import liedge.limacore.blockentity.LimaBlockEntityType;
import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.limacore.util.LimaNbtUtil;
import liedge.limatech.LimaTechTags;
import liedge.limatech.entity.BaseRocketEntity;
import liedge.limatech.entity.LimaTechEntityUtil;
import liedge.limatech.lib.TurretTargetList;
import liedge.limatech.registry.LimaTechSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Queue;
import java.util.UUID;

import static liedge.limacore.util.LimaMathUtil.toDeg;
import static liedge.limacore.util.LimaMathUtil.vec2Length;

public class RocketTurretBlockEntity extends LimaBlockEntity implements BlockEntityWithOwner
{
    private final Vec3 projectileStart;
    private final Queue<Entity> targetQueue = new ArrayDeque<>();
    private final AABB targetArea;

    private @Nullable UUID ownerUUID;
    private @Nullable Player owner;
    private @Nullable Entity currentTarget;
    private int ticker0 = 0;
    private int ticker = 0;

    // Client properties
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

    public @Nullable Entity getCurrentTarget()
    {
        return currentTarget;
    }

    public void onRemovedFromLevel()
    {
        TurretTargetList.getOrDefault(getOwner()).removeTarget(currentTarget);
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
    public @Nullable UUID getOwnerUUID()
    {
        return ownerUUID;
    }

    @Override
    public void setOwnerUUID(@Nullable UUID ownerUUID)
    {
        this.ownerUUID = ownerUUID;
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
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        collector.register(AutomaticDataWatcher.keepClientsideEntitySynced(() -> currentTarget, entity -> {
            // Don't set the current target on the client if it's already dead when packet arrives. Also reset client timers.
            if (entity != null && !checkAlive(entity)) entity = null;
            this.currentTarget = entity;
            this.ticker0 = 0;
            this.ticker = 0;
        }));
    }

    @Override
    protected void tickServer(Level level, BlockPos pos, BlockState state)
    {
        // Get the target list
        Player owner = getOwner();
        TurretTargetList targetList = TurretTargetList.getOrDefault(owner);

        // Fill targeting queue
        if (targetQueue.isEmpty() && ticker > 40)
        {
            level.getEntities(owner, getTargetArea(), e -> isValidTarget(level, owner, e) && !targetList.containsTarget(e))
                    .stream()
                    .sorted(Comparator.comparingDouble(e -> e.distanceToSqr(projectileStart)))
                    .limit(10)
                    .forEach(targetQueue::add);

            ticker = 0;
        }

        // Check if target is not null and alive
        if (currentTarget != null && checkAlive(currentTarget))
        {
            if (ticker > 30) // Fire missile at 30 ticks
            {
                BaseRocketEntity.TurretRocket missile = new BaseRocketEntity.TurretRocket(level);
                missile.setOwner(owner);
                missile.setPos(projectileStart);
                missile.aimTowardsEntity(currentTarget, 2.5f, 0);
                missile.setTargetEntity(currentTarget);
                level.addFreshEntity(missile);
                level.playSound(null, projectileStart.x, projectileStart.y, projectileStart.z, LimaTechSounds.ROCKET_LAUNCHER_FIRE.get(), SoundSource.BLOCKS, 1.5f, Mth.randomBetween(level.random, 0.75f, 0.9f));

                targetList.removeTarget(currentTarget);
                currentTarget = null;
                ticker = 0;
            }
        }
        else // If target is null or dead
        {
            currentTarget = null; // Set to null since target can be dead but still stored in BE, fixes mini-memory leak and more consistent with client behavior

            // If target queue is not empty, select next target
            if (!targetQueue.isEmpty())
            {
                while (!targetQueue.isEmpty())
                {
                    Entity next = targetQueue.poll();
                    if (targetList.addTarget(next))
                    {
                        currentTarget = next;
                        break;
                    }
                }

                ticker = 0;
            }
        }

        ticker++;
    }

    @Override
    protected void tickClient(Level level, BlockPos pos, BlockState state)
    {
        if (currentTarget != null && checkAlive(currentTarget))
        {
            double dx = currentTarget.getX() - projectileStart.x();
            double dy = currentTarget.getEyePosition().y() - projectileStart.y();
            double dz = currentTarget.getZ() - projectileStart.z();

            turretYRot0 = turretYRot;
            turretXRot0 = turretXRot;
            turretYRot = Mth.approachDegrees(turretYRot, toDeg(Mth.atan2(dz, dx)) + 90f, 20f);
            turretXRot = Mth.approachDegrees(turretXRot, toDeg(Mth.atan2(dy, vec2Length(dx, dz))), 10f);

            ticker0 = ticker;
            ticker++;
        }
        else
        {
            currentTarget = null;
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
        return LimaTechEntityUtil.isValidWeaponTarget(owner, entity) && entity.getType().is(LimaTechTags.EntityTypes.ROCKET_TURRET_TARGETS) && level.clip(new ClipContext(projectileStart, entity.getBoundingBox().getCenter(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity)).getType() != HitResult.Type.BLOCK;
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