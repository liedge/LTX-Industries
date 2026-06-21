package liedge.ltxindustries.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import liedge.limacore.client.model.StaticQuads;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.blockentity.ElectroCentrifugeBlockEntity;
import liedge.ltxindustries.client.LTXIRenderer;
import liedge.ltxindustries.client.model.LTXIModelPartKeys;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.joml.Vector2f;

public class ElectroCentrifugeRenderer extends SimpleMachineRenderer<ElectroCentrifugeBlockEntity>
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

    private final StaticQuads tubes;

    public ElectroCentrifugeRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context);
        this.tubes = StaticQuads.get(LTXIModelPartKeys.ELECTROCENTRIFUGE_TUBES);
    }

    @Override
    protected void extractAdditional(ElectroCentrifugeBlockEntity blockEntity, State state, float partialTick)
    {
        state.machineSpin = blockEntity.lerpTubesYRot(partialTick);
        state.machineBolt = blockEntity.tubeBolt;
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState camera)
    {
        poseStack.pushPose();

        poseStack.translate(0.5f, 0, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(state.machineSpin));
        poseStack.translate(-0.5f, 0, -0.5f);

        tubes.submit(poseStack, nodeCollector, state.lightCoords);

        poseStack.translate(0f, 0.625f, 0f);

        if (state.machineBolt != null)
        {
            for (Vector2f v : BOLT_POINTS)
            {
                poseStack.translate(v.x, 0f, v.y);
                LTXIRenderer.submitEnergyBolt(poseStack, nodeCollector, RenderTypes.lightning(), state.machineBolt, LTXIConstants.ELECTRIC_GREEN, 1f);
                poseStack.translate(-v.x, 0f, -v.y);
            }
        }

        poseStack.popPose();
    }
}