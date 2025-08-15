package liedge.ltxindustries.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import liedge.limacore.client.LimaBlockEntityRenderer;
import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.client.model.baked.BakedItemLayer;
import liedge.limacore.client.model.baked.ItemLayerBakedModel;
import liedge.ltxindustries.blockentity.ElectroCentrifugeBlockEntity;
import liedge.ltxindustries.registry.game.LTXIBlocks;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class ElectroCentrifugeRenderer extends LimaBlockEntityRenderer<ElectroCentrifugeBlockEntity>
{
    private final BakedItemLayer tubes;
    private final BakedItemLayer electrodes;

    public ElectroCentrifugeRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context);

        ItemLayerBakedModel model = LimaCoreClientUtil.getCustomBakedModel(LimaCoreClientUtil.inventoryModelPath(LTXIBlocks.ELECTROCENTRIFUGE), ItemLayerBakedModel.class);
        this.tubes = model.getLayer("tubes");
        this.electrodes = model.getLayer("electrodes");
    }

    @Override
    public void render(ElectroCentrifugeBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
    {
        poseStack.pushPose();

        poseStack.translate(0.5d, 0, 0.5d);
        poseStack.mulPose(Axis.YP.rotationDegrees(blockEntity.lerpTubesYRot(partialTick)));
        poseStack.translate(-0.5d, 0, -0.5d);

        tubes.putQuadsInBuffer(poseStack, bufferSource, packedLight);
        electrodes.putQuadsInBuffer(poseStack, bufferSource, packedLight);

        poseStack.popPose();
    }
}