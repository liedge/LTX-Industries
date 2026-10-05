package liedge.ltxindustries.entity;

import liedge.limacore.client.particle.ColorParticleOptions;
import liedge.limacore.client.particle.ColorSizeParticleOptions;
import liedge.limacore.util.LimaNetworkUtil;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.registry.bootstrap.LTXIDamageTypes;
import liedge.ltxindustries.registry.game.LTXIEntities;
import liedge.ltxindustries.registry.game.LTXIParticles;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ClusterMunition extends LTXIProjectileEntity
{
    private float spin0;
    private float spin;

    public ClusterMunition(EntityType<?> type, Level level)
    {
        super(type, level);
    }

    public ClusterMunition(Level level)
    {
        this(LTXIEntities.CLUSTER_MUNITION.get(), level);
    }

    @Override
    protected CollisionResult onCollision(ServerLevel level, @Nullable LivingEntity owner, HitResult hitResult, Vec3 hitLocation)
    {
        getEntitiesInAOE(level, hitLocation, 1.5d, owner, null).forEach(hit -> {
            hit.hurtServer(level, level.damageSources().source(LTXIDamageTypes.LIGHTFRAG), 4f);
        });

        level.playSound(this, hitLocation.x, hitLocation.y, hitLocation.z, SoundEvents.FIREWORK_ROCKET_LARGE_BLAST, SoundSource.NEUTRAL, 2f, Mth.nextFloat(level.getRandom(), 0.9f, 1f));

        LimaNetworkUtil.sendParticle(level, ColorSizeParticleOptions.of(LTXIParticles.COLOR_FLASH, LTXIConstants.LIME_GREEN, 3f), LimaNetworkUtil.UNLIMITED_PARTICLE_DIST, hitLocation);
        LimaNetworkUtil.sendParticle(level, ColorParticleOptions.of(LTXIParticles.COLOR_HALF_SONIC_BOOM, LTXIConstants.LIME_GREEN), LimaNetworkUtil.UNLIMITED_PARTICLE_DIST, hitLocation);

        return CollisionResult.DESTROY;
    }

    @Override
    protected double getDefaultGravity()
    {
        return 0.03125d;
    }

    @Override
    protected void tickClient(Level level)
    {
        spin0 = spin;
        spin = (spin + 25) % 360f;
    }

    public float lerpMotion(float partialTick)
    {
        return Mth.rotLerp(partialTick, spin0, spin);
    }
}