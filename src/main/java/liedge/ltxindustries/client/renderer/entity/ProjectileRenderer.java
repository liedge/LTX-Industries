package liedge.ltxindustries.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.ltxindustries.client.model.entity.ProjectileModel;
import liedge.ltxindustries.entity.LTXIProjectileEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;

public abstract class ProjectileRenderer<T extends LTXIProjectileEntity> extends EntityRenderer<T, ProjectileRenderState>
{
    private final ProjectileModel model;

    protected ProjectileRenderer(EntityRendererProvider.Context context)
    {
        super(context);
        this.model = createModel(context);
    }

    protected abstract Identifier texture();

    protected abstract ProjectileModel createModel(EntityRendererProvider.Context context);

    @Override
    public ProjectileRenderState createRenderState()
    {
        return new ProjectileRenderState();
    }

    @Override
    public void extractRenderState(T entity, ProjectileRenderState reusedState, float partialTick)
    {
        super.extractRenderState(entity, reusedState, partialTick);
        reusedState.yRot = LimaCoreMath.toRad(-entity.getYRot());
        reusedState.xRot = LimaCoreMath.toRad(entity.getXRot() - 90f);
    }

    @Override
    public void submit(ProjectileRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState)
    {
        poseStack.pushPose();

        model.setupAnim(renderState);
        model.submitParts(renderState, poseStack, nodeCollector, texture());

        poseStack.popPose();
    }
}