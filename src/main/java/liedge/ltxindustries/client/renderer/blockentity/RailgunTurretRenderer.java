package liedge.ltxindustries.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import liedge.limacore.lib.LimaColor;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.blockentity.RailgunTurretBlockEntity;
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
        return 1.75d;
    }

    @Override
    protected void renderAdditionalGuns(RailgunTurretBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight)
    {
        if (blockEntity.isLookingAtTarget())
        {
            float dist = (float) blockEntity.getTargetDistance() - 0.4375f; // Target distance is measured from z 0.5, subtract difference between laser engine Z
            if (dist > 0)
            {
                VertexConsumer buffer = bufferSource.getBuffer(RenderType.lightning());
                Matrix4f mx4 = poseStack.last().pose();

                // Laser 'engines' y and z: 28.5 and 15 -> 1.78125f, 0.9375f
                // Left laser x: 11.5 (0.71875f) Right laser x: 4.5 (0.28125f)
                float y = 1.78125f;
                float z = 0.0625f;
                float lx = 0.71875f;
                float rx = 0.28125f;

                LimaColor color = blockEntity.lerpTicker(partialTick, 37) >= 1 ? LTXIConstants.LIME_GREEN : LTXIConstants.HOSTILE_ORANGE;

                LTXIRenderUtil.renderPositionColorCuboid(buffer, mx4, lx - LASER_RADIUS, y - LASER_RADIUS, z, lx + LASER_RADIUS, y + LASER_RADIUS, z - dist, color, 0.8f, LTXIRenderUtil.ALL_SIDES);
                LTXIRenderUtil.renderPositionColorCuboid(buffer, mx4, rx - LASER_RADIUS, y - LASER_RADIUS, z, rx + LASER_RADIUS, y + LASER_RADIUS, z - dist, color, 0.8f, LTXIRenderUtil.ALL_SIDES);
            }
        }
    }
}