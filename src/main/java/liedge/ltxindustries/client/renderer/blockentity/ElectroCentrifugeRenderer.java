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

public class ElectroCentrifugeRenderer extends MachineRenderer<ElectroCentrifugeBlockEntity>
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
    void extractAdditional(ElectroCentrifugeBlockEntity blockEntity, MachineRenderState renderState, float partialTick)
    {
        renderState.machineSpin = blockEntity.lerpTubesYRot(partialTick);
        renderState.machineBolt = blockEntity.tubeBolt;
    }

    @Override
    public void submit(MachineRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState)
    {
        poseStack.pushPose();

        poseStack.translate(0.5f, 0, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(renderState.machineSpin));
        poseStack.translate(-0.5f, 0, -0.5f);

        tubes.submit(poseStack, nodeCollector, renderState.lightCoords);

        poseStack.translate(0f, 0.625f, 0f);

        if (renderState.machineBolt != null)
        {
            for (Vector2f v : BOLT_POINTS)
            {
                poseStack.translate(v.x, 0f, v.y);
                LTXIRenderer.submitEnergyBolt(poseStack, nodeCollector, RenderTypes.lightning(), renderState.machineBolt, LTXIConstants.ELECTRIC_GREEN, 1f);
                poseStack.translate(-v.x, 0f, -v.y);
            }
        }

        poseStack.popPose();
    }
}