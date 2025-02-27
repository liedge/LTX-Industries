package liedge.limatech.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import liedge.limacore.client.EmptyVertexConsumer;
import liedge.limacore.client.LimaBlockEntityRenderer;
import liedge.limatech.blockentity.FabricatorBlockEntity;
import liedge.limatech.client.renderer.LimaTechRenderTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class FabricatorRenderer extends LimaBlockEntityRenderer<FabricatorBlockEntity>
{
    private static final Set<VertexFormat> VALID_WIREFRAME_FORMATS = Set.of(
            DefaultVertexFormat.BLOCK,
            DefaultVertexFormat.NEW_ENTITY,
            DefaultVertexFormat.POSITION_TEX_COLOR);

    public FabricatorRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context);
    }

    @Override
    public void render(FabricatorBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay)
    {
        ItemStack previewItem = blockEntity.getPreviewItem();

        if (!previewItem.isEmpty())
        {
            poseStack.pushPose();

            Direction facing = blockEntity.getBlockState().getValue(HORIZONTAL_FACING);

            poseStack.translate(0.5d, 0.25d, 0.5d);

            float angle = switch (facing)
            {
                case SOUTH -> 180f;
                case EAST -> -90f;
                case WEST -> 90f;
                default -> 0f;
            };

            poseStack.mulPose(Axis.YP.rotationDegrees(angle));
            poseStack.mulPose(Axis.XP.rotationDegrees(90f));
            poseStack.scale(0.4375f, 0.4375f, 0.4375f);

            MultiBufferSource wireframeBufferSource = renderType -> {
                if (blockEntity.isCrafting())
                {
                    return VALID_WIREFRAME_FORMATS.contains(renderType.format()) ? bufferSource.getBuffer(LimaTechRenderTypes.FABRICATOR_WIREFRAME) : EmptyVertexConsumer.EMPTY_VERTEX_CONSUMER;
                }
                else
                {
                    return bufferSource.getBuffer(renderType);
                }
            };

            itemRenderer.renderStatic(previewItem, ItemDisplayContext.FIXED, light, overlay, poseStack, wireframeBufferSource, Minecraft.getInstance().level, 0);

            poseStack.popPose();
        }
    }
}