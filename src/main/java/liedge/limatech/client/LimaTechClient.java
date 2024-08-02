package liedge.limatech.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import liedge.limacore.lib.LimaColor;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public final class LimaTechClient
{
    private static final float ANIMATION_B_FACTOR = 1 / 0.9f;
    private static final float ANIMATION_C_FACTOR = 1 / 0.7f;

    public static final Direction[] ALL_SIDES = Direction.values();

    private LimaTechClient() {}

    public static float animationCurveSin(float delta)
    {
        return Mth.sin(Mth.PI * delta);
    }

    public static float animationCurveA(float delta)
    {
        return delta <= 0.2f ? delta / 0.2f : 1 - ((delta - 0.2f) * 1.25f);
    }

    public static float animationCurveB(float delta)
    {
        return delta <= 0.1f ? delta / 0.1f : 1 - ((delta - 0.1f) * ANIMATION_B_FACTOR);
    }

    public static float animationCurveC(float delta)
    {
        return delta <= 0.3f ? delta / 0.3f : 1 - ((delta - 0.3f) * ANIMATION_C_FACTOR);
    }

    public static void renderPositionColorCuboid(VertexConsumer buffer, Matrix4f mx4, float x1, float y1, float z1, float x2, float y2, float z2, LimaColor color, float alpha, Direction[] sides)
    {
        for (Direction side : sides)
        {
            switch (side)
            {
                case UP ->
                {
                    buffer.addVertex(mx4, x2, y2, z2).setColor(color.red(), color.green(), color.blue(), alpha);
                    buffer.addVertex(mx4, x2, y2, z1).setColor(color.red(), color.green(), color.blue(), alpha);
                    buffer.addVertex(mx4, x1, y2, z1).setColor(color.red(), color.green(), color.blue(), alpha);
                    buffer.addVertex(mx4, x1, y2, z2).setColor(color.red(), color.green(), color.blue(), alpha);
                }
                case DOWN ->
                {
                    buffer.addVertex(mx4, x1, y1, z1).setColor(color.red(), color.green(), color.blue(), alpha);
                    buffer.addVertex(mx4, x2, y1, z1).setColor(color.red(), color.green(), color.blue(), alpha);
                    buffer.addVertex(mx4, x2, y1, z2).setColor(color.red(), color.green(), color.blue(), alpha);
                    buffer.addVertex(mx4, x1, y1, z2).setColor(color.red(), color.green(), color.blue(), alpha);
                }
                case WEST ->
                {
                    buffer.addVertex(mx4, x1, y2, z2).setColor(color.red(), color.green(), color.blue(), alpha);
                    buffer.addVertex(mx4, x1, y2, z1).setColor(color.red(), color.green(), color.blue(), alpha);
                    buffer.addVertex(mx4, x1, y1, z1).setColor(color.red(), color.green(), color.blue(), alpha);
                    buffer.addVertex(mx4, x1, y1, z2).setColor(color.red(), color.green(), color.blue(), alpha);
                }
                case EAST ->
                {
                    buffer.addVertex(mx4, x2, y1, z1).setColor(color.red(), color.green(), color.blue(), alpha);
                    buffer.addVertex(mx4, x2, y2, z1).setColor(color.red(), color.green(), color.blue(), alpha);
                    buffer.addVertex(mx4, x2, y2, z2).setColor(color.red(), color.green(), color.blue(), alpha);
                    buffer.addVertex(mx4, x2, y1, z2).setColor(color.red(), color.green(), color.blue(), alpha);
                }
                case SOUTH ->
                {
                    buffer.addVertex(mx4, x1, y1, z2).setColor(color.red(), color.green(), color.blue(), alpha);
                    buffer.addVertex(mx4, x2, y1, z2).setColor(color.red(), color.green(), color.blue(), alpha);
                    buffer.addVertex(mx4, x2, y2, z2).setColor(color.red(), color.green(), color.blue(), alpha);
                    buffer.addVertex(mx4, x1, y2, z2).setColor(color.red(), color.green(), color.blue(), alpha);
                }
                case NORTH ->
                {
                    buffer.addVertex(mx4, x2, y2, z1).setColor(color.red(), color.green(), color.blue(), alpha);
                    buffer.addVertex(mx4, x2, y1, z1).setColor(color.red(), color.green(), color.blue(), alpha);
                    buffer.addVertex(mx4, x1, y1, z1).setColor(color.red(), color.green(), color.blue(), alpha);
                    buffer.addVertex(mx4, x1, y2, z1).setColor(color.red(), color.green(), color.blue(), alpha);
                }
            }
        }
    }
}