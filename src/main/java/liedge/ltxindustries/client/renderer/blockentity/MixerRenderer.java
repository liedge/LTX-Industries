package liedge.ltxindustries.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import liedge.limacore.client.model.StaticQuads;
import liedge.ltxindustries.blockentity.MixerBlockEntity;
import liedge.ltxindustries.client.model.LTXIModelPartKeys;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;

public class MixerRenderer extends SimpleMachineRenderer<MixerBlockEntity>
{
    private final StaticQuads blades;

    public MixerRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context);
        this.blades = StaticQuads.get(LTXIModelPartKeys.MIXER_BLADES);
    }

    @Override
    protected void extractAdditional(MixerBlockEntity blockEntity, State state, float partialTick)
    {
        state.machineSpin = blockEntity.lerpImpellerYRot(partialTick);
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState camera)
    {
        poseStack.pushPose();

        poseStack.translate(0.5f, 0f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(state.machineSpin));
        poseStack.translate(-0.5f, 0f, -0.5f);

        blades.submit(poseStack, nodeCollector, state.lightCoords);

        poseStack.popPose();
    }
}