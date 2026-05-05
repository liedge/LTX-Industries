package liedge.ltxindustries.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import liedge.ltxindustries.blockentity.BaseFabricatorBlockEntity;
import liedge.ltxindustries.client.LTXIRenderer;
import liedge.ltxindustries.client.renderer.WireframeNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public final class BaseFabricatorRenderer extends MachineRenderer<BaseFabricatorBlockEntity>
{
    private final double xOffset;
    private final double yOffset;

    public BaseFabricatorRenderer(BlockEntityRendererProvider.Context context, double xOffset, double yOffset)
    {
        super(context);
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    @Override
    void extractAdditional(BaseFabricatorBlockEntity blockEntity, MachineRenderState renderState, float partialTick)
    {
        ItemStack stack = blockEntity.getClientPreviewItem();
        if (stack.isEmpty()) return;

        ItemStackRenderState previewItem = new ItemStackRenderState();
        itemResolver.updateForTopItem(previewItem, stack, ItemDisplayContext.FIXED, null, null, 0);
        renderState.previewItem = previewItem;
        renderState.active = blockEntity.isCrafting();
    }

    @Override
    public void submit(MachineRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState)
    {
        ItemStackRenderState previewItem = renderState.previewItem;
        if (previewItem == null) return;

        poseStack.pushPose();

        poseStack.translate(0.5d, yOffset, 0.5d);
        poseStack.mulPose(Axis.YP.rotationDegrees(LTXIRenderer.facingYRotation(renderState.facing)));
        poseStack.mulPose(Axis.XP.rotationDegrees(90));
        poseStack.translate(xOffset, 0, 0);
        poseStack.scale(0.4375f, 0.4375f, 0.4375f);

        if (renderState.active) nodeCollector = new WireframeNodeCollector(nodeCollector);
        previewItem.submit(poseStack, nodeCollector, renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);

        poseStack.popPose();
    }
}