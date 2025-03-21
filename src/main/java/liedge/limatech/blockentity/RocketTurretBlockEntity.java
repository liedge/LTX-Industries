package liedge.limatech.blockentity;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.IntList;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.IOAccessSets;
import liedge.limacore.blockentity.OwnableBlockEntity;
import liedge.limacore.blockentity.RelativeHorizontalSide;
import liedge.limacore.capability.energy.LimaBlockEntityEnergyStorage;
import liedge.limacore.capability.energy.LimaEnergyStorage;
import liedge.limacore.capability.energy.LimaEnergyUtil;
import liedge.limacore.capability.itemhandler.LimaItemHandlerBase;
import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.limacore.network.sync.ManualDataWatcher;
import liedge.limacore.registry.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaCollectionsUtil;
import liedge.limacore.util.LimaItemUtil;
import liedge.limacore.util.LimaStreamsUtil;
import liedge.limatech.LimaTechTags;
import liedge.limatech.blockentity.base.SidedAccessRules;
import liedge.limatech.entity.BaseRocketEntity;
import liedge.limatech.entity.LimaTechEntityUtil;
import liedge.limatech.lib.TurretTargetList;
import liedge.limatech.registry.LimaTechBlockEntities;
import liedge.limatech.registry.LimaTechMenus;
import liedge.limatech.registry.LimaTechSounds;
import liedge.limatech.util.config.LimaTechMachinesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
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

public class RocketTurretBlockEntity extends SidedItemEnergyMachineBlockEntity implements OwnableBlockEntity
{
    private static final Set<RelativeHorizontalSide> VALID_SIDES = ImmutableSet.copyOf(EnumSet.of(RelativeHorizontalSide.BOTTOM, RelativeHorizontalSide.FRONT, RelativeHorizontalSide.REAR, RelativeHorizontalSide.LEFT, RelativeHorizontalSide.RIGHT));
    public static final SidedAccessRules ITEM_RULES = SidedAccessRules.builder().setValidSides(VALID_SIDES).setValidIOStates(IOAccessSets.INPUT_XOR_OUTPUT_OR_DISABLED).setDefaultIOState(IOAccess.OUTPUT_ONLY).build();
    public static final SidedAccessRules ENERGY_RULES = SidedAccessRules.builder().setValidSides(VALID_SIDES).setValidIOStates(IOAccessSets.INPUT_ONLY_OR_DISABLED).setDefaultIOState(IOAccess.INPUT_ONLY).build();

    private final LimaBlockEntityEnergyStorage energyStorage;
    private final Vec3 projectileStart;
    private final Queue<Entity> targetQueue = new ArrayDeque<>();
    private final AABB targetArea;
    private LimaDataWatcher<IntList> targetsWatcher;
    private LimaDataWatcher<Boolean> activeWatcher;

    private @Nullable UUID ownerUUID;
    private @Nullable Entity currentTarget;
    private int ticker = 0;
    private boolean startedVolley;

    // Client properties
    private boolean activeClient;
    private int ticker0 = 0;
    private float turretYRot0;
    private float turretYRot;
    private float turretXRot0;
    private float turretXRot;

    public RocketTurretBlockEntity(BlockPos pos, BlockState state)
    {
        super(LimaTechBlockEntities.ROCKET_TURRET.get(), pos, state, 21);
        this.energyStorage = new LimaBlockEntityEnergyStorage(this);
        this.projectileStart = new Vec3(pos.getX() + 0.5d, pos.getY() + 1.625d, pos.getZ() + 0.5d);
        Vec3 center = Vec3.atBottomCenterOf(pos);
        this.targetArea = new AABB(center.x - 50d, center.y - 15d, center.z - 50d, center.x + 50d, center.y + 50d, center.z + 50d);
    }

