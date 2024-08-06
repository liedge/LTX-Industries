package liedge.limatech.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import liedge.limacore.client.LimaBlockEntityRenderer;
import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.lib.LimaColor;
import liedge.limatech.blockentity.RocketTurretBlockEntity;
import liedge.limatech.client.model.baked.DynamicModularBakedModel;
import liedge.limatech.client.model.baked.LimaTechExtraBakedModels;
import liedge.limatech.client.renderer.LimaTechRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;

import static liedge.limatech.LimaTechConstants.HOSTILE_ORANGE;
import static liedge.limatech.LimaTechConstants.LIME_GREEN;

public class RocketTurretRenderer extends LimaBlockEntityRenderer<RocketTurretBlockEntity>
{
    private final DynamicModularBakedModel.SubModel gun;
    private final DynamicModularBakedModel.SubModel swivel;

    public RocketTurretRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context);

        DynamicModularBakedModel model = LimaCoreClientUtil.getCustomBakedModel(LimaTechExtraBakedModels.ROCKET_TURRET_GUN, DynamicModularBakedModel.class);
        this.gun = model.getSubmodel("gun");
        this.swivel = model.getSubmodel("swivel");
    }

    @Override
    public void render(RocketTurretBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay)
    {
        // Render the turret gun
        poseStack.pushPose();

        float ry = blockEntity.lerpYRot(partialTick);

        poseStack.translate(0.5d, 0, 0.5d);
        poseStack.mulPose(Axis.YP.rotationDegrees(ry));
        poseStack.translate(-0.5d, 0, -0.5d);

        swivel.renderToBuffer(poseStack, bufferSource, light);

        poseStack.translate(0.5d, 1.6875d, 0.5d);
        poseStack.mulPose(Axis.XN.rotationDegrees(blockEntity.lerpXRot(partialTick)));
        poseStack.translate(-0.5d, -1.6875d, -0.5d);

        gun.renderToBuffer(poseStack, bufferSource, light);

        poseStack.popPose();

        // Render lock on triangle
        Entity currentTarget = blockEntity.getCurrentTarget();
        if (currentTarget != null)
        {
            poseStack.pushPose();

            VertexConsumer buffer = bufferSource.getBuffer(LimaTechRenderTypes.TARGET_TRIANGLE);
            Matrix4f mx4 = poseStack.last().pose();

            double[] targetCenter = lerpEntityCenter(blockEntity.getBlockPos(), currentTarget, partialTick);

            poseStack.translate(targetCenter[0], targetCenter[1], targetCenter[2]);
            poseStack.mulPose(Axis.YP.rotationDegrees(-entityRenderer.camera.getYRot()));
            poseStack.mulPose(Axis.XP.rotationDegrees(entityRenderer.camera.getXRot()));

            float size = (float) currentTarget.getBoundingBox().getSize();
            poseStack.scale(size, size, size);
            poseStack.mulPose(Axis.ZP.rotationDegrees(ry));
            poseStack.translate(-0.5d, -0.5d, 0);

            float lerpTick = blockEntity.lerpTicker(partialTick);
            float lockProgress = -0.6875f - (2f * (1 - lerpTick));
            LimaColor color = lerpTick >= 1 ? LIME_GREEN : HOSTILE_ORANGE;

            renderTargetTriangle(mx4, buffer, 0, lockProgress, 1, color);

            poseStack.translate(0.5d, 0.5d, 0);
            poseStack.mulPose(Axis.ZP.rotationDegrees(120));
            poseStack.translate(-0.5d, -0.5d, 0);
            renderTargetTriangle(mx4, buffer, 0, lockProgress, 1, color);

            poseStack.translate(0.5d, 0.5d, 0);
            poseStack.mulPose(Axis.ZP.rotationDegrees(120));
            poseStack.translate(-0.5d, -0.5d, 0);
            renderTargetTriangle(mx4, buffer, 0, lockProgress, 1, color);

            poseStack.popPose();
        }
    }

    private void renderTargetTriangle(Matrix4f mx4, VertexConsumer buffer, float x1, float y1, float size, LimaColor color)
    {
        float x2 = x1 + size;
        float y2 = y1 + size;

        buffer.addVertex(mx4, x1, y1, 0).setColor(color.red(), color.green(), color.blue(), 1f).setUv(0, 0).setLight(LightTexture.FULL_BRIGHT);
        buffer.addVertex(mx4, x1, y2, 0).setColor(color.red(), color.green(), color.blue(), 1f).setUv(0, 1).setLight(LightTexture.FULL_BRIGHT);
        buffer.addVertex(mx4, x2, y2, 0).setColor(color.red(), color.green(), color.blue(), 1f).setUv(1, 1).setLight(LightTexture.FULL_BRIGHT);
        buffer.addVertex(mx4, x2, y1, 0).setColor(color.red(), color.green(), color.blue(), 1f).setUv(1, 0).setLight(LightTexture.FULL_BRIGHT);
    }

    @Override
    public int getViewDistance()
    {
        return 128;
    }

    @Override
    public boolean shouldRenderOffScreen(RocketTurretBlockEntity blockEntity)
    {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(RocketTurretBlockEntity blockEntity)
    {
        return blockEntity.getTargetArea();
    }
}