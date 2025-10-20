package liedge.ltxindustries.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import liedge.limacore.client.LimaBlockEntityRenderer;
import liedge.ltxindustries.blockentity.DigitalGardenBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class DigitalGardenRenderer extends LimaBlockEntityRenderer<DigitalGardenBlockEntity>
{
    public DigitalGardenRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context);
    }

    @Override
    public void render(DigitalGardenBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
    {
        ItemStack previewItem = blockEntity.getPreviewItem();

        if (!previewItem.isEmpty())
        {
            poseStack.pushPose();

            Direction facing = blockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);

            float angle = switch (facing)
            {
                case SOUTH -> 180f;
                case EAST -> -90f;
                case WEST -> 90f;
                default -> 0f;
            };

            poseStack.translate(0.5d, 0.4375d, 0.5d);
            poseStack.mulPose(Axis.YP.rotationDegrees(angle));
            poseStack.translate(0, 0, -0.53125d);
            poseStack.scale(0.4375f, 0.4375f, 0.4375f);

            itemRenderer.renderStatic(previewItem, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, bufferSource, Minecraft.getInstance().level, 0);

            poseStack.popPose();
        }
    }
}