package liedge.ltxindustries.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import liedge.limacore.client.LimaBlockEntityRenderer;
import liedge.ltxindustries.blockentity.BaseECABlockEntity;
import liedge.ltxindustries.client.model.custom.TranslucentFillModel;
import liedge.ltxindustries.client.renderer.LTXIRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

public class EnergyCellArrayRenderer extends LimaBlockEntityRenderer<BaseECABlockEntity>
{
    private static TranslucentFillModel fillModel(float x, float y, float z)
    {
        float x2 = x + 4 - 0.01f;
        float z2 = z + 4 - 0.01f;
        return TranslucentFillModel.create(x + 0.01f, y + 0.01f, z + 0.01f, x2, y + 9, z2, Direction.Axis.Y);
    }

    private final TranslucentFillModel[] fillModels = new TranslucentFillModel[]
            {
                    fillModel(2, 2.5f, 2),
                    fillModel(10, 2.5f, 2),
                    fillModel(2, 2.5f, 10),
                    fillModel(10, 2.5f, 10)
            };

    public EnergyCellArrayRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context);
    }

    @Override
    public void render(BaseECABlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
    {
        float fill = blockEntity.getRemoteEnergyFill();
        if (fill > 0)
        {
            VertexConsumer buffer = bufferSource.getBuffer(LTXIRenderTypes.POSITION_COLOR_QUADS);

            for (TranslucentFillModel model : fillModels)
            {
                model.render(buffer, poseStack, blockEntity.getRemoteEnergyFillColor(), fill);
            }
        }
    }
}