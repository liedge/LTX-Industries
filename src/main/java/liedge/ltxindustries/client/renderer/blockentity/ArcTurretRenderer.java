package liedge.ltxindustries.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.blockentity.turret.ArcTurretBlockEntity;
import liedge.ltxindustries.client.LTXIRenderUtil;
import liedge.ltxindustries.client.model.custom.EnergyBoltData;
import liedge.ltxindustries.registry.game.LTXIBlocks;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class ArcTurretRenderer extends TurretRenderer<ArcTurretBlockEntity>
{
    public ArcTurretRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context);
    }

    @Override
    protected ItemLike getModelItem()
    {
        return LTXIBlocks.ARC_TURRET;
    }

    @Override
    protected double gunsYPivot()
    {
        return 1.71875d;
    }

    @Override
    protected void renderAdditionalGuns(ArcTurretBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight)
    {
        poseStack.translate(0.5f, 1.62625f, -0.4375f);

        EnergyBoltData primaryBolt = blockEntity.primaryBolt;
        if (primaryBolt != null)
        {
            LTXIRenderUtil.submitEnergyBolt(bufferSource.getBuffer(RenderType.lightning()), poseStack.last().pose(), primaryBolt, LTXIConstants.ELECTRIC_GREEN, 0.9f);
        }
    }

    @Override
    public void render(ArcTurretBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
    {
        super.render(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);

        Vec3 a = blockEntity.chainOffset0;
        Vec3 b = blockEntity.chainOffset;

        if (a != null && b != null)
        {
            poseStack.pushPose();

            double x = Mth.lerp(partialTick, a.x, b.x);
            double y = Mth.lerp(partialTick, a.y, b.y);
            double z = Mth.lerp(partialTick, a.z, b.z);

            poseStack.translate(x, y, z);

            VertexConsumer buffer = bufferSource.getBuffer(RenderType.lightning());
            Matrix4f mx4 = poseStack.last().pose();

            for (EnergyBoltData model : blockEntity.boltChains)
            {
                LTXIRenderUtil.submitEnergyBolt(buffer, mx4, model, LTXIConstants.ELECTRIC_GREEN, 0.6f);
            }


            poseStack.popPose();
        }
    }
}