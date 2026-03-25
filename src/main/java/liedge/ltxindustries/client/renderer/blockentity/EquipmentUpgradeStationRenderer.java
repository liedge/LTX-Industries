package liedge.ltxindustries.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import liedge.ltxindustries.blockentity.EquipmentUpgradeStationBlockEntity;
import liedge.ltxindustries.client.LTXIRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public final class EquipmentUpgradeStationRenderer implements BlockEntityRenderer<EquipmentUpgradeStationBlockEntity, EquipmentUpgradeStationRenderer.StationRenderState>
{
    private final ItemModelResolver itemResolver;

    public EquipmentUpgradeStationRenderer(BlockEntityRendererProvider.Context context)
    {
        this.itemResolver = context.itemModelResolver();
    }

    @Override
    public StationRenderState createRenderState()
    {
        return new StationRenderState();
    }

    @Override
    public void extractRenderState(EquipmentUpgradeStationBlockEntity blockEntity, StationRenderState renderState, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress)
    {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);

        renderState.facing = blockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);

        ItemStack stack = blockEntity.getPreviewItem();
        if (!stack.isEmpty())
        {
            ItemStackRenderState previewItem = new ItemStackRenderState();
            itemResolver.updateForTopItem(previewItem, stack, ItemDisplayContext.FIXED, null, null, 0);
            renderState.previewItem = previewItem;
        }
    }

    @Override
    public void submit(StationRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState)
    {
        ItemStackRenderState previewItem = renderState.previewItem;
        if (previewItem == null) return;

        poseStack.pushPose();

        poseStack.translate(0.5f, 0.8125f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(LTXIRenderer.facingYRotation(renderState.facing)));
        poseStack.mulPose(Axis.XP.rotationDegrees(90f));
        poseStack.scale(0.4375f, 0.4375f, 0.4375f);

        previewItem.submit(poseStack, nodeCollector, renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);

        poseStack.popPose();
    }

    public static class StationRenderState extends BlockEntityRenderState
    {
        Direction facing = Direction.NORTH;
        @Nullable ItemStackRenderState previewItem;
    }
}