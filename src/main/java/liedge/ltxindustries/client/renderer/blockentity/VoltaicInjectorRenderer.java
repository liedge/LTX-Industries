package liedge.ltxindustries.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import liedge.limacore.client.LimaBlockEntityRenderer;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.blockentity.VoltaicInjectorBlockEntity;
import liedge.ltxindustries.client.LTXIRenderUtil;
import liedge.ltxindustries.client.model.custom.EnergyBoltData;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class VoltaicInjectorRenderer extends LimaBlockEntityRenderer<VoltaicInjectorBlockEntity>
{
    public VoltaicInjectorRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context);
    }

    @Override
    public void render(VoltaicInjectorBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
    {
        EnergyBoltData bolt = blockEntity.platformBolt;
        if (bolt == null) return;

        poseStack.pushPose();

        poseStack.translate(0.5f, 0.375f, 0.5f);

        VertexConsumer buffer = bufferSource.getBuffer(RenderType.lightning());

        LTXIRenderUtil.submitEnergyBolt(buffer, poseStack.last().pose(), bolt, LTXIConstants.ELECTRIC_GREEN, 0.9f);

        poseStack.popPose();
    }
}