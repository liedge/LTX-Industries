package liedge.ltxindustries.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import liedge.limacore.client.renderer.LimaCoreRenderTypes;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.LTXIRenderer;
import liedge.ltxindustries.client.model.entity.LTXIModelLayers;
import liedge.ltxindustries.client.model.entity.SeekerMineModel;
import liedge.ltxindustries.entity.SeekerMine;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class SeekerMineRenderer extends EntityRenderer<SeekerMine, SeekerMineRenderState>
{
    private static final Identifier TEXTURE = LTXIndustries.RESOURCES.textureLocation("entity", "seeker_mine");
    private static final float LASER_RADIUS = 0.015625f;

    private final SeekerMineModel base;
    private final SeekerMineModel emissive;

    public SeekerMineRenderer(EntityRendererProvider.Context context)
    {
        super(context);
        this.base = new SeekerMineModel(context.bakeLayer(LTXIModelLayers.SEEKER_MINE_BASE), RenderTypes::entityCutout);
        this.emissive = new SeekerMineModel(context.bakeLayer(LTXIModelLayers.SEEKER_MINE_EMISSIVE), LimaCoreRenderTypes::entityCutoutEmissive);
    }

    @Override
    public SeekerMineRenderState createRenderState()
    {
        return new SeekerMineRenderState();
    }

    @Override
    public void extractRenderState(SeekerMine entity, SeekerMineRenderState state, float partialTicks)
    {
        super.extractRenderState(entity, state, partialTicks);

        state.yRot = LimaCoreMath.toRad(-entity.getYRot());
        state.wheelXRot = LimaCoreMath.toRad(-Mth.rotLerp(partialTicks, entity.wheelSpin0, entity.wheelSpin));
        state.capXRot = LimaCoreMath.toRad(-Mth.rotLerp(partialTicks, entity.capSpin0, entity.capSpin));

        LivingEntity target = entity.getRemoteTarget();
        if (target != null && entity.distanceToSqr(target) <= 1024)
        {
            state.targetPos = LTXIRenderer.lerpEntityCenter(target, state.x, state.y, state.z, partialTicks);
        }
    }

    @Override
    public void submit(SeekerMineRenderState state, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState camera)
    {
        poseStack.pushPose();

        if (state.targetPos != null)
        {
            float yo = 0.1875f + LASER_RADIUS;
            poseStack.translate(0, yo, 0);
            nodeCollector.submitCustomGeometry(poseStack, RenderTypes.lines(), (pose, buffer) -> {
                float l = state.targetPos.length();
                float nx = state.targetPos.x() / l;
                float ny = state.targetPos.y() / l;
                float nz = state.targetPos.z() / l;

                buffer.addVertex(pose, 0, 0, 0).setColor(LTXIConstants.LIME_GREEN).setNormal(pose, nx, ny, nz).setLineWidth(2f);
                buffer.addVertex(pose, state.targetPos).setColor(LTXIConstants.LIME_GREEN).setNormal(pose, -nx, -ny, -nz).setLineWidth(2f);
            });
            poseStack.translate(0, -yo, 0);
        }

        poseStack.translate(0, -1f, 0);

        base.setupAnim(state);
        nodeCollector.submitModel(base, state, poseStack, TEXTURE, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);

        emissive.setupAnim(state);
        nodeCollector.submitModel(emissive, state, poseStack, TEXTURE, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, state.outlineColor, null);

        poseStack.popPose();
    }

    @Override
    protected boolean affectedByCulling(SeekerMine entity)
    {
        return !entity.isAggressive();
    }
}