package liedge.ltxindustries.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.ltxindustries.LTXIIdentifiers;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.model.entity.BombletModel;
import liedge.ltxindustries.client.model.entity.LTXIModelLayers;
import liedge.ltxindustries.entity.Bomblet;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;

public class BombletRenderer extends EntityRenderer<Bomblet, BombletRenderer.State>
{
    private static final Identifier TEXTURE = LTXIndustries.RESOURCES.textureLocation("entity", LTXIIdentifiers.ID_BOMBLET);

    private final BombletModel model;

    public BombletRenderer(EntityRendererProvider.Context context)
    {
        super(context);
        this.model = new BombletModel(context.bakeLayer(LTXIModelLayers.BOMBLET));
    }

    @Override
    public State createRenderState()
    {
        return new State();
    }

    @Override
    public void extractRenderState(Bomblet entity, State state, float partialTicks)
    {
        super.extractRenderState(entity, state, partialTicks);

        state.rotation = LimaCoreMath.toRad(entity.lerpSpin(partialTicks));
        state.color = entity.getColor();
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera)
    {
        poseStack.pushPose();

        poseStack.translate(0f, -1.25f, 0f);

        model.setupAnim(state);
        submitNodeCollector.submitModel(model, state, poseStack, model.renderType(TEXTURE), LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, state.color, null, state.outlineColor, null);

        poseStack.popPose();
    }

    public static final class State extends EntityRenderState
    {
        public float rotation;
        public int color = -1;
    }
}