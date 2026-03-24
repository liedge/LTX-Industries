package liedge.ltxindustries.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.blockentity.VoltaicInjectorBlockEntity;
import liedge.ltxindustries.client.LTXIRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;

public final class VoltaicInjectorRenderer extends MachineRenderer<VoltaicInjectorBlockEntity>
{
    public VoltaicInjectorRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context);
    }

    @Override
    void extractAdditional(VoltaicInjectorBlockEntity blockEntity, MachineRenderState renderState, float partialTick)
    {
        renderState.machineBolt = blockEntity.platformBolt;
    }

    @Override
    public void submit(MachineRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState)
    {
        if (renderState.machineBolt == null) return;

        poseStack.pushPose();

        poseStack.translate(0.5f, 0.375f, 0.5f);
        LTXIRenderer.submitEnergyBolt(poseStack, nodeCollector, RenderTypes.lightning(), renderState.machineBolt, LTXIConstants.ELECTRIC_GREEN, 0.9f);

        poseStack.popPose();
    }
}