    public AABB getTargetArea()
    {
        return targetArea;
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

    public int getEnergyPerTarget()
    {
        return LimaTechMachinesConfig.ATMOS_TURRET_ENERGY_PER_TARGET.getAsInt();
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
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        collector.register(AutomaticDataWatcher.keepClientsideEntitySynced(() -> currentTarget, entity -> {
            // Don't set the current target on the client if it's already dead when packet arrives.
            if (entity != null && !LimaTechEntityUtil.isEntityAlive(entity)) entity = null;
            this.currentTarget = entity;
        }));

        this.targetsWatcher = ManualDataWatcher.manuallyTrack(LimaCoreNetworkSerializers.INT_LIST, () -> LimaCollectionsUtil.toIntList(targetQueue.stream().mapToInt(Entity::getId)), list -> {
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
    public int getBaseEnergyCapacity()
    {
        return LimaTechMachinesConfig.ATMOS_TURRET_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyTransferRate()
    {
        return getBaseEnergyCapacity() / 20;
    }

    @Override
    public LimaEnergyStorage getEnergyStorage()
    {
        return energyStorage;
    }

    @Override
    public void onEnergyChanged()
    {
        setChanged();
        activeWatcher.setChanged(true);
    }

    @Override
    public LimaMenuType<?, ?> getMenuType()
    {
        return LimaTechMenus.ROCKET_TURRET.get();
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
    protected void tickServer(Level level, BlockPos pos, BlockState state)
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

        // Try to fill targeting queue
        if (targetQueue.isEmpty() && ticker >= 100) // Increased scan rate to 5 sec up from 2
        {
            // Energy consumption is handled at the target acquisition level
            int energyPerTarget = getEnergyPerTarget();
            int maxTargets = Math.min(4, energyStorage.getEnergyStored() / energyPerTarget);

            // Only acquire targets as energy permits
            if (maxTargets > 0)
            {
                level.getProfiler().push("turretTargetScan");

                List<Entity> foundTargets = level.getEntities(owner, getTargetArea(), e -> e.getType().is(LimaTechTags.EntityTypes.FLYING_TARGETS) && LimaTechEntityUtil.isValidWeaponTarget(owner, e) && !targetList.containsTarget(e))
                        .stream()
                        .sorted(Comparator.comparingDouble(e -> e.distanceToSqr(projectileStart)))
                        .filter(e -> {
                            if (targetList.addTarget(e)) // Temporarily intern e in target list
                            {
                                // Clip test (heaviest performance hit) is now final filter in stream pipeline, only running as necessary (I think?)
                                if (level.clip(new ClipContext(projectileStart, e.getBoundingBox().getCenter(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, e)).getType() != HitResult.Type.BLOCK) return true;
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
                }
            }

            ticker = 0;
        }

        // If rocket volley is still 'charging'
        if (!startedVolley)
        {
            if (!targetQueue.isEmpty() && ticker >= 40)
            {
                startedVolley = true;
                ticker = 0;
            }
        }
        else // If rocket volley is in progress
        {
            // Check if target is not null and alive
            if (currentTarget != null && LimaTechEntityUtil.isEntityAlive(currentTarget))
            {
                if (ticker >= 5) // Fire missiles in quick succession
                {
                    BaseRocketEntity.TurretRocket missile = new BaseRocketEntity.TurretRocket(level, this);
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
                    currentTarget = targetQueue.poll();
                }
                else // Else end the volley and mark target list empty
                {
                    startedVolley = false;
                    targetsWatcher.setChanged(true);
                }

                ticker = 0;
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

        // Chose target to aim at
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
            double dx = target.getX() - projectileStart.x();
            double dy = target.getEyePosition().y() - projectileStart.y();
            double dz = target.getZ() - projectileStart.z();

            turretYRot0 = turretYRot;
            turretXRot0 = turretXRot;
            turretYRot = Mth.approachDegrees(turretYRot, toDeg(Mth.atan2(dz, dx)) + 90f, 20f);
            turretXRot = Mth.approachDegrees(turretXRot, -toDeg(Mth.atan2(dy, vec2Length(dx, dz))), 10f);

            ticker0 = ticker;
            ticker++;
        }
        else
        {
            currentTarget = null; // Clear target if not null but dead

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
                turretXRot = Mth.approachDegrees(turretXRot, 30, 10f);
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
    public void onLoad()
    {
        super.onLoad();
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

    public float lerpIndicators(float partialTick)
    {
        return Math.min(Mth.lerp(partialTick, ticker0, ticker) / 34f, 1f); // Reach 100% indicators shortly before the 40 tick volley timer (34 tick)
    }

    public float lerpYRot(float partialTick)
    {
        return 180f - Mth.rotLerp(partialTick, turretYRot0, turretYRot);
    }

    public float lerpXRot(float partialTick)
    {
        return -Mth.rotLerp(partialTick, turretXRot0, turretXRot);
    }
}