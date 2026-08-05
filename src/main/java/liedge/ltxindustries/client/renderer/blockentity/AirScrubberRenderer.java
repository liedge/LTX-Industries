package liedge.ltxindustries.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import liedge.limacore.client.model.StaticQuads;
import liedge.ltxindustries.blockentity.AirScrubberBlockEntity;
import liedge.ltxindustries.client.LTXIRenderer;
import liedge.ltxindustries.client.model.LTXIModelPartKeys;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;

public final class AirScrubberRenderer extends SimpleMachineRenderer<AirScrubberBlockEntity>
{
    private final StaticQuads impeller;

    public AirScrubberRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context);
        this.impeller = StaticQuads.get(LTXIModelPartKeys.ATMOSPHERIC_SCRUBBER_IMPELLER);
    }

    @Override
    protected void extractAdditional(AirScrubberBlockEntity blockEntity, State state, float partialTick)
    {
        state.machineMotion = blockEntity.lerpImpellerRot(partialTick);
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState camera)
    {
        poseStack.pushPose();

        poseStack.translate(0.5f, 0f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(LTXIRenderer.facingYRotation(state.facing)));
        poseStack.translate(-0.5f, 0f, -0.5f);

        poseStack.translate(0.71875f, 0.5f, 0.5f);
        poseStack.mulPose(Axis.XP.rotationDegrees(state.machineMotion));
        poseStack.translate(-0.71875f, -0.5f, -0.5f);

        impeller.submit(poseStack, nodeCollector, state.lightCoords);

        poseStack.popPose();
    }
}