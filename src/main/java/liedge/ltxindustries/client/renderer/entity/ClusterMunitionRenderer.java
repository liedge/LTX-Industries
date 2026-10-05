package liedge.ltxindustries.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.model.entity.ClusterMunitionModel;
import liedge.ltxindustries.client.model.entity.LTXIModelLayers;
import liedge.ltxindustries.entity.ClusterMunition;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;

public class ClusterMunitionRenderer extends EntityRenderer<ClusterMunition, ProjectileRenderState>
{
    private static final Identifier TEXTURE = LTXIndustries.RESOURCES.textureLocation("entity", "cluster_munition");

    private final ClusterMunitionModel model;

    public ClusterMunitionRenderer(EntityRendererProvider.Context context)
    {
        super(context);
        this.model = new ClusterMunitionModel(context.bakeLayer(LTXIModelLayers.CLUSTER_MUNITION));
    }

    @Override
    public ProjectileRenderState createRenderState()
    {
        return new ProjectileRenderState();
    }

    @Override
    public void extractRenderState(ClusterMunition entity, ProjectileRenderState state, float partialTick)
    {
        super.extractRenderState(entity, state, partialTick);

        state.yRot = LimaCoreMath.toRad(-entity.getYRot());
        state.xRot = LimaCoreMath.toRad(entity.lerpMotion(partialTick));
    }

    @Override
    public void submit(ProjectileRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera)
    {
        poseStack.pushPose();

        poseStack.translate(0f, -1.25f, 0f);

        model.setupAnim(state);
        submitNodeCollector.submitModel(model, state, poseStack, model.renderType(TEXTURE), LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, LTXIConstants.LIME_GREEN, null, state.outlineColor, null);

        poseStack.popPose();
    }
}