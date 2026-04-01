package liedge.ltxindustries.blockentity.turret;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntList;
import liedge.limacore.LimaCommonConstants;
import liedge.limacore.blockentity.OwnableBlockEntity;
import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.limacore.network.sync.ManualDataWatcher;
import liedge.limacore.registry.game.LimaCoreDataComponents;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.ltxindustries.blockentity.base.ConfigurableIOBlockEntityType;
import liedge.ltxindustries.blockentity.base.EnergyConsumerBlockEntity;
import liedge.ltxindustries.blockentity.template.ProductionMachineBlockEntity;
import liedge.ltxindustries.entity.LTXIEntityUtil;
import liedge.ltxindustries.entity.TargetPredicate;
import liedge.ltxindustries.lib.TurretTargetTracker;
import liedge.ltxindustries.lib.upgrades.machine.MachineUpgrades;
import liedge.ltxindustries.registry.game.LTXISounds;
import liedge.ltxindustries.util.LTXITooltipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;

public abstract class TurretBlockEntity extends ProductionMachineBlockEntity implements EnergyConsumerBlockEntity, OwnableBlockEntity
{
    private static final Logger LOG = LogUtils.getLogger();

    // General properties
    protected final Vec3 traceStart;
    private final AABB searchArea;
    private final AABB attackArea;
    private final AABB boundingBox;

    private int energyUsage = getBaseEnergyUsage();
    private @Nullable UUID ownerUUID;
    private int serverTimer;
    private TurretState turretState = TurretState.INACTIVE;

    private final TargetPredicate defaultFilter = (level, targetEntity, _) -> this.isValidDefaultTarget(level, targetEntity);
    private TargetPredicate targetFilter = defaultFilter;
    private @Nullable UUID targetId;
    private @Nullable Entity target;
    private final Queue<Entity> targetQueue = new ArrayDeque<>();
    @SuppressWarnings("NotNullFieldNotInitialized")
    private LimaDataWatcher<IntList> queueWatcher;

    // Client properties
    private int clientAimTicks;
    private int clientAimTicks0;
    private float turretYRot0;
    protected float turretYRot;
    private float turretXRot0;
    protected float turretXRot;
    private double targetDistance;
    private boolean lookingAtTarget;

