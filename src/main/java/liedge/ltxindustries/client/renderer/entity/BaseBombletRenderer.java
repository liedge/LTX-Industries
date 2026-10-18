package liedge.ltxindustries.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import liedge.ltxindustries.LTXIIdentifiers;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.model.entity.BombletModel;
import liedge.ltxindustries.client.model.entity.LTXIModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.entity.Entity;

public class BaseBombletRenderer<T extends Entity> extends EntityRenderer<T, ProjectileRenderState>
{
    private static final Identifier TEXTURE = LTXIndustries.RESOURCES.textureLocation("entity", LTXIIdentifiers.ID_BOMBLET);

    private final BombletModel model;

    protected BaseBombletRenderer(EntityRendererProvider.Context context)
    {
        super(context);
        this.model = new BombletModel(context.bakeLayer(LTXIModelLayers.BOMBLET));
    }

    @Override
    public ProjectileRenderState createRenderState()
    {
        return new ProjectileRenderState();
    }

    @Override
    public void submit(ProjectileRenderState state, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState camera)
    {
        poseStack.pushPose();

        poseStack.translate(0f, -1.25f, 0f);

        model.setupAnim(state);
        nodeCollector.submitModel(model, state, poseStack, model.renderType(TEXTURE), LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, state.color, null, state.outlineColor, null);

        poseStack.popPose();
    }
}