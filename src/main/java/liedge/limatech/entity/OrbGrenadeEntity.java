package liedge.limatech.entity;

import liedge.limacore.util.LimaNetworkUtil;
import liedge.limatech.client.particle.GrenadeElementParticleOptions;
import liedge.limatech.lib.weapons.OrbGrenadeElement;
import liedge.limatech.lib.weapons.WeaponDamageSource;
import liedge.limatech.registry.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

import java.util.List;

import static liedge.limatech.registry.LimaTechMobEffects.CORROSIVE;

public class OrbGrenadeEntity extends LimaTechProjectile implements IEntityWithComplexSpawn
{
    private OrbGrenadeElement element = OrbGrenadeElement.EXPLOSIVE;
    private float spin0;
    private float spin;

    public OrbGrenadeEntity(EntityType<?> type, Level level)
    {
        super(type, level);
    }

    public OrbGrenadeEntity(Level level, OrbGrenadeElement grenadeElement)
    {
        this(LimaTechEntities.ORB_GRENADE.get(), level);
        this.element = grenadeElement;
    }

    public OrbGrenadeElement getGrenadeElement()
    {
        return element;
    }

    private void setGrenadeElement(OrbGrenadeElement element)
    {
        this.element = element;
    }

    public float lerpSpinAnimation(float partialTick)
    {
        return Mth.rotLerp(partialTick, spin0, spin);
    }

    @SuppressWarnings("deprecation")
    private boolean isInRainOrWater(Level level)
    {
        return BlockPos.betweenClosedStream(getBoundingBox()).filter(level::hasChunkAt).anyMatch(pos -> level.getBlockState(pos).getFluidState().is(FluidTags.WATER) || level.isRainingAt(pos));
    }

    private double getBlastRadius(Level level)
    {
        return switch (getGrenadeElement())
        {
            case EXPLOSIVE -> 10.5d;
            case FLAME -> 8.5d;
            case FREEZE -> 9.5d;
            case ELECTRIC -> isInRainOrWater(level) ? 16d : 7.5d;
            case ACID -> 5.5d;
        };
    }

    private float getBlastDamage(Entity hitEntity)
    {
        return switch (getGrenadeElement())
        {
            case EXPLOSIVE -> 20f;
            case FLAME -> 9.5f;
            case FREEZE -> 12f;
            case ELECTRIC ->
            {
                boolean bonus = hitEntity.isInWaterOrRain() || hitEntity.getType().is(EntityTypeTags.AQUATIC);
                yield  bonus ? 34f : 12f;
            }
            case ACID -> 15f;
        };
    }

    @Override
    public int getLifetime()
    {
        return 200;
    }

    @Override
    protected float getProjectileGravity()
    {
        return 0.04f;
    }

    @Override
    protected void onProjectileHit(Level level, HitResult hitResult, Vec3 hitLocation)
    {
        LivingEntity owner = getOwner();

        double radius = getBlastRadius(level);
        List<Entity> hits = level.getEntities(this, AABB.ofSize(hitLocation, radius, radius, radius), e -> canHitEntity(owner, e));

        hits.forEach(e -> {
            e.hurt(WeaponDamageSource.projectileEntityDamage(level, this, owner, LimaTechDamageTypes.ROCKET_TURRET, LimaTechItems.GRENADE_LAUNCHER.get()), 50f);

            if (element == OrbGrenadeElement.ACID && e instanceof LivingEntity livingEntity)
            {
                livingEntity.addEffect(new MobEffectInstance(CORROSIVE, 300, 2), owner);
            }
        });

        level.gameEvent(owner, GameEvent.EXPLODE, hitLocation);
        level.playSound(null, hitLocation.x, hitLocation.y, hitLocation.z, LimaTechSounds.GRENADE_SOUNDS.get(element).get(), SoundSource.PLAYERS, 2.5f, 0.9f);
        LimaNetworkUtil.spawnAlwaysVisibleParticle(level, new GrenadeElementParticleOptions(LimaTechParticles.GRENADE_EXPLOSION, element), hitLocation);
        discard();
    }

    @Override
    protected void tickProjectile(Level level, boolean isClientSide)
    {
        if (isClientSide)
        {
            float speed = (float) getDeltaMovement().length();
            spin0 = spin;
            spin = (spin - (60 * speed)) % 360;
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        setGrenadeElement(OrbGrenadeElement.CODEC.byName(tag.getString("element")));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        tag.putString("element", element.getSerializedName());
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer)
    {
        OrbGrenadeElement.STREAM_CODEC.encode(buffer, element);
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf additionalData)
    {
        setGrenadeElement(OrbGrenadeElement.STREAM_CODEC.decode(additionalData));
    }
}