    protected TurretBlockEntity(ConfigurableIOBlockEntityType<?> type, BlockPos pos, BlockState state, double traceY, double horizontalSearchRadius, double verticalSearchRadius)
    {
        super(type, pos, state, 2, 0, 20);
        this.traceStart = new Vec3(pos.getX() + 0.5d, pos.getY() + traceY, pos.getZ() + 0.5d);
        this.searchArea = AABB.ofSize(traceStart, horizontalSearchRadius * 2d, verticalSearchRadius * 2d, horizontalSearchRadius * 2d);
        this.attackArea = searchArea.inflate(3d);
        this.boundingBox = new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1);
    }

    public Vec3 getTraceStart()
    {
        return traceStart;
    }

    public AABB getBoundingBox()
    {
        return boundingBox;
    }

    public TurretState getTurretState()
    {
        return turretState;
    }

    protected void setTurretState(TurretState turretState)
    {
        this.turretState = turretState;
        setChanged();
    }

    @Nullable
    protected Entity getTarget()
    {
        if (target == null && targetId != null && level instanceof ServerLevel sl)
        {
            target = sl.getEntity(targetId);
        }

        return target;
    }

    public void setTarget(@Nullable Entity target)
    {
        this.target = target;
        this.targetId = target != null ? target.getUUID() : null;

        setChanged();
    }

    public Queue<Entity> getTargetQueue()
    {
        return targetQueue;
    }

    protected boolean advanceServerTimer(int interval)
    {
        if (serverTimer >= interval)
        {
            serverTimer = 0;
            return true;
        }
        else
        {
            serverTimer++;
            return false;
        }
    }

    protected abstract int getMaxTargetsPerSearch();

    protected abstract int getSearchInterval();

    protected abstract int getChargingDuration();

    protected abstract boolean isValidDefaultTarget(ServerLevel level, Entity targetEntity);

    protected int getCooldownDuration()
    {
        return 4;
    }

    @Contract("null->false")
    protected boolean targetStillValid(@Nullable Entity entity)
    {
        return entity != null && LTXIEntityUtil.isEntityAlive(entity) && attackArea.intersects(entity.getBoundingBox());
    }

    protected void setNextTarget(TurretTargetTracker tracker)
    {
        tracker.remove(target);
        setTarget(null);

        while (!targetQueue.isEmpty())
        {
            Entity next = targetQueue.poll();
            queueWatcher.setChanged(true);

            if (targetStillValid(next))
            {
                setTarget(next);
                return;
            }
            else
            {
                tracker.remove(next);
            }
        }

        setTurretState(TurretState.SEARCHING);
    }

    protected void purgeTargets(TurretTargetTracker tracker)
    {
        tracker.remove(target);
        tracker.removeAll(targetQueue);

        setTarget(null);
        targetQueue.clear();
        queueWatcher.setChanged(true);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state)
    {
        super.preRemoveSideEffects(pos, state);
        purgeTargets(TurretTargetTracker.getOrDefault(getOwner()));
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        collector.register(AutomaticDataWatcher.keepEnumSynced(TurretState.class, this::getTurretState, this::setTurretState));
        collector.register(AutomaticDataWatcher.keepClientsideEntitySynced(this::getTarget, this::setClientTarget));

        this.queueWatcher = ManualDataWatcher.manuallyTrack(LimaCoreNetworkSerializers.INT_LIST, () -> LTXIEntityUtil.flattenEntityIds(targetQueue), list ->
        {
            targetQueue.clear();

            for (int eid : list)
            {
                Entity entity = LimaCoreClientUtil.getClientEntity(eid);
                if (entity != null) targetQueue.add(entity);
            }
        });
        collector.register(queueWatcher);
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
        setChanged();
    }

    //#region Energy User implementation

    @Override
    public int getEnergyUsage()
    {
        return energyUsage;
    }

    @Override
    public void setEnergyUsage(int energyUsage)
    {
        this.energyUsage = energyUsage;
    }

    //#endregion

    //#region Upgrades User implementation

    @Override
    public void appendStatsTooltips(TooltipLineConsumer consumer)
    {
        consumer.accept(LTXITooltipUtil.makeOwnerComponent(ownerUUID));
        LTXITooltipUtil.appendEnergyUsageTooltip(consumer, getEnergyUsage());
    }

    @Override
    public void onUpgradeRefresh(LootContext context, MachineUpgrades upgrades)
    {
        super.onUpgradeRefresh(context, upgrades);
        EnergyConsumerBlockEntity.applyUpgrades(this, context, upgrades);
        this.targetFilter = TargetPredicate.create(upgrades, defaultFilter);
    }

    //#endregion

    protected abstract void turretFiringTick(ServerLevel level, BlockPos pos, BlockState state, @Nullable Player owner, TurretTargetTracker tracker);

    @Override
    protected void tickServer(ServerLevel level, BlockPos pos, BlockState state)
    {
        // Fill internal energy buffer from energy item slot
        pullEnergyFromAux();

        // Auto eject items
        tickItemAutoOutput(100, getOutputInventory());

        Player owner = getOwner();
        TurretTargetTracker tracker = TurretTargetTracker.getOrDefault(owner);

        switch (turretState)
        {
            case INACTIVE ->
            {
                if (advanceServerTimer(20) && hasMinimumEnergy())
                {
                    setTurretState(TurretState.SEARCHING);
                }
            }
            case SEARCHING ->
            {
                if (advanceServerTimer(getSearchInterval()))
                {
                    if (hasMinimumEnergy())
                    {
                        if (!targetQueue.isEmpty()) purgeTargets(tracker);

                        List<Entity> foundTargets = level.getEntities(owner, searchArea, e -> LTXIEntityUtil.isValidContextTarget(e, owner, targetFilter) && !tracker.contains(e))
                                .stream()
                                .sorted(Comparator.comparingDouble(e -> e.distanceToSqr(traceStart)))
                                .filter(e -> {
                                    if (tracker.add(e))
                                    {
                                        HitResult blockTrace = level.clip(new TurretClipContext(traceStart, e.getBoundingBox().getCenter(), e, boundingBox));
                                        if (blockTrace.getType() != HitResult.Type.BLOCK) return true;
                                    }

                                    tracker.remove(e);
                                    return false;
                                })
                                .limit(getMaxTargetsPerSearch())
                                .toList();

                        LOG.debug("Scanned and found {} targets", foundTargets.size());

                        if (!foundTargets.isEmpty())
                        {
                            targetQueue.addAll(foundTargets);

                            level.playSound(null, traceStart.x, traceStart.y, traceStart.z, LTXISounds.TURRET_TARGET_FOUND, SoundSource.BLOCKS, 2f, Mth.randomBetween(level.getRandom(), 0.875f, 0.95f));
                            setTarget(targetQueue.poll());
                            queueWatcher.setChanged(true);

                            int chargeTime = getChargingDuration();
                            TurretState nextState = chargeTime > 0 ? TurretState.CHARGING : TurretState.FIRING;
                            setTurretState(nextState);
                        }
                    }
                    else
                    {
                        setTurretState(TurretState.INACTIVE);
                    }
                }
            }
            case CHARGING, COOLDOWN ->
            {
                int duration = turretState == TurretState.CHARGING ? getChargingDuration() : getCooldownDuration();

                if (targetStillValid(target))
                {
                    if (advanceServerTimer(duration))
                    {
                        setTurretState(TurretState.FIRING);
                    }
                }
                else
                {
                    setNextTarget(tracker);
                }
            }
            case FIRING -> turretFiringTick(level, pos, state, owner, tracker);
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter getter)
    {
        super.applyImplicitComponents(getter);
        setOwnerUUID(getter.get(LimaCoreDataComponents.OWNER));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components)
    {
        super.collectImplicitComponents(components);
        components.set(LimaCoreDataComponents.OWNER, getOwnerUUID());
    }

    @Override
    public void removeComponentsFromTag(ValueOutput output)
    {
        super.removeComponentsFromTag(output);
        output.discard(LimaCommonConstants.KEY_OWNER);
    }

    @Override
    protected void loadAdditional(ValueInput input)
    {
        super.loadAdditional(input);
        loadOwnerID(input);
        turretState = input.read("turret_state", TurretState.CODEC).orElse(TurretState.SEARCHING);
        targetId = input.read("target", UUIDUtil.CODEC).orElse(null);
    }

    @Override
    protected void saveAdditional(ValueOutput output)
    {
        super.saveAdditional(output);
        saveOwnerID(output);
        output.store("turret_state", TurretState.CODEC, turretState);
        output.storeNullable("target", UUIDUtil.CODEC, targetId);
    }

    //#region Client functions

    @Override
    protected void tickClient(Level level, BlockPos pos, BlockState state)
    {
        // Set old rotations
        turretYRot0 = turretYRot;
        turretXRot0 = turretXRot;

        // Point towards active target
        if (targetStillValid(target))
        {
            Vec3 dir = target.getBoundingBox().getCenter().subtract(traceStart);
            targetDistance = dir.length();

            float nextYRot = 180f - LimaCoreMath.getYRot(dir);
            float nextXRot = -LimaCoreMath.getXRot(dir);

            turretYRot = Mth.approachDegrees(turretYRot, nextYRot, 20f);
            turretXRot = Mth.approachDegrees(turretXRot, nextXRot, 10f);

            lookingAtTarget = Mth.degreesDifferenceAbs(turretYRot0, turretYRot) <= 10f;

            clientAimTicks0 = clientAimTicks;
            clientAimTicks++;
        }
        else // Idle animation
        {
            target = null;
            targetDistance = 0d;
            lookingAtTarget = false;

            if (targetQueue.isEmpty())
            {
                clientAimTicks0 = 0;
                clientAimTicks = 0;
            }

            if (turretState != TurretState.INACTIVE)
            {
                turretXRot = Mth.approachDegrees(turretXRot, 0, 5f);
                turretYRot = (turretYRot + 2) % 360;
            }
            else
            {
                turretXRot = Mth.approachDegrees(turretXRot, -30, 10f);
                turretYRot = Mth.approachDegrees(turretYRot, -getFacing().toYRot(), 15f);
            }
        }
    }

    public @Nullable Entity getClientTarget()
    {
        return target;
    }

    protected void setClientTarget(@Nullable Entity target)
    {
        if (target != null && !LTXIEntityUtil.isEntityAlive(target)) target = null;
        this.target = target;
    }

    public double getTargetDistance()
    {
        return targetDistance;
    }

    public boolean isLookingAtTarget()
    {
        return lookingAtTarget;
    }

    public float lerpYRot(float partialTick)
    {
        return Mth.rotLerp(partialTick, turretYRot0, turretYRot);
    }

    public float lerpXRot(float partialTick)
    {
        return Mth.rotLerp(partialTick, turretXRot0, turretXRot);
    }

    public float lerpAimTicks(float partialTick, float maxTicks)
    {
        return Math.min(Mth.lerp(partialTick, clientAimTicks0, clientAimTicks) / maxTicks, 1f);
    }

    //#endregion
}