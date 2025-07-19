package liedge.ltxindustries.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.client.LTXIRenderUtil;
import liedge.ltxindustries.client.renderer.LTXIRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.InventoryMenu;
import org.joml.Matrix4f;

public class LightfragRenderer extends EntityRenderer<Entity>
{
    public LightfragRenderer(EntityRendererProvider.Context ctx)
    {
        super(ctx);
    }

    @Override
    public void render(Entity entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light)
    {
        poseStack.pushPose();

        poseStack.translate(0, 0.09375d, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(-entity.getYRot()));
        poseStack.mulPose(Axis.XP.rotationDegrees(180f + entity.getXRot()));

        VertexConsumer buffer = bufferSource.getBuffer(LTXIRenderTypes.POSITION_COLOR_QUADS);
        final float alpha = 0.875f;
        Matrix4f mx4 = poseStack.last().pose();

        LTXIRenderUtil.renderPositionColorCuboid(buffer, mx4, -0.03125f, -0.03125f, -0.125f, 0.03125f, 0.03125f, 0.125f, LTXIConstants.LIME_GREEN, alpha, LTXIRenderUtil.ALL_SIDES);

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(Entity entity)
    {
        return InventoryMenu.BLOCK_ATLAS;
    }
}