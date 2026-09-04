package liedge.ltxindustries.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import liedge.limacore.client.particle.ColorParticleOptions;
import liedge.limacore.client.particle.CustomGeometryParticle;
import liedge.limacore.client.particle.CustomGeometryParticleEntry;
import liedge.ltxindustries.client.LTXIRenderer;
import liedge.ltxindustries.client.model.custom.EnergyBoltData;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;


public class EnergyBoltParticle extends CustomGeometryParticle
{
    private final Vec3 start;
    private final Vec3 end;
    private final int color;

    @Nullable
    private EnergyBoltData nextBolt;

    private EnergyBoltParticle(ClientLevel level, Vec3 start, Vec3 end, int color, double length)
    {
        super(level, start.x, start.y, start.z);

        this.start = start;
        this.end = end;
        this.color = color;
        this.nextBolt = makeNextBolt();

        setBoundingBox(AABB.ofSize(getPos(), length, length, length));
    }

    private EnergyBoltData makeNextBolt()
    {
        return EnergyBoltData.create(start, end, 0.015625f, 0.4f, level.getRandom());
    }

    @Override
    public void tick()
    {
        if (age++ >= lifetime)
        {
            remove();
            return;
        }

        nextBolt = EnergyBoltData.create(start, end, 0.015625f, 0.4f, level.getRandom());
    }

    @Override
    public @Nullable CustomGeometryParticleEntry extractEntry(float x, float y, float z, Camera camera, float partialTick)
    {
        return nextBolt != null ? new Entry(x, y, z, nextBolt, color) : null;
    }

    private record Entry(float x, float y, float z, EnergyBoltData bolt, int color) implements CustomGeometryParticleEntry
    {
        @Override
        public RenderType renderType()
        {
            return RenderTypes.lightning();
        }

        @Override
        public void render(PoseStack.Pose pose, VertexConsumer buffer)
        {
            LTXIRenderer.submitEnergyBolt(pose, buffer, bolt, color, 0.85f);
        }
    }

    public static final class Provider implements ParticleProvider<ColorParticleOptions>
    {
        @Override
        public @Nullable Particle createParticle(ColorParticleOptions particleType, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource random)
        {
            Vec3 start = new Vec3(x, y, z);
            Vec3 end = new Vec3(xSpeed, ySpeed, zSpeed);
            double length = start.distanceTo(end);

            return length <= 100 ? new EnergyBoltParticle(level, start, end, particleType.color(), length) : null;
        }
    }
}