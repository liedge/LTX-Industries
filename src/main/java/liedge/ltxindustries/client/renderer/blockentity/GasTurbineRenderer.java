package liedge.ltxindustries.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import liedge.limacore.client.model.StaticQuads;
import liedge.ltxindustries.blockentity.GasTurbineBlockEntity;
import liedge.ltxindustries.client.LTXIRenderer;
import liedge.ltxindustries.client.model.LTXIModelPartKeys;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;

public class GasTurbineRenderer extends SimpleMachineRenderer<GasTurbineBlockEntity>
{
    private final StaticQuads blades;

    public GasTurbineRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context);
        this.blades = StaticQuads.get(LTXIModelPartKeys.GAS_TURBINE_BLADES);
    }

    @Override
    protected void extractAdditional(GasTurbineBlockEntity blockEntity, SimpleMachineRenderer.State state, float partialTick)
    {
        state.machineMotion = blockEntity.lerpRotation(partialTick);
    }

    @Override
    public void submit(SimpleMachineRenderer.State state, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState camera)
    {
        poseStack.pushPose();

        poseStack.translate(0.5f, 0.5f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(LTXIRenderer.facingYRotation(state.facing) - 90f));
        poseStack.translate(0.5f, -0.5f, -0.5f);

        poseStack.translate(0.5f, 1f, 0.5f);
        poseStack.mulPose(Axis.ZP.rotationDegrees(state.machineMotion));
        poseStack.translate(-0.5f, -1f, -0.5f);

        blades.submit(poseStack, nodeCollector, state.lightCoords);

        poseStack.popPose();
    }

    @Override
    public AABB getRenderBoundingBox(GasTurbineBlockEntity blockEntity)
    {
        BlockPos pos = blockEntity.getBlockPos();
        Direction zAxis = blockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        Direction xAxis = zAxis.getCounterClockWise();

        int x1 = xAxis.getStepX() * -1;
        int z1 = xAxis.getStepZ() * -1;
        int x2 = xAxis.getStepX() + zAxis.getStepX() * -2;
        int z2 = xAxis.getStepZ() + zAxis.getStepZ() * -2;

        return new AABB(pos.getX() + Math.min(x1, x2), pos.getY(), pos.getZ() + Math.min(z1, z2),
                pos.getX() + Math.max(x1, x2) + 1, pos.getY() + 2, pos.getZ() + Math.max(z1, z2) + 1);
    }
}