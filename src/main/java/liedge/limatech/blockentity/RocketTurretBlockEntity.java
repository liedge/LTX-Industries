package liedge.limatech.blockentity;

import it.unimi.dsi.fastutil.ints.IntList;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.LimaBlockEntity;
import liedge.limacore.blockentity.LimaBlockEntityType;
import liedge.limacore.blockentity.OwnableBlockEntity;
import liedge.limacore.capability.energy.EnergyHolderBlockEntity;
import liedge.limacore.capability.energy.LimaBlockEntityEnergyStorage;
import liedge.limacore.capability.energy.LimaEnergyStorage;
import liedge.limacore.capability.itemhandler.LimaBlockEntityItemHandler;
import liedge.limacore.capability.itemhandler.LimaItemHandlerBase;
import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.inventory.menu.LimaMenuProvider;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.limacore.network.sync.ManualDataWatcher;
import liedge.limacore.registry.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaCollectionsUtil;
import liedge.limacore.util.LimaNbtUtil;
import liedge.limacore.util.LimaStreamsUtil;
import liedge.limatech.LimaTechTags;
import liedge.limatech.entity.BaseRocketEntity;
import liedge.limatech.entity.LimaTechEntityUtil;
import liedge.limatech.lib.TurretTargetList;
import liedge.limatech.lib.upgrades.machine.MachineUpgrades;
import liedge.limatech.registry.LimaTechMenus;
import liedge.limatech.registry.LimaTechSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.RegistryOps;
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
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static liedge.limacore.LimaCommonConstants.KEY_ENERGY_CONTAINER;
import static liedge.limacore.LimaCommonConstants.KEY_ITEM_CONTAINER;
import static liedge.limacore.util.LimaMathUtil.toDeg;
import static liedge.limacore.util.LimaMathUtil.vec2Length;

public class RocketTurretBlockEntity extends LimaBlockEntity implements LimaMenuProvider, EnergyHolderBlockEntity, UpgradableMachineBlockEntity, OwnableBlockEntity
{
    private final LimaBlockEntityItemHandler inventory;
    private final LimaBlockEntityItemHandler upgradeModuleSlot;
    private final LimaBlockEntityEnergyStorage energyStorage;
    private final Vec3 projectileStart;
    private final Queue<Entity> targetQueue = new ArrayDeque<>();
    private final AABB targetArea;
    private LimaDataWatcher<IntList> targetsWatcher;

    private @Nullable UUID ownerUUID;
    private @Nullable Entity currentTarget;
    private int ticker = 0;
    private boolean startedVolley;
    private MachineUpgrades upgrades = MachineUpgrades.EMPTY;

    // Client properties
    private int ticker0 = 0;
    private float turretYRot0;
    private float turretYRot;
    private float turretXRot0;
    private float turretXRot;

    public RocketTurretBlockEntity(LimaBlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.inventory = new LimaBlockEntityItemHandler(this, 21);
        this.upgradeModuleSlot = new LimaBlockEntityItemHandler(this, 1, 1);
        this.energyStorage = new LimaBlockEntityEnergyStorage(this);
        this.projectileStart = new Vec3(pos.getX() + 0.5d, pos.getY() + 1.625d, pos.getZ() + 0.5d);
        Vec3 center = Vec3.atBottomCenterOf(pos);
        this.targetArea = new AABB(center.x - 50d, center.y, center.z - 50d, center.x + 50d, center.y + 50d, center.z + 50d);
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

        collector.register(targetsWatcher);
    }

    @Override
    public int getBaseEnergyCapacity()
    {
        return 200_000; // TODO Un hard code value
    }

    @Override
    public int getBaseEnergyTransferRate()
    {
        return 2000; // TODO Un hard code
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
    }

    @Override
    public IOAccess getEnergyIOForSide(Direction side)
    {
        return IOAccess.INPUT_ONLY;
    }

    @Override
    public LimaItemHandlerBase getItemHandler(int handlerIndex) throws IndexOutOfBoundsException
    {
        return switch (handlerIndex)
        {
            case 0 -> inventory;
            case 1 -> upgradeModuleSlot;
            default -> throw new IndexOutOfBoundsException("Invalid item handler index " + handlerIndex + " accessed.");
        };
    }

    @Override
    public boolean isItemValid(int handlerIndex, int slot, ItemStack stack)
    {
        return true;
    }

    @Override
    public IOAccess getItemHandlerSideIO(Direction side)
    {
        return IOAccess.DISABLED;
    }

    @Override
    public LimaMenuType<?, ?> getMenuType()
    {
        return LimaTechMenus.ROCKET_TURRET.get();
    }

    @Override
    public MachineUpgrades getUpgrades()
    {
        return upgrades;
    }

    @Override
    public void setUpgrades(MachineUpgrades upgrades)
    {
        this.upgrades = upgrades;
    }

    @Override
    protected void tickServer(Level level, BlockPos pos, BlockState state)
    {
        // Get the target list
        Player owner = getOwner();
        TurretTargetList targetList = TurretTargetList.getOrDefault(owner);

        // Fill targeting queue
        if (targetQueue.isEmpty() && ticker >= 100) // Increased scan rate to 5 sec up from 2
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
                    .limit(4).collect(LimaStreamsUtil.toObjectList());
            level.getProfiler().pop();

            if (!foundTargets.isEmpty())
            {
                targetQueue.addAll(foundTargets);
                level.playSound(null, pos, LimaTechSounds.TURRET_TARGET_FOUND.get(), SoundSource.BLOCKS, 1.5f, Mth.randomBetween(level.random, 0.9f, 1f));
                targetsWatcher.setChanged(true);
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
            currentTarget = null;
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
        loadOwnerID(tag);

        inventory.deserializeNBT(registries, tag.getCompound(KEY_ITEM_CONTAINER));
        upgradeModuleSlot.deserializeNBT(registries, tag.getCompound("upgrade_slot"));
        LimaNbtUtil.deserializeInt(energyStorage, registries, tag.get(KEY_ENERGY_CONTAINER));
        upgrades = LimaNbtUtil.lenientDecode(MachineUpgrades.CODEC, RegistryOps.create(NbtOps.INSTANCE, registries), tag, "upgrades");

    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
        saveOwnerID(tag);

        tag.put(KEY_ITEM_CONTAINER, inventory.serializeNBT(registries));
        tag.put("upgrade_slot", upgradeModuleSlot.serializeNBT(registries));
        tag.put(KEY_ENERGY_CONTAINER, energyStorage.serializeNBT(registries));
        tag.put("upgrades", LimaCoreCodecs.lenientEncode(MachineUpgrades.CODEC, RegistryOps.create(NbtOps.INSTANCE, registries), upgrades));
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