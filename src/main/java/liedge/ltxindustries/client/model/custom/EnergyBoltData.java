package liedge.ltxindustries.client.model.custom;

import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.lib.math.LimaCoreMath;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.List;

public record EnergyBoltData(List<Vector3f[]> segments)
{
    private static final double MIN_LENGTH = 0.0625d;
    private static final double MAX_LENGTH = 64d;

    public static EnergyBoltData create(double x1, double y1, double z1, double x2, double y2, double z2, float thickness, float amplitude, RandomSource random)
    {
        // General bolt data
        final float boltLength = (float) Mth.clamp(LimaCoreMath.distanceBetween(x1, y1, z1, x2, y2, z2), MIN_LENGTH, MAX_LENGTH);
        final int arcs = Mth.clamp(Mth.ceil(boltLength), 4, 32);
        final float arcPointDistance = LimaCoreMath.divideFloat(boltLength, arcs);
        Vector2f boltDirection = LimaCoreMath.xyRotBetweenPoints(x1, y1, z1, x2, y2, z2);

        // Normalized points
        List<Vector3f> arcPoints = new ObjectArrayList<>();
        arcPoints.add(new Vector3f());
        amplitude = Mth.clamp(amplitude, 0f, 0.75f);
        for (int i = 1; i < arcs; i++)
        {
            float dx = (random.nextFloat() - random.nextFloat()) * amplitude;
            float dy = arcPointDistance * i;
            float dz = (random.nextFloat() - random.nextFloat()) * amplitude;
            arcPoints.add(new Vector3f(dx, dy, dz));
        }
        arcPoints.add(new Vector3f(0, boltLength, 0));

        // Geometry
        Matrix4f origin = new Matrix4f();
        origin.rotate(Axis.YP.rotationDegrees(-boltDirection.y));
        origin.rotate(Axis.XP.rotationDegrees(boltDirection.x + 90f));

        List<Vector3f[]> segments = new ObjectArrayList<>();
        Matrix4f pointMatrix = new Matrix4f();
        for (int i = 0; i < (arcPoints.size() - 1); i++)
        {
            pointMatrix.set(origin);

            Vector3f a = arcPoints.get(i);
            Vector3f b = arcPoints.get(i + 1);
            Vector2f arcDirection = LimaCoreMath.xyRotBetweenPoints(a.x, a.y, a.z, b.x, b.y, b.z);
            float arcLength = (float) LimaCoreMath.distanceBetween(a.x, a.y, a.z, b.x, b.y, b.z);

            pointMatrix.translate(a);
            pointMatrix.rotate(Axis.YP.rotationDegrees(-arcDirection.y));
            pointMatrix.rotate(Axis.XP.rotationDegrees(arcDirection.x + 90f));

            segments.add(vertices(pointMatrix, thickness, arcLength));
        }

        return new EnergyBoltData(segments);
    }

    public static EnergyBoltData create(Vec3 start, Vec3 end, float thickness, float amplitude, RandomSource random)
    {
        return create(start.x, start.y, start.z, end.x, end.y, end.z, thickness, amplitude, random);
    }

    private static Vector3f vertex(Matrix4f mx4, float x, float y, float z)
    {
        Vector3f result = new Vector3f(x, y, z);
        return result.mulPosition(mx4);
    }

    private static Vector3f[] vertices(Matrix4f mx4, float t, float length)
    {
        return new Vector3f[] {
                vertex(mx4, -t, 0, -t),
                vertex(mx4, t, 0, -t),
                vertex(mx4, t, length, -t),
                vertex(mx4, -t, length, -t),

                vertex(mx4, -t, 0, t),
                vertex(mx4, t, 0, t),
                vertex(mx4, t, length, t),
                vertex(mx4, -t, length, t)
        };
    }
}