package liedge.limatech.client.particle;

import liedge.limacore.util.LimaMathUtil;
import liedge.limatech.lib.weapons.OrbGrenadeElement;
import liedge.limatech.registry.LimaTechParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GrenadeExplosionParticle extends NoRenderParticle
{
    private final OrbGrenadeElement grenadeElement;

    public GrenadeExplosionParticle(GrenadeElementParticleOptions options, ClientLevel level, double x, double y, double z, double dx, double dy, double dz)
    {
        super(level, x, y, z);
        this.grenadeElement = options.element();
    }

    @Override
    public void tick()
    {
        switch (grenadeElement)
        {
            case EXPLOSIVE -> level.addParticle(LimaTechParticles.HALF_SONIC_BOOM_EMITTER.get(), true, x, y, z, 0, 0, 0);
            case FLAME -> particleBall(LimaTechParticles.CAMP_FLAME.get(), 0.25d, 1);
            case FREEZE -> freezeExplosion();
            case ELECTRIC -> electricExplosion();
            case ACID -> particleBall(LimaTechParticles.ACID_FALL.get(), 0.755d, 2);
        }

        remove();
    }

    @SuppressWarnings("deprecation")
    private boolean isNotAirAndHasAirAbove(BlockPos pos)
    {
        if (level.hasChunkAt(pos))
        {
            return !level.getBlockState(pos).isAir() && level.getBlockState(pos.above()).isAir();
        }
        else
        {
            return false;
        }
    }

    private void freezeExplosion()
    {
        particleBall(ParticleTypes.SNOWFLAKE, 0.325d, 1);

        BlockPos.betweenClosedStream(AABB.ofSize(getPos(), 3, 3, 3)).filter(this::isNotAirAndHasAirAbove).forEach(blockPos -> {
            VoxelShape shape = level.getBlockState(blockPos).getCollisionShape(level, blockPos);

            double xOffset = random.nextDouble();
            double zOffset = random.nextDouble();

            double px = blockPos.getX() + xOffset;
            double py = blockPos.getY() + shape.toAabbs().stream().filter(box -> xOffset >= box.minX && xOffset <= box.maxX && zOffset >= box.minZ && zOffset <= box.maxZ).mapToDouble(box -> box.maxY).max().orElse(0d);
            double pz = blockPos.getZ() + zOffset;

            level.addParticle(LimaTechParticles.GROUND_ICICLE.get(), false, px, py, pz, 0, 0, 0);
        });
    }

    private void electricExplosion()
    {
        particleBall(LimaTechParticles.MINI_ELECTRIC_SPARK.get(), 0.6, 1);

        for (int i = 0; i < 7; i++)
        {
            double ax = x + (random.nextDouble() - random.nextDouble()) * 2.75d;
            double ay = y + (random.nextDouble() - random.nextDouble()) * 2.75d;
            double az = z + (random.nextDouble() - random.nextDouble()) * 2.75d;

            level.addParticle(LimaTechParticles.ELECTRIC_ARC.get(), true, ax, ay, az, 0, 0, 0);
        }
    }

    private void particleBall(ParticleOptions particle, double speed, int size)
    {
        for (int i = -size; i <= size; i++)
        {
            for (int j = -size; j <= size; j++)
            {
                for (int k = -size; k <= size; k++)
                {
                    double dx = (double) j + (random.nextDouble() - random.nextDouble()) * 0.5d;
                    double dy = (double) i + (random.nextDouble() - random.nextDouble()) * 0.5d;
                    double dz = (double) k + (random.nextDouble() - random.nextDouble()) * 0.5d;
                    double d1 = LimaMathUtil.vec3Length(dx, dy, dz) / speed + random.nextGaussian() * 0.05d;

                    level.addAlwaysVisibleParticle(particle, true, x, y, z, dx / d1, Math.min(dy / d1, 0.5d), dz / d1);
                }
            }
        }
    }
}