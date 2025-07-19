package liedge.ltxindustries.client.model.custom;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.lib.LimaColor;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.List;

import static liedge.limacore.util.LimaMathUtil.*;
import static liedge.ltxindustries.client.LTXIRenderUtil.renderPositionColorCuboid;

public class EnergyBoltModel
{
    private static final double MIN_BOLT_LENGTH = 0.125d;
    private static final double MAX_BOLT_LENGTH = 20d;
    private static final Direction[] BOLT_FACES = new Direction[] {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

    private static Vector2f arcAngles(double x1, double y1, double z1, double x2, double y2, double z2)
    {
        double vx = x2 - x1;
        double vy = y2 - y1;
        double vz = z2 - z1;

        float yRot = toDeg(Mth.atan2(vz, vx)) - 90f;
        float xRot = 90f - toDeg(Mth.atan2(vy, vec2Length(vx, vz)));

        return new Vector2f(xRot, yRot);
    }

    private static Vector2f arcAngles(Vec3 a, Vec3 b)
    {
        return arcAngles(a.x, a.y, a.z, b.x, b.y, b.z);
    }

    private static Vector2f arcAngles(Vector4f a, Vector4f b)
    {
        return arcAngles(a.x, a.y, a.z, b.x, b.y, b.z);
    }

    public static EnergyBoltModel multiPointBolt(List<Vec3> boltPoints, float boltThickness)
    {
        Preconditions.checkArgument(boltPoints.size() >= 2, "Energy bolt model requires at least 2 points");

        Vec3 first = boltPoints.getFirst();
        List<Vec3> adjusted = new ObjectArrayList<>();
        for (Vec3 o : boltPoints)
        {
            adjusted.add(o.subtract(first));
        }

        BoltVertexConsumer consumer = new BoltVertexConsumer();

        for (int bi = 0; bi < (adjusted.size() - 1); bi++)
        {
            // Bolt points
            Vec3 boltA = adjusted.get(bi);
            Vec3 boltB = adjusted.get(bi + 1);
            final float boltLength = (float) boltA.distanceTo(boltB);
            final int arcs = Mth.ceil(boltLength) * 2;
            final float arcPointDistance = divideFloat(boltLength, arcs);
            Vector2f boltAngle = arcAngles(boltA, boltB);

            // Origin matrix & vertex consumer
            Matrix4f origin = new Matrix4f();
            origin.translate((float) boltA.x, (float) boltA.y, (float) boltA.z);
            origin.rotate(Axis.YN.rotationDegrees(boltAngle.y));
            origin.rotate(Axis.XP.rotationDegrees(boltAngle.x));

            // Normalized arc points
            List<Vector4f> arcPoints = new ObjectArrayList<>();
            arcPoints.add(new Vector4f());
            for (int i = 1; i < arcs; i++)
            {
                float dx = (RANDOM.nextFloat() - RANDOM.nextFloat()) * 0.3125f;
                float dy = arcPointDistance * i;
                float dz = (RANDOM.nextFloat() - RANDOM.nextFloat()) * 0.3125f;
                arcPoints.add(new Vector4f(dx, dy, dz, 1f));
            }
            arcPoints.add(new Vector4f(0, boltLength, 0, 1f));

            Matrix4f pointMatrix = new Matrix4f();
            for (int i = 0; i < (arcPoints.size() - 1); i++)
            {
                pointMatrix.set(origin);

                Vector4f a = arcPoints.get(i);
                Vector4f b = arcPoints.get(i + 1);
                Vector2f arcAngle = arcAngles(a, b);
                float arcLength = (float) distanceBetween(a.x, a.y, a.z, b.x, b.y, b.z);

                pointMatrix.translate(a.x, a.y, a.z);
                pointMatrix.rotate(Axis.YN.rotationDegrees(arcAngle.y));
                pointMatrix.rotate(Axis.XP.rotationDegrees(arcAngle.x));

                renderPositionColorCuboid(consumer, pointMatrix, -boltThickness, 0, -boltThickness, boltThickness, arcLength, boltThickness, LimaColor.WHITE, 1f, BOLT_FACES);
            }
        }

        return new EnergyBoltModel(consumer.shapes);
    }

    public static EnergyBoltModel twoFixedPointBolt(double x1, double y1, double z1, double x2, double y2, double z2, float boltThickness)
    {
        // Bolt points
        final float boltLength = (float) Mth.clamp(distanceBetween(x1, y1, z1, x2, y2, z2), MIN_BOLT_LENGTH, MAX_BOLT_LENGTH);
        final int arcs = Mth.ceil(boltLength) * 2;
        final float arcPointDistance = divideFloat(boltLength, arcs);
        Vector2f boltAngle = arcAngles(x1, y1, z1, x2, y2, z2);

        // Normalized arc points
        List<Vector4f> arcPoints = new ObjectArrayList<>();
        arcPoints.add(new Vector4f());
        for (int i = 1; i < arcs; i++)
        {
            float dx = (RANDOM.nextFloat() - RANDOM.nextFloat()) * 0.3125f;
            float dy = arcPointDistance * i;
            float dz = (RANDOM.nextFloat() - RANDOM.nextFloat()) * 0.3125f;
            arcPoints.add(new Vector4f(dx, dy, dz, 1f));
        }
        arcPoints.add(new Vector4f(0, boltLength, 0, 1f));

        // Origin matrix & vertex consumer
        Matrix4f origin = new Matrix4f();
        origin.rotate(Axis.YN.rotationDegrees(boltAngle.y));
        origin.rotate(Axis.XP.rotationDegrees(boltAngle.x));
        BoltVertexConsumer consumer = new BoltVertexConsumer();

        Matrix4f pointMatrix = new Matrix4f();
        for (int i = 0; i < (arcPoints.size() - 1); i++)
        {
            pointMatrix.set(origin);

            Vector4f a = arcPoints.get(i);
            Vector4f b = arcPoints.get(i + 1);
            Vector2f arcAngle = arcAngles(a, b);
            float arcLength = (float) distanceBetween(a.x, a.y, a.z, b.x, b.y, b.z);

            pointMatrix.translate(a.x, a.y, a.z);
            pointMatrix.rotate(Axis.YN.rotationDegrees(arcAngle.y));
            pointMatrix.rotate(Axis.XP.rotationDegrees(arcAngle.x));

            renderPositionColorCuboid(consumer, pointMatrix, -boltThickness, 0, -boltThickness, boltThickness, arcLength, boltThickness, LimaColor.WHITE, 1f, BOLT_FACES);
        }

        return new EnergyBoltModel(consumer.shapes);
    }

    private final List<BoltShape> shapes;

    private EnergyBoltModel(List<BoltShape> shapes)
    {
        this.shapes = shapes;
    }

    public void renderEnergyBolt(VertexConsumer buffer, Matrix4f mx4, LimaColor color, float alpha)
    {
        for (BoltShape shape : shapes)
        {
            shape.addToBuffer(buffer, mx4, color, alpha);
        }
    }

    public void renderPartialBolt(VertexConsumer buffer, Matrix4f mx4, float boltAmount, LimaColor color, float alpha)
    {
        for (int i = 0; i < Math.min(shapes.size(), shapes.size() * boltAmount); i++)
        {
            shapes.get(i).addToBuffer(buffer, mx4, color, alpha);
        }
    }

    private record BoltShape(BoltQuad a, BoltQuad b, BoltQuad c, BoltQuad d)
    {
        private void addToBuffer(VertexConsumer buffer, Matrix4f mx4, LimaColor color, float alpha)
        {
            a.addToBuffer(buffer, mx4, color, alpha);
            b.addToBuffer(buffer, mx4, color, alpha);
            c.addToBuffer(buffer, mx4, color, alpha);
            d.addToBuffer(buffer, mx4, color, alpha);
        }
    }

    private record BoltQuad(float ax, float ay, float az, float bx, float by, float bz, float cx, float cy, float cz, float dx, float dy, float dz)
    {
        private void addToBuffer(VertexConsumer buffer, Matrix4f mx4, LimaColor color, float alpha)
        {
            buffer.addVertex(mx4, ax, ay, az).setColor(color.red(), color.green(), color.blue(), alpha);
            buffer.addVertex(mx4, bx, by, bz).setColor(color.red(), color.green(), color.blue(), alpha);
            buffer.addVertex(mx4, cx, cy, cz).setColor(color.red(), color.green(), color.blue(), alpha);
            buffer.addVertex(mx4, dx, dy, dz).setColor(color.red(), color.green(), color.blue(), alpha);
        }
    }

    private static class BoltVertexConsumer implements VertexConsumer
    {
        private final List<BoltShape> shapes = new ObjectArrayList<>();
        private final BoltQuad[] quadBuffer = new BoltQuad[4];
        private final float[][] vertexBuffer = new float[4][3];

        private int pos = 0;
        private int quadPos = 0;

        @Override
        public VertexConsumer addVertex(float x, float y, float z)
        {
            float[] sub = vertexBuffer[pos];

            sub[0] = x;
            sub[1] = y;
            sub[2] = z;

            pos++;

            if (pos == 4)
            {
                pos = 0;

                float[] a = vertexBuffer[0];
                float[] b = vertexBuffer[1];
                float[] c = vertexBuffer[2];
                float[] d = vertexBuffer[3];

                BoltQuad quad = new BoltQuad(a[0], a[1], a[2], b[0], b[1], b[2], c[0], c[1], c[2], d[0], d[1], d[2]);
                quadBuffer[quadPos] = quad;
                quadPos++;

                if (quadPos == 4)
                {
                    quadPos = 0;
                    shapes.add(new BoltShape(quadBuffer[0], quadBuffer[1], quadBuffer[2], quadBuffer[3]));
                }
            }

            return this;
        }

        @Override
        public VertexConsumer setColor(int red, int green, int blue, int alpha)
        {
            return this;
        }

        @Override
        public VertexConsumer setUv(float v, float v1)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public VertexConsumer setUv1(int i, int i1)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public VertexConsumer setUv2(int i, int i1)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public VertexConsumer setNormal(float v, float v1, float v2)
        {
            throw new UnsupportedOperationException();
        }
    }
}