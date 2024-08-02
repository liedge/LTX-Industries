package liedge.limatech.entity;

import liedge.limacore.util.LimaNetworkUtil;
import liedge.limatech.registry.LimaTechDamageTypes;
import liedge.limatech.registry.LimaTechEntities;
import liedge.limatech.registry.LimaTechParticles;
import liedge.limatech.registry.LimaTechSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class MissileEntity extends AutoTrackingProjectile
{
    public MissileEntity(EntityType<?> type, Level level)
    {
        super(type, level);
    }

    public MissileEntity(Level level)
    {
        this(LimaTechEntities.MISSILE.get(), level);
    }

    @Override
    public int getLifetime()
    {
        return 1000;
    }

    @Override
    protected void onProjectileHit(Level level, HitResult hitResult, Vec3 hitLocation)
    {
        LivingEntity owner = getOwner();
        AABB aabb = AABB.ofSize(hitLocation, 5, 5, 5);

        level.getEntities(this, aabb, e -> canHitEntity(owner, e)).forEach(e -> e.hurt(level.damageSources().source(LimaTechDamageTypes.ROCKET_TURRET, this, owner), 100f));
        level.playSound(null, hitLocation.x, hitLocation.y, hitLocation.z, LimaTechSounds.MISSILE_EXPLODE.get(), SoundSource.PLAYERS, 4f, 0.9f);
        LimaNetworkUtil.spawnAlwaysVisibleParticle(level, LimaTechParticles.HALF_SONIC_BOOM_EMITTER.get(), hitLocation);
        discard();
    }

    @Override
    protected void tickProjectile(Level level, boolean isClientSide)
    {
        super.tickProjectile(level, isClientSide);

        if (isClientSide)
        {
            double dx = getX() + (random.nextDouble() - random.nextDouble()) * 0.35d;
            double dz = getZ() + (random.nextDouble() - random.nextDouble()) * 0.35d;
            Vec3 delta = getDeltaMovement();
            level.addAlwaysVisibleParticle(LimaTechParticles.MISSILE_TRAIL.get(), dx, getY(0.5d), dz, -delta.x * 0.5d, -delta.y * 0.5d, -delta.z * 0.5d);
        }
    }
}