package liedge.limatech.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.client.LimaTechRenderUtil;
import liedge.limatech.client.renderer.LimaTechRenderTypes;
import liedge.limatech.entity.LightfragEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import org.joml.Matrix4f;

public class LightfragRenderer extends EntityRenderer<LightfragEntity>
{
    public LightfragRenderer(EntityRendererProvider.Context ctx)
    {
        super(ctx);
    }

    @Override
    public void render(LightfragEntity entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light)
    {
        poseStack.pushPose();

        poseStack.translate(0, 0.09375d, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(-entity.getYRot()));
        poseStack.mulPose(Axis.XP.rotationDegrees(180f + entity.getXRot()));

        VertexConsumer buffer = bufferSource.getBuffer(LimaTechRenderTypes.POSITION_COLOR_TRANSLUCENT);
        final float alpha = 0.875f;
        Matrix4f mx4 = poseStack.last().pose();

        LimaTechRenderUtil.renderPositionColorCuboid(buffer, mx4, -0.03125f, -0.03125f, -0.125f, 0.03125f, 0.03125f, 0.125f, LimaTechConstants.LIME_GREEN, alpha, LimaTechRenderUtil.ALL_SIDES);

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(LightfragEntity entity)
    {
        return InventoryMenu.BLOCK_ATLAS;
    }
}