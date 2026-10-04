package liedge.ltxindustries.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.ltxindustries.client.model.entity.ProjectileModel;
import liedge.ltxindustries.entity.LTXIProjectileEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import org.jspecify.annotations.Nullable;

public abstract class ProjectileRenderer<T extends LTXIProjectileEntity> extends EntityRenderer<T, ProjectileRenderState>
{
    private final ProjectileModel base;
    private final @Nullable ProjectileModel emissive;

    protected ProjectileRenderer(EntityRendererProvider.Context context)
    {
        super(context);
        this.base = createBaseModel(context);
        this.emissive = createEmissiveModel(context);
    }

    protected abstract Identifier texture();

    protected abstract ProjectileModel createBaseModel(EntityRendererProvider.Context context);

    protected @Nullable ProjectileModel createEmissiveModel(EntityRendererProvider.Context context)
    {
        return null;
    }

    protected float yOffset()
    {
        return 0f;
    }

    @Override
    public ProjectileRenderState createRenderState()
    {
        return new ProjectileRenderState();
    }

    @Override
    public void extractRenderState(T entity, ProjectileRenderState state, float partialTick)
    {
        super.extractRenderState(entity, state, partialTick);
        state.yRot = LimaCoreMath.toRad(-entity.getYRot());
        state.xRot = LimaCoreMath.toRad(entity.getXRot() - 90f);
    }

    @Override
    public void submit(ProjectileRenderState state, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraState)
    {
        poseStack.pushPose();

        poseStack.translate(0, yOffset(), 0);

        base.setupAnim(state);
        nodeCollector.submitModel(base, state, poseStack, texture(), state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);

        if (emissive != null)
        {
            emissive.setupAnim(state);
            nodeCollector.submitModel(emissive, state, poseStack, emissive.renderType(texture()), LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, state.energyColor, null, state.outlineColor, null);
        }

        poseStack.popPose();
    }
}