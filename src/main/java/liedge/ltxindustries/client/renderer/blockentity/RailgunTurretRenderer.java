package liedge.ltxindustries.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import liedge.limacore.lib.LimaColor;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.blockentity.turret.RailgunTurretBlockEntity;
import liedge.ltxindustries.client.LTXIRenderUtil;
import liedge.ltxindustries.registry.game.LTXIBlocks;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.ItemLike;
import org.joml.Matrix4f;

public class RailgunTurretRenderer extends TurretRenderer<RailgunTurretBlockEntity>
{
    private static final float LASER_RADIUS = 0.015625f;

    public RailgunTurretRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context);
    }

    @Override
    protected ItemLike getModelItem()
    {
        return LTXIBlocks.RAILGUN_TURRET;
    }

    @Override
    protected double gunsYPivot()
    {
        return 1.6875d;
    }

    @Override
    protected void renderAdditionalGuns(RailgunTurretBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight)
    {
        if (blockEntity.isLookingAtTarget())
        {
            float dist = (float) blockEntity.getTargetDistance() - 0.4375f;
            if (dist > 0)
            {
                VertexConsumer buffer = bufferSource.getBuffer(RenderType.lightning());
                Matrix4f mx4 = poseStack.last().pose();

                float x = 0.5f;
                float y = 1.6875f;
                float z = 0.0625f;

                LimaColor color = blockEntity.lerpAimTicks(partialTick, 25) >= 1 ? LTXIConstants.LIME_GREEN : LTXIConstants.HOSTILE_ORANGE;
                LTXIRenderUtil.submitUnlitCuboid(LTXIRenderUtil.ALL_SIDES, buffer, mx4, x - LASER_RADIUS, y - LASER_RADIUS, z, x + LASER_RADIUS, y + LASER_RADIUS, z - dist, color, 0.8f);
            }
        }
    }
}