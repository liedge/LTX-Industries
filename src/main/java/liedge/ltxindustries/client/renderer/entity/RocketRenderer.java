package liedge.ltxindustries.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.client.model.entity.RocketModel;
import liedge.ltxindustries.entity.BaseRocketEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class RocketRenderer<T extends BaseRocketEntity> extends EntityRenderer<T>
{
    private static final ResourceLocation TEXTURE = LTXIndustries.RESOURCES.textureLocation("entity", "rocket");
    private static final RenderType RENDER_TYPE = RenderType.entitySolid(TEXTURE);

    private final RocketModel model;

    public RocketRenderer(EntityRendererProvider.Context ctx)
    {
        super(ctx);
        this.model = new RocketModel(ctx.getModelSet());
    }

    @Override
    public void render(T entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light)
    {
        poseStack.pushPose();

        poseStack.mulPose(Axis.YP.rotationDegrees(-entity.getYRot()));
        poseStack.translate(0, 0.21875d, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees(180f + entity.getXRot()));
        poseStack.translate(0, -1.34375d, 0);

        VertexConsumer buffer = bufferSource.getBuffer(RENDER_TYPE);
        model.renderToBuffer(poseStack, buffer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, LTXIConstants.LIME_GREEN.argb32());

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(T entity)
    {
        return TEXTURE;
    }
}