package liedge.ltxindustries.client.particle;

import liedge.limacore.client.particle.ColorParticleOptions;
import liedge.limacore.client.particle.ColorSizeParticleOptions;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.ltxindustries.lib.weapons.GrenadeType;
import liedge.ltxindustries.registry.game.LTXIParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HanabiExplosionParticle extends NoRenderParticle
{
    private final GrenadeType element;
    private final int color;
    private final float size;

    private HanabiExplosionParticle(ClientLevel level, double x, double y, double z, HanabiParticleOptions data)
    {
        super(level, x, y, z);
        this.element = data.element();
        this.color = data.color();
        this.size = data.size();
    }

    @Override
    public void tick()
    {
        level.addAlwaysVisibleParticle(ColorSizeParticleOptions.of(LTXIParticles.COLOR_FLASH, color, size), true, x, y, z, 0, 0, 0);

        switch (element)
        {
            case EXPLOSIVE -> particleBall(ColorParticleOptions.of(LTXIParticles.COLOR_TWINKLE_SPARK, color), 0.775d, 3);
            case CRYO -> cryoSnowflakeExplosion();
            case ELECTRIC -> particleBall(ColorParticleOptions.of(LTXIParticles.COLOR_TWINKLE_SPARK, color), 0.5d, 1);
            case ACID -> particleBall(LTXIParticles.ACID_FALL.get(), 0.755d, 3);
            case GLOOM_GAS -> level.addAlwaysVisibleParticle(LTXIParticles.GLOOM_BURST_EMITTER.get(), true, x, y, z, 0, 0, 0);
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
        particleBall(LTXIParticles.SNOWFLAKE.get(), 1.275d, 3);

        BlockPos.betweenClosedStream(AABB.ofSize(getPos(), 9d, 9d, 9d)).filter(this::isNotAirAndHasAirAbove).forEach(blockPos -> {
            VoxelShape shape = level.getBlockState(blockPos).getCollisionShape(level, blockPos);

            double xOffset = random.nextDouble();
            double zOffset = random.nextDouble();

            double px = blockPos.getX() + xOffset;
            double py = blockPos.getY() + shape.toAabbs().stream().filter(box -> xOffset >= box.minX && xOffset <= box.maxX && zOffset >= box.minZ && zOffset <= box.maxZ).mapToDouble(box -> box.maxY).max().orElse(0d);
            double pz = blockPos.getZ() + zOffset;

            level.addParticle(LTXIParticles.GROUND_ICICLE.get(), px, py, pz, 0, 0, 0);
        });
    }

    private void particleBall(ParticleOptions particle, double speed, int steps)
    {
        for (int yStep = -steps; yStep <= steps; yStep++)
        {
            for (int xStep = -steps; xStep <= steps; xStep++)
            {
                for (int zStep = -steps; zStep <= steps; zStep++)
                {
                    double dx = xStep + (random.nextDouble() - random.nextDouble()) * 0.5d;
                    double dy = yStep + (random.nextDouble() - random.nextDouble()) * 0.5d;
                    double dz = zStep + (random.nextDouble() - random.nextDouble()) * 0.5d;
                    double dLen = LimaCoreMath.vec3Length(dx, dy, dz) / speed + random.nextGaussian() * 0.05d;

                    level.addParticle(particle, true, false, x, y, z, dx / dLen, dy / dLen, dz / dLen);
                    if (yStep != -steps && yStep != steps && xStep != -steps && xStep != steps) zStep += steps * 2 - 1;
                }
            }
        }
    }

    public static final class Provider implements ParticleProvider<HanabiParticleOptions>
    {
        @Override
        public Particle createParticle(HanabiParticleOptions data, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource random)
        {
            return new HanabiExplosionParticle(level, x, y, z, data);
        }
    }
}