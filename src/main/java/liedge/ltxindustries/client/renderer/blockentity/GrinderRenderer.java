package liedge.ltxindustries.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import liedge.limacore.client.model.StaticQuads;
import liedge.ltxindustries.blockentity.GrinderBlockEntity;
import liedge.ltxindustries.client.LTXIRenderer;
import liedge.ltxindustries.client.model.LTXIModelPartKeys;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;

public class GrinderRenderer extends SimpleMachineRenderer<GrinderBlockEntity>
{
    private final StaticQuads frontCrusher;
    private final StaticQuads rearCrusher;

    public GrinderRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context);
        this.frontCrusher = StaticQuads.get(LTXIModelPartKeys.GRINDER_FRONT_CRUSHER);
        this.rearCrusher = StaticQuads.get(LTXIModelPartKeys.GRINDER_REAR_CRUSHER);
    }

    @Override
    protected void extractAdditional(GrinderBlockEntity blockEntity, State state, float partialTick)
    {
        state.machineSpin = blockEntity.lerpCrushersRot(partialTick);
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState camera)
    {
        poseStack.pushPose();

        // Orient crushers to block facing
        poseStack.translate(0.5f, 0f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(LTXIRenderer.facingYRotation(state.facing)));
        poseStack.translate(-0.5f, 0f, -0.5f);

        // Rotate and render front crusher
        poseStack.pushPose();
        poseStack.translate(0.5f, 0.625f, 0.375f);
        poseStack.mulPose(Axis.XP.rotationDegrees(state.machineSpin));
        poseStack.translate(-0.5f, -0.625f, -0.375f);

        frontCrusher.submit(poseStack, nodeCollector, state.lightCoords);
        poseStack.popPose();

        // Rotate and render back crusher
        poseStack.pushPose();
        poseStack.translate(0.5f, 0.625f, 0.625f);
        poseStack.mulPose(Axis.XN.rotationDegrees(state.machineSpin));
        poseStack.translate(-0.5f, -0.625f, -0.625f);

        rearCrusher.submit(poseStack, nodeCollector, state.lightCoords);
        poseStack.popPose();

        poseStack.popPose();
    }
}