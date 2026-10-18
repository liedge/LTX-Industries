package liedge.ltxindustries.entity;

import liedge.limacore.LimaCommonConstants;
import liedge.limacore.client.particle.ColorParticleOptions;
import liedge.limacore.client.particle.ColorSizeParticleOptions;
import liedge.limacore.util.LimaNetworkUtil;
import liedge.ltxindustries.registry.bootstrap.LTXIDamageTypes;
import liedge.ltxindustries.registry.game.LTXIEntities;
import liedge.ltxindustries.registry.game.LTXIParticles;
import liedge.ltxindustries.registry.game.LTXISounds;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import org.jspecify.annotations.Nullable;

import java.util.Comparator;

public class HomingBomblet extends LTXIProjectileEntity implements IEntityWithComplexSpawn
{
    private static final EntityDataAccessor<Boolean> DATA_PRIMED = SynchedEntityData.defineId(HomingBomblet.class, EntityDataSerializers.BOOLEAN);

    private float power;
    private int color = -1;
    private int fuse;

    private float oSpin;
    private float spin;

    public HomingBomblet(EntityType<?> type, Level level)
    {
        super(type, level);
    }

    public HomingBomblet(Level level, UpgradesAwareEntity parent, float power)
    {
        this(LTXIEntities.HOMING_BOMBLET.get(), level);
        this.power = power;
        this.color = parent.getColor();
        setOwner(parent.getOwner());
        setWeaponItem(parent.getWeaponItem().copy());
    }

    @Override
    public int getColor()
    {
        return color;
    }

    @Override
    protected int getLifetime()
    {
        return 300;
    }

    private boolean isPrimed()
    {
        return entityData.get(DATA_PRIMED);
    }

    @Override
    public void setTarget(@Nullable LivingEntity target)
    {
        super.setTarget(target);

        if (target != null)
        {
            this.fuse = random.nextIntBetweenInclusive(5, 15);
        }
    }

    @Override
    protected CollisionResult onCollision(ServerLevel level, @Nullable LivingEntity owner, HitResult hitResult, Vec3 hitLocation)
    {
        if (hitResult instanceof EntityHitResult ehr && ehr.getEntity() == getTarget())
        {
            hurtEntity(level, LTXIDamageTypes.EXPLOSIVE, ehr.getEntity(), owner, power);
            LimaNetworkUtil.sendAlwaysVisibleParticle(level, ColorParticleOptions.of(LTXIParticles.COLOR_HALF_SONIC_BOOM, color), hitLocation);
            level.playSound(null, hitLocation.x, hitLocation.y, hitLocation.z, LTXISounds.BOMBLET_EXPLODE, SoundSource.NEUTRAL, 2f, Mth.nextFloat(random, 0.95f, 1f));
            return CollisionResult.DESTROY;
        }
        else if (isPrimed() && hitResult.getType() == HitResult.Type.BLOCK)
        {
            return CollisionResult.DESTROY;
        }

        return CollisionResult.NO_OP;
    }

    @Override
    protected void tickServer(ServerLevel level)
    {
        super.tickServer(level);

        if (fuse > 0)
        {
            fuse--;
        }
        else
        {
            LivingEntity target = getTarget();
            if (target != null)
            {
                if (!isPrimed())
                {
                    entityData.set(DATA_PRIMED, true);
                    level.playSound(null, this, LTXISounds.BOMBLET_PRIME.get(), SoundSource.NEUTRAL, 2f, Mth.nextFloat(random, 0.95f, 1f));
                }

                aimAtEntity(target, 0.7d);
            }
            else if (!isPrimed() && tickCount % 20 == 0)
            {
                getEntities(level, getBoundingBox().inflate(8), getOwner(), null).stream()
                        .filter(e -> e instanceof LivingEntity).map(e -> (LivingEntity) e)
                        .min(Comparator.comparingDouble(e -> e.distanceToSqr(this)))
                        .ifPresent(this::setTarget);
            }
        }
    }

    @Override
    protected void tickClient(Level level)
    {
        oSpin = spin;
        spin = (spin + 45) % 360f;

        if (isPrimed())
        {
            ParticleOptions particle = ColorSizeParticleOptions.of(LTXIParticles.COLOR_GLITTER, color, 0.325f);
            level.addParticle(particle, getX(), getY(0.5d), getZ(), 0, 0, 0);
        }
    }

    @Override
    protected double getDefaultGravity()
    {
        return isPrimed() && getTarget() == null ? 0.0625d : 0d;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder)
    {
        builder.define(DATA_PRIMED, false);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input)
    {
        super.readAdditionalSaveData(input);
        this.power = input.getFloatOr(LimaCommonConstants.KEY_POWER, 0f);
        this.color = input.getIntOr(LimaCommonConstants.KEY_COLOR, -1);
        this.fuse = input.getShortOr("fuse", (short) 0);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output)
    {
        super.addAdditionalSaveData(output);
        output.putFloat(LimaCommonConstants.KEY_POWER, power);
        output.putInt(LimaCommonConstants.KEY_COLOR, color);
        output.putShort("fuse", (short) fuse);
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf net)
    {
        net.writeVarInt(color);
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf net)
    {
        this.color = net.readVarInt();
    }

    public float lerpSpin(float partialTick)
    {
        return Mth.rotLerp(partialTick, oSpin, spin);
    }
}