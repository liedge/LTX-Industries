package liedge.ltxindustries.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.client.LTXIRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public record LockOnRenderData(float x, float y, float z, float xRot, float yRot, float size, float progress) implements SubmitNodeCollector.CustomGeometryRenderer
{
    public static LockOnRenderData of(Entity entity, double originX, double originY, double originZ, Camera camera, float progress, float partialTick)
    {
        double[] pos = LTXIRenderer.lerpEntityCenter(entity, originX, originY, originZ, partialTick);
        float size = (float) entity.getBoundingBox().getSize();

        float xRot = camera.xRot();
        float yRot = -camera.yRot();

        return new LockOnRenderData((float) pos[0], (float) pos[1], (float) pos[2], xRot, yRot, size, progress);
    }

    public static LockOnRenderData of(Entity entity, Camera camera, float progress, float partialTick)
    {
        Vec3 pos = camera.position();
        return of(entity, pos.x, pos.y, pos.z, camera, progress, partialTick);
    }

    @Override
    public void render(PoseStack.Pose pose, VertexConsumer consumer)
    {
        pose.translate(x, y, z);
        pose.rotate(Axis.YP.rotationDegrees(yRot));
        pose.rotate(Axis.XP.rotationDegrees(xRot));

        pose.scale(size, size, size);

        float spin = (Util.getMillis() % 5000L) / 5000f;
        pose.rotate(Axis.ZP.rotationDegrees(Mth.wrapDegrees(spin * 360f)));

        float spread = -0.6875f - (2f * (1f - progress));
        LimaColor color = progress >= 1 ? LTXIConstants.LIME_GREEN : LTXIConstants.HOSTILE_ORANGE;

        drawQuad(pose, consumer, spread, 0, color);
        drawQuad(pose, consumer, spread, 120, color);
        drawQuad(pose, consumer, spread, 240, color);
    }

    private void drawQuad(PoseStack.Pose pose, VertexConsumer buffer, float spread, int angle, LimaColor color)
    {
        float angleRad = LimaCoreMath.toRad(angle);

        float centerX = spread * Mth.cos(angleRad);
        float centerY = spread * Mth.sin(angleRad);

        float[] vtx = {
                -0.5f, -0.5f,
                -0.5f, 0.5f,
                0.5f, 0.5f,
                0.5f, -0.5f
        };

        float cos = Mth.cos(angleRad - Mth.HALF_PI);
        float sin = Mth.sin(angleRad - Mth.HALF_PI);

        for (int i = 0; i < vtx.length; i += 2)
        {
            float x = vtx[i] ;
            float y = vtx[i + 1];

            vtx[i] = (x * cos - y * sin) + centerX;
            vtx[i + 1] = (x * sin + y * cos) + centerY;
        }

        buffer.addVertex(pose, vtx[0], vtx[1], 0).setColor(color.red(), color.green(), color.blue(), 1f).setUv(0, 0);
        buffer.addVertex(pose, vtx[2], vtx[3], 0).setColor(color.red(), color.green(), color.blue(), 1f).setUv(0, 1);
        buffer.addVertex(pose, vtx[4], vtx[5], 0).setColor(color.red(), color.green(), color.blue(), 1f).setUv(1, 1);
        buffer.addVertex(pose, vtx[6], vtx[7], 0).setColor(color.red(), color.green(), color.blue(), 1f).setUv(1, 0);
    }
}