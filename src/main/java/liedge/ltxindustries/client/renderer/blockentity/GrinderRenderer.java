package liedge.ltxindustries.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import liedge.ltxindustries.blockentity.GrinderBlockEntity;
import liedge.ltxindustries.client.LTXIRenderer;
import liedge.ltxindustries.client.model.LTXIModelPartKeys;
import liedge.ltxindustries.client.model.StandaloneQuads;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;

public class GrinderRenderer extends MachineRenderer<GrinderBlockEntity>
{
    private final StandaloneQuads frontCrusher;
    private final StandaloneQuads rearCrusher;

    public GrinderRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context);
        this.frontCrusher = StandaloneQuads.get(LTXIModelPartKeys.GRINDER_FRONT_CRUSHER);
        this.rearCrusher = StandaloneQuads.get(LTXIModelPartKeys.GRINDER_REAR_CRUSHER);
    }

    @Override
    void extractAdditional(GrinderBlockEntity blockEntity, MachineRenderState renderState, float partialTick)
    {
        renderState.machineSpin = blockEntity.lerpCrushersRot(partialTick);
    }

    @Override
    public void submit(MachineRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState)
    {
        poseStack.pushPose();

        // Orient crushers to block facing
        poseStack.translate(0.5f, 0f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(LTXIRenderer.facingYRotation(renderState.facing)));
        poseStack.translate(-0.5f, 0f, -0.5f);

        // Rotate and render front crusher
        poseStack.pushPose();
        poseStack.translate(0.5f, 0.625f, 0.375f);
        poseStack.mulPose(Axis.XP.rotationDegrees(renderState.machineSpin));
        poseStack.translate(-0.5f, -0.625f, -0.375f);

        frontCrusher.render(poseStack, nodeCollector, renderState.lightCoords);
        poseStack.popPose();

        // Rotate and render back crusher
        poseStack.pushPose();
        poseStack.translate(0.5f, 0.625f, 0.625f);
        poseStack.mulPose(Axis.XN.rotationDegrees(renderState.machineSpin));
        poseStack.translate(-0.5f, -0.625f, -0.625f);

        rearCrusher.render(poseStack, nodeCollector, renderState.lightCoords);
        poseStack.popPose();

        poseStack.popPose();
    }
}