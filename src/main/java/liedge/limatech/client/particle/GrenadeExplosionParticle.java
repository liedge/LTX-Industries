package liedge.limatech.client.particle;

import liedge.limacore.client.particle.ColorParticleOptions;
import liedge.limacore.client.particle.ColorSizeParticleOptions;
import liedge.limacore.util.LimaMathUtil;
import liedge.limatech.registry.game.LimaTechParticles;
import liedge.limatech.lib.weapons.GrenadeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GrenadeExplosionParticle extends NoRenderParticle
{
    private final GrenadeType grenadeElement;
    private final double explosionSize;

    public GrenadeExplosionParticle(GrenadeExplosionParticleOptions options, ClientLevel level, double x, double y, double z)
    {
        super(level, x, y, z);
        this.grenadeElement = options.element();
        this.explosionSize = options.explosionSize();
    }

    @Override
    public void tick()
    {
        level.addAlwaysVisibleParticle(new ColorSizeParticleOptions(LimaTechParticles.COLOR_FLASH, grenadeElement.getColor(), (float) explosionSize), true, x, y, z, 0, 0, 0);

        switch (grenadeElement)
        {
            case EXPLOSIVE -> level.addParticle(new ColorParticleOptions(LimaTechParticles.HALF_SONIC_BOOM_EMITTER, grenadeElement.getColor()), true, x, y, z, 0, 0, 0);
            case CRYO -> cryoSnowflakeExplosion();
            case ELECTRIC -> particleBall(LimaTechParticles.MINI_ELECTRIC_SPARK.get(), 0.5d, 1);
            case ACID -> acidExplosion();
            case NEURO -> neuroExplosion();
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

    private void cryoSnowflakeExplosion()
    {
        particleBallManualSpeed(LimaTechParticles.CRYO_SNOWFLAKE.get(), 0.325d, 1);

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

    private void acidExplosion()
    {
        particleBall(LimaTechParticles.ACID_FALL.get(), 0.755d, 2);
    }

    private void neuroExplosion()
    {
        for (int i = 0; i < 16; i++)
        {
            double dx = (random.nextDouble() - random.nextDouble()) * 0.2d;
            double dz = (random.nextDouble() - random.nextDouble()) * 0.2d;
            level.addParticle(LimaTechParticles.NEURO_SMOKE.get(), true, x, y + 0.25d, z, dx, 0.05d, dz);
        }
    }

    private void particleBallManualSpeed(ParticleOptions options, double speed, int size)
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

                    Particle particle = Minecraft.getInstance().particleEngine.createParticle(options, x, y, z, 0, 0, 0);
                    if (particle != null)
                    {
                        particle.setParticleSpeed(dx / d1, Math.min(dy / d1, 0.5d), dz / d1);
                    }
                }
            }
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