package liedge.limatech.client.particle;

import liedge.limacore.client.particle.ColorParticleOptions;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.util.LimaMathUtil;
import liedge.limatech.registry.game.LimaTechParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class RailgunBoltParticle extends NoRenderParticle
{
    public static @Nullable RailgunBoltParticle create(ColorParticleOptions options, ClientLevel level, double x1, double y1, double z1, double x2, double y2, double z2)
    {
        Vec3 start = new Vec3(x1, y1, z1);
        Vec3 end = new Vec3(x2, y2, z2);
        Vec3 path = end.subtract(start);

        double length = path.length();

        return length <= 100 ? new RailgunBoltParticle(level, start, path.normalize(), Mth.ceil(length), options.color()) : null;
    }

    private final Vec3 start;
    private final Vec3 direction;
    private final int beamSegments;
    private final LimaColor color;

    private RailgunBoltParticle(ClientLevel level, Vec3 start, Vec3 direction, int beamSegments, LimaColor color)
    {
        super(level, start.x, start.y, start.z);
        this.start = start;
        this.direction = direction;
        this.beamSegments = beamSegments;
        this.color = color;
    }

    @Override
    public void tick()
    {
        if (age == 0)
        {
            ParticleOptions options = new ColorParticleOptions(LimaTechParticles.COLOR_FULL_SONIC_BOOM, color);
            for (int i = 0; i < beamSegments; i++)
            {
                double px = start.x + direction.x * i;
                double py = start.y + direction.y * i;
                double pz = start.z + direction.z * i;
                level.addAlwaysVisibleParticle(options, true, px, py, pz, 0, 0, 0);
            }
        }
        else if (age >= 10)
        {
            float xRot = LimaMathUtil.getXRot(direction);
            float yRot = LimaMathUtil.getYRot(direction);

            for (int i = 0; i < beamSegments; i++)
            {
                double px = start.x + direction.x * i;
                double py = start.y + direction.y * i;
                double pz = start.z + direction.z * i;

                float xo0 = (random.nextFloat() - random.nextFloat()) * 0.3f;
                float yo0 = (random.nextFloat() - random.nextFloat()) * 0.3f;
                float xo1 = (random.nextFloat() - random.nextFloat()) * 1.1f;
                float yo1 = (random.nextFloat() - random.nextFloat()) * 1.1f;

                Vec3 arcStart = LimaMathUtil.relativePointToRotations(xRot, yRot, xo0, yo0, 0f).add(px, py, pz);
                Vec3 arcEnd = LimaMathUtil.relativePointToRotations(xRot, yRot, xo1, yo1, 1f).add(px, py, pz);

                level.addAlwaysVisibleParticle(new ColorParticleOptions(LimaTechParticles.FIXED_ELECTRIC_BOLT, color), true, arcStart.x, arcStart.y, arcStart.z, arcEnd.x, arcEnd.y, arcEnd.z);
            }

            remove();
            return;
        }

        age++;
    }
}