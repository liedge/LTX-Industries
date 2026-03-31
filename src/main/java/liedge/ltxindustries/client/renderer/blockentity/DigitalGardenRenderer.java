package liedge.ltxindustries.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import liedge.ltxindustries.blockentity.DigitalGardenBlockEntity;
import liedge.ltxindustries.client.LTXIRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class DigitalGardenRenderer extends MachineRenderer<DigitalGardenBlockEntity>
{
    public DigitalGardenRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context);
    }

    @Override
    void extractAdditional(DigitalGardenBlockEntity blockEntity, MachineRenderState renderState, float partialTick)
    {
        ItemStack stack = blockEntity.getClientPreviewItem();
        if (stack.isEmpty()) return;

        ItemStackRenderState previewItem = new ItemStackRenderState();
        itemResolver.updateForTopItem(previewItem, stack, ItemDisplayContext.FIXED, null, null, 0);
        renderState.previewItem = previewItem;
    }

    @Override
    public void submit(MachineRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState)
    {
        ItemStackRenderState previewItem = renderState.previewItem;
        if (previewItem == null) return;

        poseStack.pushPose();

        poseStack.translate(0.5f, 0.4375f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(LTXIRenderer.facingYRotation(renderState.facing)));
        poseStack.translate(0, 0, -0.53125f);
        poseStack.scale(0.4375f, 0.4375f, 0.4375f);

        previewItem.submit(poseStack, nodeCollector, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);

        poseStack.popPose();
    }
}