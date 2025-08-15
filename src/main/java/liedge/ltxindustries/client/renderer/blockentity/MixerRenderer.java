package liedge.ltxindustries.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import liedge.limacore.client.LimaBlockEntityRenderer;
import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.client.model.baked.BakedItemLayer;
import liedge.limacore.client.model.baked.ItemLayerBakedModel;
import liedge.ltxindustries.blockentity.MixerBlockEntity;
import liedge.ltxindustries.registry.game.LTXIBlocks;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class MixerRenderer extends LimaBlockEntityRenderer<MixerBlockEntity>
{
    private final BakedItemLayer impeller;

    public MixerRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context);

        ItemLayerBakedModel model = LimaCoreClientUtil.getCustomBakedModel(LimaCoreClientUtil.inventoryModelPath(LTXIBlocks.MIXER), ItemLayerBakedModel.class);
        this.impeller = model.getLayer("impeller");
    }

    @Override
    public void render(MixerBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
    {
        poseStack.pushPose();

        poseStack.translate(0.5d, 0, 0.5d);
        poseStack.mulPose(Axis.YP.rotationDegrees(blockEntity.lerpImpellerYRot(partialTick)));
        poseStack.translate(-0.5d, 0, -0.5d);

        impeller.putQuadsInBuffer(poseStack, bufferSource, packedLight);

        poseStack.popPose();
    }
}