package liedge.ltxindustries.entity;

import liedge.limacore.client.particle.ColorParticleOptions;
import liedge.limacore.client.particle.ColorSizeParticleOptions;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.limacore.util.LimaNetworkUtil;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.registry.game.LTXIParticles;
import liedge.ltxindustries.registry.game.LTXISounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public abstract class BaseRocketEntity extends HomingProjectileEntity
{
    protected BaseRocketEntity(EntityType<?> type, Level level)
    {
        super(type, level);
    }

    protected abstract void hurtTarget(ServerLevel level, Entity targetEntity, @Nullable LivingEntity owner, Vec3 hitLocation, boolean isDirectHit);

    @Override
    public int getLifetime()
    {
        return 1000;
    }

    @Override
    protected CollisionResult onCollision(ServerLevel level, @Nullable LivingEntity owner, HitResult hitResult, Vec3 hitLocation)
    {
        Entity directHit = hitResult.getType() == HitResult.Type.ENTITY ? ((EntityHitResult) hitResult).getEntity() : null;

        if (directHit != null) hurtTarget(level, directHit, owner, hitLocation, true);

        getEntitiesInAOE(level, hitLocation, 3d, owner, directHit).forEach(aoeHit -> hurtTarget(level, aoeHit, owner, hitLocation, false));
        level.playSound(null, hitLocation.x, hitLocation.y, hitLocation.z, LTXISounds.ROCKET_EXPLODE.get(), SoundSource.PLAYERS, 4f, 0.9f);
        LimaNetworkUtil.sendParticle(level, new ColorParticleOptions(LTXIParticles.HALF_SONIC_BOOM_EMITTER, LTXIConstants.LIME_GREEN), LimaNetworkUtil.UNLIMITED_PARTICLE_DIST, hitLocation);

        return CollisionResult.DESTROY;
    }

    @Override
    protected void tickClient(Level level)
    {
        Vec3 p = LimaCoreMath.createMotionVector(getXRot(), getYRot(), -0.5625d, 0d);
        double px = getX() + p.x();
        double py = getY(0.5d) + p.y();
        double pz = getZ() + p.z();

        double trailSpeed = Mth.clamp(getDeltaMovement().length() - 0.7d, -0.7d, 0.1d);
        Vec3 v = LimaCoreMath.createMotionVector(getXRot(), getYRot(), trailSpeed, 5d);
        level.addAlwaysVisibleParticle(new ColorSizeParticleOptions(LTXIParticles.COLOR_GLITTER, LTXIConstants.LIME_GREEN, 1.5f), true, px, py, pz, v.x(), v.y(), v.z());
    }
}