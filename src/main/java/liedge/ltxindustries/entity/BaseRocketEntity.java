package liedge.ltxindustries.entity;

import liedge.limacore.client.particle.ColorParticleOptions;
import liedge.limacore.client.particle.ColorSizeParticleOptions;
import liedge.limacore.util.LimaNetworkUtil;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.registry.game.LTXIParticles;
import liedge.ltxindustries.registry.game.LTXISounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public abstract class BaseRocketEntity extends AutoTrackingProjectile
{
    protected BaseRocketEntity(EntityType<?> type, Level level)
    {
        super(type, level);
    }

    protected abstract void damageTarget(Level level, @Nullable LivingEntity owner, Entity targetEntity, Vec3 hitLocation, boolean isDirectHit);

    @Override
    public int getLifetime()
    {
        return 1000;
    }

    @Override
    protected void onProjectileHit(Level level, HitResult hitResult, Vec3 hitLocation)
    {
        LivingEntity owner = getOwner();
        Entity directHit = hitResult.getType() == HitResult.Type.ENTITY ? ((EntityHitResult) hitResult).getEntity() : null;

        if (directHit != null) damageTarget(level, owner, directHit, hitLocation, true);

        getEntitiesInAOE(level, hitLocation, 3d, owner, directHit).forEach(aoeHit -> damageTarget(level, owner, aoeHit, hitLocation, false));

        level.playSound(null, hitLocation.x, hitLocation.y, hitLocation.z, LTXISounds.ROCKET_EXPLODE.get(), SoundSource.PLAYERS, 4f, 0.9f);
        LimaNetworkUtil.sendParticle(level, new ColorParticleOptions(LTXIParticles.HALF_SONIC_BOOM_EMITTER, LTXIConstants.LIME_GREEN), LimaNetworkUtil.UNLIMITED_PARTICLE_DIST, hitLocation);
    }

    @Override
    protected void tickProjectile(Level level, boolean isClientSide)
    {
        super.tickProjectile(level, isClientSide);

        if (isClientSide)
        {
            double dx = getX() + (random.nextDouble() - random.nextDouble()) * 0.35d;
            double dz = getZ() + (random.nextDouble() - random.nextDouble()) * 0.35d;
            level.addAlwaysVisibleParticle(new ColorSizeParticleOptions(LTXIParticles.COLOR_GLITTER, LTXIConstants.LIME_GREEN, 1.75f), true, dx, getY(0.5d), dz, 0, 0, 0);
        }
    }
}