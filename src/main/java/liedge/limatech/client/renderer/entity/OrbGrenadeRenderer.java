package liedge.limatech.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import liedge.limatech.client.model.entity.OrbGrenadeModel;
import liedge.limatech.entity.OrbGrenadeEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class OrbGrenadeRenderer extends EntityRenderer<OrbGrenadeEntity>
{
    private final OrbGrenadeModel model;

    public OrbGrenadeRenderer(EntityRendererProvider.Context ctx)
    {
        super(ctx);
        this.model = new OrbGrenadeModel(ctx.getModelSet());
    }

    @Override
    public void render(OrbGrenadeEntity entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light)
    {
        poseStack.pushPose();

        poseStack.translate(0, 0.1875d, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(180f - entity.getYRot()));
        model.animateFromEntity(entity, partialTick);
        poseStack.translate(0, -1.1875d, 0);

        model.renderToBuffer(poseStack, bufferSource, light, entity.getGrenadeElement().getColor());

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(OrbGrenadeEntity entity)
    {
        return OrbGrenadeModel.TEXTURE;
    }
}