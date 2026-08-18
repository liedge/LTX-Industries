package liedge.ltxindustries.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import liedge.limacore.client.model.StaticQuads;
import liedge.ltxindustries.blockentity.MaterialPressBlockEntity;
import liedge.ltxindustries.client.model.LTXIModelPartKeys;
import liedge.ltxindustries.client.renderer.LinearAnimation;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.EasingType;

public class MaterialPressRenderer extends SimpleMachineRenderer<MaterialPressBlockEntity>
{
    private static final LinearAnimation ANIMATION = LinearAnimation.builder()
            .with(0f, 0f, 1f, EasingType.OUT_BOUNCE)
            .with(0.25f, 1f, 1f, EasingType.CONSTANT)
            .with(0.5f, 1f, 0f, EasingType.IN_SINE)
            .with(0.825f, 0f, 0f, EasingType.CONSTANT)
            .build();

    private final StaticQuads hammer;

    public MaterialPressRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context);
        this.hammer = StaticQuads.get(LTXIModelPartKeys.MATERIAL_PRESS_HAMMER);
    }

    @Override
    protected void extractAdditional(MaterialPressBlockEntity blockEntity, State state, float partialTick)
    {
        float delta = blockEntity.animationTimer.lerpProgressNotPaused(partialTick);
        state.machineMotion = ANIMATION.apply(delta) * -0.4375f;
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState camera)
    {
        poseStack.pushPose();

        poseStack.translate(0, state.machineMotion, 0);

        hammer.submit(poseStack, nodeCollector, state.lightCoords);

        poseStack.popPose();
    }
}