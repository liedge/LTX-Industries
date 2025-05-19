package liedge.limatech.blockentity;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.IntList;
import liedge.limacore.LimaCommonConstants;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.IOAccessSets;
import liedge.limacore.blockentity.OwnableBlockEntity;
import liedge.limacore.blockentity.RelativeHorizontalSide;
import liedge.limacore.capability.energy.LimaBlockEntityEnergyStorage;
import liedge.limacore.capability.energy.LimaEnergyUtil;
import liedge.limacore.capability.itemhandler.LimaItemHandlerBase;
import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.limacore.network.sync.ManualDataWatcher;
import liedge.limacore.registry.game.LimaCoreDataComponents;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaItemUtil;
import liedge.limacore.util.LimaStreamsUtil;
import liedge.limatech.blockentity.base.SidedAccessBlockEntityType;
import liedge.limatech.blockentity.base.SidedAccessRules;
import liedge.limatech.entity.LimaTechEntityUtil;
import liedge.limatech.lib.TurretTargetList;
import liedge.limatech.registry.game.LimaTechSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static liedge.limacore.util.LimaMathUtil.toDeg;
import static liedge.limacore.util.LimaMathUtil.vec2Length;

public abstract class BaseTurretBlockEntity extends SidedItemEnergyMachineBlockEntity implements OwnableBlockEntity
{
    private static final Set<RelativeHorizontalSide> VALID_SIDES = ImmutableSet.copyOf(EnumSet.of(RelativeHorizontalSide.BOTTOM, RelativeHorizontalSide.FRONT, RelativeHorizontalSide.REAR, RelativeHorizontalSide.LEFT, RelativeHorizontalSide.RIGHT));
    public static final SidedAccessRules ITEM_RULES = SidedAccessRules.builder().setValidSides(VALID_SIDES).setValidIOStates(IOAccessSets.INPUT_XOR_OUTPUT_OR_DISABLED).setDefaultIOState(IOAccess.OUTPUT_ONLY).build();
    public static final SidedAccessRules ENERGY_RULES = SidedAccessRules.builder().setValidSides(VALID_SIDES).setValidIOStates(IOAccessSets.INPUT_ONLY_OR_DISABLED).setDefaultIOState(IOAccess.INPUT_ONLY).build();

    // General properties
    private final LimaBlockEntityEnergyStorage energyStorage;
    protected final Queue<Entity> targetQueue = new ArrayDeque<>();
    private final Vec3 projectileStart;
    private final AABB targetArea;
    private @Nullable UUID ownerUUID;
    protected @Nullable Entity currentTarget;
    private LimaDataWatcher<IntList> targetsWatcher;
    private LimaDataWatcher<Boolean> activeWatcher;
    private boolean turretCharging;
    private boolean turretFiring;
    protected int ticker;

    // Client properties
    private final AABB defaultRenderBox;
    private boolean activeClient;
    private int ticker0;
    private float turretYRot0;
    private float turretYRot;
    private float turretXRot0;
    private float turretXRot;
    private double targetDistance;
    private boolean lookingAtTarget;

