package liedge.ltxindustries.entity;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import liedge.limacore.LimaCommonConstants;
import liedge.limacore.util.LimaBlockUtil;
import liedge.limacore.util.LimaNbtUtil;
import liedge.ltxindustries.item.UpgradableEquipmentItem;
import liedge.ltxindustries.lib.upgrades.UpgradesContainerBase;
import liedge.ltxindustries.lib.upgrades.machine.MachineUpgrades;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static liedge.ltxindustries.LTXIConstants.KEY_UPGRADES_CONTAINER;

public abstract class LimaTraceableEntity extends Entity implements TraceableEntity
{
    private static final String LAUNCHER_ITEM_KEY = "launcher";

    private final ObjectSet<FluidType> touchingFluidTypes = new ObjectOpenHashSet<>();

    private @Nullable UUID ownerUUID;
    private @Nullable LivingEntity owner;
    private ItemStack launcherItem = ItemStack.EMPTY;

    protected LimaTraceableEntity(EntityType<?> entityType, Level level)
    {
        super(entityType, level);
    }

    protected ItemStack getLauncherItem()
    {
        return launcherItem;
    }

    public void setLauncherItem(ItemStack launcherItem)
    {
        this.launcherItem = launcherItem;
    }

    public UpgradesContainerBase<?, ?> getUpgrades()
    {
        return UpgradableEquipmentItem.getEquipmentUpgradesFromStack(getLauncherItem());
    }

    @Override
    public @Nullable LivingEntity getOwner()
    {
        if (owner != null && !owner.isRemoved())
        {
            return owner;
        }
        else if (ownerUUID != null && level() instanceof ServerLevel serverLevel)
        {
            Entity entity = serverLevel.getEntity(ownerUUID);
            if (entity instanceof LivingEntity) owner = (LivingEntity) entity;
            return owner;
        }
        else
        {
            return null;
        }
    }

    public void setOwner(@Nullable LivingEntity owner)
    {
        if (owner != null)
        {
            this.ownerUUID = owner.getUUID();
            this.owner = owner;
        }
    }

    // AOE Helpers
    protected List<Entity> getEntitiesInAOE(Level level, AABB aabb, @Nullable LivingEntity owner, @Nullable Entity directHit)
    {
        return level.getEntities(this, aabb, e -> LTXIEntityUtil.checkWeaponTargetValidity(owner, e, getUpgrades()) && !Objects.equals(directHit, e));
    }

    protected List<Entity> getEntitiesInAOE(Level level, Vec3 hitLocation, double radius, @Nullable LivingEntity owner, @Nullable Entity directHit)
    {
        radius *= 2;
        return getEntitiesInAOE(level, AABB.ofSize(hitLocation, radius, radius, radius), owner, directHit);
    }

    // Serialization
    @Override
    protected void readAdditionalSaveData(CompoundTag tag)
    {
        tickCount = tag.getInt("age");
        ownerUUID = LimaNbtUtil.getOptionalUUID(tag, LimaCommonConstants.KEY_OWNER);
        launcherItem = ItemStack.parseOptional(registryAccess(), tag.getCompound(LAUNCHER_ITEM_KEY));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag)
    {
        tag.putInt("age", tickCount);
        LimaNbtUtil.putOptionalUUID(tag, LimaCommonConstants.KEY_OWNER, ownerUUID);
        if (!launcherItem.isEmpty()) tag.put(LAUNCHER_ITEM_KEY, launcherItem.save(registryAccess()));
    }

    protected RegistryOps<Tag> createRegistryOps()
    {
        return RegistryOps.create(NbtOps.INSTANCE, registryAccess());
    }

    protected MachineUpgrades readMachineUpgrades(CompoundTag tag)
    {
        return LimaNbtUtil.tryDecode(MachineUpgrades.CODEC, createRegistryOps(), tag, KEY_UPGRADES_CONTAINER, MachineUpgrades.EMPTY);
    }

    protected void writeMachineUpgrades(MachineUpgrades upgrades, CompoundTag tag)
    {
        LimaNbtUtil.tryEncodeTo(MachineUpgrades.CODEC, createRegistryOps(), upgrades, tag, KEY_UPGRADES_CONTAINER);
    }

    // Reduced fluid physics helpers
    @Override
    public boolean isInWater()
    {
        return touchingFluidTypes.contains(NeoForgeMod.WATER_TYPE.value()) || super.isInWater();
    }

    /**
     * Highly reduced version of {@link #updateFluidHeightAndDoFluidPushing()}. Updates fluid heights only
     * with no physics processing. Suitable for projectiles and other entities affected only by the presence of fluids.
     * @param level Level object of this entity
     */
    protected void updateFluidHeightOnly(Level level)
    {
        forgeFluidTypeHeight.clear();

        AABB bb = getBoundingBox().inflate(0.001d);

        LimaBlockUtil.betweenClosedStreamSafeCeil(level, bb).forEach(pos -> {
            FluidState fluidState = level.getFluidState(pos);
            FluidType fluidType = fluidState.getFluidType();

            if (!fluidType.isAir())
            {
                float stateHeight = pos.getY() + fluidState.getHeight(level, pos);
                float actualFluidHeight = Math.max(0, stateHeight - (float) bb.minY);
                setFluidTypeHeight(fluidType, actualFluidHeight);
            }
        });

        ObjectSet<FluidType> previouslyTouchedFluids = new ObjectOpenHashSet<>(touchingFluidTypes);
        touchingFluidTypes.clear();

        forgeFluidTypeHeight.object2DoubleEntrySet().stream().filter(entry -> entry.getDoubleValue() > 0).map(Map.Entry::getKey).forEach(fluidType -> {
            if (!previouslyTouchedFluids.remove(fluidType))
            {
                onEnteredFluidType(fluidType);
            }
            touchingFluidTypes.add(fluidType);
        });

        previouslyTouchedFluids.forEach(fluidType -> {
            if (getFluidTypeHeight(fluidType) == 0)
            {
                onExitFluidType(fluidType);
            }
        });
    }

    protected void onEnteredFluidType(FluidType fluidType)
    {
        if (fluidType == NeoForgeMod.WATER_TYPE.value() && !firstTick) doWaterSplashEffect();
    }

    protected void onExitFluidType(FluidType fluidType) {}
}