package liedge.ltxindustries.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import liedge.limacore.client.LimaBlockEntityRenderer;
import liedge.ltxindustries.blockentity.EquipmentUpgradeStationBlockEntity;
import liedge.ltxindustries.client.LTXIRenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class EquipmentUpgradeStationRenderer extends LimaBlockEntityRenderer<EquipmentUpgradeStationBlockEntity>
{
    public EquipmentUpgradeStationRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context);
    }

    @Override
    public void render(EquipmentUpgradeStationBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
    {
        ItemStack previewItem = blockEntity.getPreviewItem();
        if (!previewItem.isEmpty())
        {
            poseStack.pushPose();

            poseStack.translate(0.5d, 0.8125d, 0.5d);

            Direction facing = blockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
            poseStack.mulPose(Axis.YP.rotationDegrees(LTXIRenderUtil.facingYRotation(facing)));
            poseStack.mulPose(Axis.XP.rotationDegrees(90f));
            poseStack.scale(0.4375f, 0.4375f, 0.4375f);

            itemRenderer.renderStatic(previewItem, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, bufferSource, Minecraft.getInstance().level, 0);

            poseStack.popPose();
        }
    }
}