package liedge.ltxindustries.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.ltxindustries.blockentity.BaseFabricatorBlockEntity;
import liedge.ltxindustries.client.LTXIRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.List;

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

        WireframeItemRenderState previewItem = new WireframeItemRenderState();
        itemResolver.updateForTopItem(previewItem, stack, ItemDisplayContext.FIXED, null, null, 0);
        renderState.previewItem = previewItem;
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

        previewItem.submit(poseStack, nodeCollector, renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);

        poseStack.popPose();
    }

    private static class WireframeItemRenderState extends ItemStackRenderState
    {
        private final List<LayerRenderState> trackedLayers = new ObjectArrayList<>();

        @Override
        public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, int packedOverlay, int outlineColor)
        {
            for (LayerRenderState layer : trackedLayers)
            {
                // TODO Find a way to return this. Mixin?
                //layer.setRenderType(LTXIRenderTypes.FABRICATOR_WIREFRAME);
            }

            super.submit(poseStack, nodeCollector, packedLight, packedOverlay, outlineColor);
        }

        @Override
        public LayerRenderState newLayer()
        {
            LayerRenderState layer = super.newLayer();
            trackedLayers.add(layer);
            return layer;
        }

        @Override
        public void clear()
        {
            super.clear();
            trackedLayers.clear();
        }
    }
}