    protected BaseTurretBlockEntity(SidedAccessBlockEntityType<?> type, BlockPos pos, BlockState state, double startY, double areaXZRadius, double areaYMin, double areaYMax)
    {
        super(type, pos, state, 21);
        this.energyStorage = new LimaBlockEntityEnergyStorage(this);
        this.projectileStart = new Vec3(pos.getX() + 0.5d, pos.getY() + startY, pos.getZ() + 0.5d);
        this.targetArea = new AABB(projectileStart.x - areaXZRadius, projectileStart.y - areaYMin, projectileStart.z - areaXZRadius, projectileStart.x + areaXZRadius, projectileStart.y + areaYMax, projectileStart.z + areaXZRadius);

        this.defaultRenderBox = new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1);
    }

    protected abstract int getEnergyPerTarget();

    protected abstract int getTargetScanTime();

    protected abstract int getMaxTargetsPerScan();

    protected abstract int getFiringSequenceDelay();

    protected abstract boolean isValidTarget(Entity entity);

    protected abstract void serverTargetFiringTick(ServerLevel level, BlockPos pos, BlockState state, @Nullable Player owner, Entity target, TurretTargetList targetList);

    protected Comparator<Entity> targetsComparator()
    {
        return Comparator.comparingDouble(e -> e.distanceToSqr(projectileStart));
    }

    public Vec3 getProjectileStart()
    {
        return projectileStart;
    }

    public AABB getTargetArea()
    {
        return targetArea;
    }

    public AABB getDefaultRenderBox()
    {
        return defaultRenderBox;
    }

    public Collection<Entity> getTargetQueue()
    {
        return targetQueue;
    }

    public void onRemovedFromLevel()
    {
        TurretTargetList targetList = TurretTargetList.getOrDefault(getOwner());
        targetList.removeTarget(currentTarget);
        targetList.removeTargets(targetQueue);
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        collector.register(AutomaticDataWatcher.keepClientsideEntitySynced(() -> currentTarget, entity -> {
            // Don't set the current target on the client if it's already dead when packet arrives.
            if (entity != null && !LimaTechEntityUtil.isEntityAlive(entity)) entity = null;
            this.currentTarget = entity;
        }));

        this.targetsWatcher = ManualDataWatcher.manuallyTrack(LimaCoreNetworkSerializers.INT_LIST, () -> LimaTechEntityUtil.flattenEntityIds(targetQueue), list -> {
            targetQueue.clear();
            list.intStream().mapToObj(LimaCoreClientUtil::getClientEntity).filter(e -> e != null && LimaTechEntityUtil.isEntityAlive(e)).forEach(targetQueue::add);
            this.ticker0 = 0;
            this.ticker = 0; // Reset timers on client targets refresh
        });
        this.activeWatcher = ManualDataWatcher.manuallyTrack(LimaCoreNetworkSerializers.BOOL, () -> energyStorage.getEnergyStored() >= getEnergyPerTarget(), b -> this.activeClient = b);

        collector.register(targetsWatcher);
        collector.register(activeWatcher);
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

    @Override
    public int getBaseEnergyTransferRate()
    {
        return getBaseEnergyCapacity() / 20;
    }

    @Override
    public LimaBlockEntityEnergyStorage getEnergyStorage()
    {
        return energyStorage;
    }

    @Override
    public void onEnergyChanged()
    {
        super.onEnergyChanged();
        activeWatcher.setChanged(true);
    }

    @Override
    public boolean isItemValid(int handlerIndex, int slot, ItemStack stack)
    {
        if (handlerIndex == 0)
        {
            return slot != 0 || LimaItemUtil.hasEnergyCapability(stack);
        }

        return super.isItemValid(handlerIndex, slot, stack);
    }

    @Override
    protected void tickServer(ServerLevel level, BlockPos pos, BlockState state)
    {
        // Tick variables
        Player owner = getOwner();
        TurretTargetList targetList = TurretTargetList.getOrDefault(owner);
        LimaItemHandlerBase inventory = getItemHandler();

        // Fill internal energy buffer from energy item slot
        if (energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored())
        {
            IEnergyStorage itemEnergy = inventory.getStackInSlot(0).getCapability(Capabilities.EnergyStorage.ITEM);
            if (itemEnergy != null)
            {
                LimaEnergyUtil.transferEnergyBetween(itemEnergy, energyStorage, energyStorage.getTransferRate(), false);
            }
        }

        // Try to fill targeting queue if turret firing sequence is not active
        if (!turretCharging && targetQueue.isEmpty() && ticker >= getTargetScanTime())
        {
            // Energy consumption is handled at the target acquisition level
            int energyPerTarget = getEnergyPerTarget();
            int maxTargets = Math.min(getMaxTargetsPerScan(), energyStorage.getEnergyStored() / energyPerTarget);

            // Only acquire targets as energy permits
            if (maxTargets > 0)
            {
                level.getProfiler().push("turretTargetScan");
                List<Entity> foundTargets = level.getEntities(owner, getTargetArea(), e -> isValidTarget(e) && LimaTechEntityUtil.isValidWeaponTarget(owner, e) && !targetList.containsTarget(e))
                        .stream()
                        .sorted(targetsComparator())
                        .distinct()
                        .filter(e -> {
                            if (targetList.addTarget(e)) // Temporarily intern e in target list
                            {
                                // Clip test (heaviest performance hit) is now final filter in stream pipeline, only running as necessary (I think?)
                                if (level.clip(new ClipContext(getProjectileStart(), e.getBoundingBox().getCenter(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, e)).getType() != HitResult.Type.BLOCK) return true;
                            }

                            targetList.removeTarget(e); // Remove e from target list if check failed (leaving it open for other turrets)
                            return false;
                        })
                        .limit(maxTargets).collect(LimaStreamsUtil.toObjectList());
                level.getProfiler().pop();

                if (!foundTargets.isEmpty())
                {
                    LimaEnergyUtil.consumeEnergy(energyStorage, energyPerTarget * foundTargets.size(), true);
                    targetQueue.addAll(foundTargets);
                    level.playSound(null, pos, LimaTechSounds.TURRET_TARGET_FOUND.get(), SoundSource.BLOCKS, 1.5f, Mth.randomBetween(level.random, 0.9f, 1f));
                    targetsWatcher.setChanged(true);
                    turretCharging = true;
                }
            }

            ticker = 0;
        }

        // Charge up turret firing sequence
        if (turretCharging)
        {
            if (!turretFiring)
            {
                if (!targetQueue.isEmpty() && ticker >= getFiringSequenceDelay())
                {
                    turretFiring = true;
                    ticker = 0;
                }
                else if (targetQueue.isEmpty())
                {
                    turretFiring = false;
                    turretCharging = false;
                    ticker = 0;
                    targetsWatcher.setChanged(true);
                }
            }
            else
            {
                // Check if target is not null and alive
                if (currentTarget != null && LimaTechEntityUtil.isEntityAlive(currentTarget))
                {
                    serverTargetFiringTick(level, pos, state, owner, currentTarget, targetList);
                }
                else // Target is null or dead
                {
                    currentTarget = null; // Target might be dead but not null, always clear

                    // Select next target if available
                    if (!targetQueue.isEmpty())
                    {
                        currentTarget = targetQueue.poll();
                    }
                    else // End firing sequence and mark target list empty
                    {
                        turretCharging = false;
                        turretFiring = false;
                        targetsWatcher.setChanged(true);
                    }

                    ticker = 0;
                }
            }
        }

        ticker++;
    }

    @Override
    protected void tickClient(Level level, BlockPos pos, BlockState state)
    {
        // Prune stale entities every 5 ticks
        if (!targetQueue.isEmpty() && ticker % 5 == 0)
        {
            targetQueue.removeIf(e -> !LimaTechEntityUtil.isEntityAlive(e));
        }

        // Choose target to aim at
        Entity target;
        if (currentTarget == null && !targetQueue.isEmpty())
        {
            // Starts aiming at first target if queue has targets but no current target has been received from server
            target = targetQueue.peek();
        }
        else
        {
            target = currentTarget;
        }

        // Aim the turret
        if (target != null && LimaTechEntityUtil.isEntityAlive(target))
        {
            Vec3 start = getProjectileStart();
            Vec3 end = target.getBoundingBox().getCenter();

            targetDistance = start.distanceTo(end);

            double dx = end.x - start.x;
            double dy = end.y - start.y;
            double dz = end.z - start.z;

            turretYRot0 = turretYRot;
            float nextYRot = toDeg(Mth.atan2(dz, dx)) + 90f;
            if (!lookingAtTarget) lookingAtTarget = Mth.degreesDifferenceAbs(turretYRot, nextYRot) <= 10f;
            turretYRot = Mth.approachDegrees(turretYRot, nextYRot, 20f);

            turretXRot0 = turretXRot;
            turretXRot = Mth.approachDegrees(turretXRot, toDeg(Mth.atan2(dy, vec2Length(dx, dz))), 10f);

            ticker0 = ticker;
            ticker++;
        }
        else
        {
            currentTarget = null; // Clear target if not null but dead
            targetDistance = 0d;
            lookingAtTarget = false;

            if (activeClient)
            {
                turretXRot0 = turretXRot;
                turretXRot = Mth.approachDegrees(turretXRot, 0, 5f);
                turretYRot0 = turretYRot;
                turretYRot = (turretYRot - 2) % 360;
            }
            else
            {
                turretXRot0 = turretXRot;
                turretXRot = Mth.approachDegrees(turretXRot, -30, 10f);
                turretYRot0 = turretYRot;

                float angle = switch (getFacing())
                {
                    case SOUTH -> 0f;
                    case EAST -> -90f;
                    case WEST -> 90f;
                    default -> 180f;
                };

                turretYRot = Mth.approachDegrees(turretYRot, angle, 15f);
            }
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput)
    {
        super.applyImplicitComponents(componentInput);
        setOwnerUUID(componentInput.get(LimaCoreDataComponents.OWNER));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components)
    {
        super.collectImplicitComponents(components);
        components.set(LimaCoreDataComponents.OWNER, getOwnerUUID());
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag)
    {
        super.removeComponentsFromTag(tag);
        tag.remove(LimaCommonConstants.KEY_OWNER);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);
        loadOwnerID(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
        saveOwnerID(tag);
    }

    // Client functions
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
        return -Mth.rotLerp(partialTick, turretYRot0, turretYRot);
    }

    public float lerpXRot(float partialTick)
    {
        return Mth.rotLerp(partialTick, turretXRot0, turretXRot);
    }

    public float lerpTicker(float partialTick, float maxTicks)
    {
        return Math.min(Mth.lerp(partialTick, ticker0, ticker) / maxTicks, 1f);
    }
}