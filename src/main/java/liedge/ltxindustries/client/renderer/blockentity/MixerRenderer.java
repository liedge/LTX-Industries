package liedge.ltxindustries.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import liedge.limacore.client.model.StaticQuads;
import liedge.ltxindustries.blockentity.MixerBlockEntity;
import liedge.ltxindustries.client.model.LTXIModelPartKeys;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;

public class MixerRenderer extends MachineRenderer<MixerBlockEntity>
{
    private final StaticQuads blades;

    public MixerRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context);
        this.blades = StaticQuads.get(LTXIModelPartKeys.MIXER_BLADES);
    }

    @Override
    void extractAdditional(MixerBlockEntity blockEntity, MachineRenderState renderState, float partialTick)
    {
        renderState.machineSpin = blockEntity.lerpImpellerYRot(partialTick);
    }

    @Override
    public void submit(MachineRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState)
    {
        poseStack.pushPose();

        poseStack.translate(0.5f, 0f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(renderState.machineSpin));
        poseStack.translate(-0.5f, 0f, -0.5f);

        blades.submit(poseStack, nodeCollector, renderState.lightCoords);

        poseStack.popPose();
    }
}