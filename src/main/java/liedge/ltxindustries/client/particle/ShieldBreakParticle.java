package liedge.ltxindustries.client.particle;

import liedge.limacore.client.particle.ColorParticleOptions;
import liedge.limacore.client.particle.ColorSizeParticleOptions;
import liedge.limacore.lib.LimaColor;
import liedge.ltxindustries.registry.game.LTXIParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.world.phys.Vec3;

public class ShieldBreakParticle extends NoRenderParticle
{
    private final LimaColor color;
    private final float size;

    public ShieldBreakParticle(ColorSizeParticleOptions options, ClientLevel level, double x, double y, double z)
    {
        super(level, x, y, z);
        this.color = options.color();
        this.size = options.size();
    }

    @Override
    public void tick()
    {
        level.addAlwaysVisibleParticle(new ColorSizeParticleOptions(LTXIParticles.COLOR_FLASH, color, size), true, x, y, z, 0, 0, 0);

        int arcs = random.nextIntBetweenInclusive(3, 7);

        double r = size / 2d;
        for (int i = 0; i < arcs; i++)
        {
            Vec3 a = arcPoint(r * 0.3d);
            Vec3 b = arcPoint(r);
            level.addAlwaysVisibleParticle(new ColorParticleOptions(LTXIParticles.FIXED_ELECTRIC_BOLT, color), true, a.x, a.y, b.z, b.x, b.y, b.z);
        }

        remove();
    }

    private Vec3 arcPoint(double radius)
    {
        Vec3 v = new Vec3(random.nextGaussian(), random.nextGaussian(), random.nextGaussian()).normalize();
        double r = Math.cbrt(random.nextDouble()) * radius;

        return new Vec3(x + v.x * r, y + v.y * r, z + v.z * r);
    }
}