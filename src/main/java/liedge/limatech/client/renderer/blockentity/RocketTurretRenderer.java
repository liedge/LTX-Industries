package liedge.limatech.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.lib.LimaColor;
import liedge.limatech.LimaTech;
import liedge.limatech.blockentity.RocketTurretBlockEntity;
import liedge.limatech.client.model.baked.DynamicModularBakedModel;
import liedge.limatech.client.renderer.LimaTechRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;

import static liedge.limatech.LimaTechConstants.*;

public class RocketTurretRenderer implements BlockEntityRenderer<RocketTurretBlockEntity>
{
    public static final ModelResourceLocation GUN_MODEL_PATH = ModelResourceLocation.standalone(LimaTech.RESOURCES.location("misc/rocket_turret_gun"));

    private final EntityRenderDispatcher entityRenderer;

    private final DynamicModularBakedModel.SubModel gun;
    private final DynamicModularBakedModel.SubModel swivel;

    public RocketTurretRenderer(BlockEntityRendererProvider.Context ctx)
    {
        this.entityRenderer = ctx.getEntityRenderer();
        DynamicModularBakedModel model = LimaCoreClientUtil.getCustomBakedModel(GUN_MODEL_PATH, DynamicModularBakedModel.class);
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
        Entity remoteTarget = blockEntity.getRemoteTarget();
        if (remoteTarget != null)
        {
            BlockPos pos = blockEntity.getBlockPos();

            double lx = Mth.lerp(partialTick, remoteTarget.xo - pos.getX(), remoteTarget.getX() - pos.getX());
            double ly = Mth.lerp(partialTick, remoteTarget.yo - pos.getY(), remoteTarget.getY() - pos.getY()) + (remoteTarget.getBoundingBox().getYsize() / 2d);
            double lz = Mth.lerp(partialTick, remoteTarget.zo - pos.getZ(), remoteTarget.getZ() - pos.getZ());

            poseStack.pushPose();

            VertexConsumer buffer = bufferSource.getBuffer(LimaTechRenderTypes.TARGET_TRIANGLE);
            Matrix4f mx4 = poseStack.last().pose();

            poseStack.translate(lx, ly, lz);
            poseStack.mulPose(Axis.YP.rotationDegrees(-entityRenderer.camera.getYRot()));
            poseStack.mulPose(Axis.XP.rotationDegrees(entityRenderer.camera.getXRot()));

            float size = (float) remoteTarget.getBoundingBox().getSize();
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