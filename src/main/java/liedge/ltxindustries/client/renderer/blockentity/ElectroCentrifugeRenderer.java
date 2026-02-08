package liedge.ltxindustries.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import liedge.limacore.client.LimaBlockEntityRenderer;
import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.client.model.baked.BakedItemLayer;
import liedge.limacore.client.model.baked.ItemLayerBakedModel;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.blockentity.ElectroCentrifugeBlockEntity;
import liedge.ltxindustries.client.LTXIRenderUtil;
import liedge.ltxindustries.client.model.custom.EnergyBoltData;
import liedge.ltxindustries.registry.game.LTXIBlocks;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.joml.Vector2f;

public class ElectroCentrifugeRenderer extends LimaBlockEntityRenderer<ElectroCentrifugeBlockEntity>
{
    private static Vector2f boltPoint(float x, float z)
    {
        return new Vector2f(x / 16f, z / 16f);
    }

    private static final Vector2f[] BOLT_POINTS = new Vector2f[] {
            boltPoint(8, 3.75f),
            boltPoint(5, 5),
            boltPoint(3.75f, 8),
            boltPoint(5, 11),
            boltPoint(8, 12.25f),
            boltPoint(11, 11),
            boltPoint(12.25f, 8),
            boltPoint(11, 5)
    };

    private final BakedItemLayer tubes;
    private final BakedItemLayer lights;

    public ElectroCentrifugeRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context);

        ItemLayerBakedModel model = LimaCoreClientUtil.getCustomBakedModel(LimaCoreClientUtil.inventoryModelPath(LTXIBlocks.ELECTROCENTRIFUGE), ItemLayerBakedModel.class);
        this.tubes = model.getLayer("tubes");
        this.lights = model.getLayer("tube lights");
    }

    @Override
    public void render(ElectroCentrifugeBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
    {
        poseStack.pushPose();

        poseStack.translate(0.5d, 0, 0.5d);
        poseStack.mulPose(Axis.YP.rotationDegrees(blockEntity.lerpTubesYRot(partialTick)));
        poseStack.translate(-0.5d, 0, -0.5d);

        tubes.putQuadsInBuffer(poseStack, bufferSource, packedLight);
        lights.putQuadsInBuffer(poseStack, bufferSource, packedLight);

        poseStack.translate(0f, 0.625f, 0f);
        EnergyBoltData bolt = blockEntity.tubeBolt;
        if (bolt != null)
        {
            VertexConsumer buffer = bufferSource.getBuffer(RenderType.lightning());

            for (Vector2f v : BOLT_POINTS)
            {
                poseStack.translate(v.x, 0f, v.y);
                LTXIRenderUtil.submitEnergyBolt(buffer, poseStack.last().pose(), bolt, LTXIConstants.ELECTRIC_GREEN, 1f);
                poseStack.translate(-v.x, 0f, -v.y);
            }
        }

        poseStack.popPose();
    }
}