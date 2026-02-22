package liedge.ltxindustries.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import liedge.limacore.client.LimaBlockEntityRenderer;
import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.client.model.baked.BakedItemLayer;
import liedge.limacore.client.model.baked.ItemLayerBakedModel;
import liedge.ltxindustries.blockentity.GrinderBlockEntity;
import liedge.ltxindustries.client.LTXIRenderUtil;
import liedge.ltxindustries.registry.game.LTXIBlocks;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class GrinderRenderer extends LimaBlockEntityRenderer<GrinderBlockEntity>
{
    private final BakedItemLayer frontCrusher;
    private final BakedItemLayer backCrusher;

    public GrinderRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context);

        ItemLayerBakedModel model = LimaCoreClientUtil.getCustomBakedModel(LimaCoreClientUtil.inventoryModelPath(LTXIBlocks.GRINDER), ItemLayerBakedModel.class);
        this.frontCrusher = model.getLayer("front crusher");
        this.backCrusher = model.getLayer("back crusher");
    }

    @Override
    public void render(GrinderBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
    {
        poseStack.pushPose();

        // Orient crushers to block facing
        poseStack.translate(0.5d, 0, 0.5d);
        poseStack.mulPose(Axis.YP.rotationDegrees(LTXIRenderUtil.facingYRotation(blockEntity.getFacing())));
        poseStack.translate(-0.5d, 0, -0.5d);

        final float crusherRot = blockEntity.lerpCrushersRot(partialTick);

        // Rotate and render front crusher
        poseStack.pushPose();
        poseStack.translate(0.5f, 0.625f, 0.375f);
        poseStack.mulPose(Axis.XP.rotationDegrees(crusherRot));
        poseStack.translate(-0.5f, -0.625f, -0.375f);

        frontCrusher.putQuadsInBuffer(poseStack, bufferSource, packedLight);
        poseStack.popPose();

        // Rotate and render back crusher
        poseStack.pushPose();
        poseStack.translate(0.5f, 0.625f, 0.625f);
        poseStack.mulPose(Axis.XN.rotationDegrees(crusherRot));
        poseStack.translate(-0.5f, -0.625f, -0.625f);

        backCrusher.putQuadsInBuffer(poseStack, bufferSource, packedLight);
        poseStack.popPose();

        poseStack.popPose();
    }
}