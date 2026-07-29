package liedge.ltxindustries.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import liedge.limacore.client.model.StaticQuads;
import liedge.ltxindustries.blockentity.HydroSieveBlockEntity;
import liedge.ltxindustries.client.model.LTXIModelPartKeys;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;

public final class HydroSieveRenderer extends SimpleMachineRenderer<HydroSieveBlockEntity>
{
    private final StaticQuads impeller;

    public HydroSieveRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context);
        this.impeller = StaticQuads.get(LTXIModelPartKeys.HYDROSIEVE_IMPELLER);
    }

    @Override
    protected void extractAdditional(HydroSieveBlockEntity blockEntity, State state, float partialTick)
    {
        state.machineSpin = blockEntity.lerpRotation(partialTick);
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState camera)
    {
        poseStack.pushPose();

        poseStack.translate(0.5f, 0f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(state.machineSpin));
        poseStack.translate(-0.5f, 0f, -0.5f);

        impeller.submit(poseStack, nodeCollector, state.lightCoords);

        poseStack.popPose();
    }
}