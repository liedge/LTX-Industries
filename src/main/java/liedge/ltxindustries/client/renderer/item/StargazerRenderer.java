package liedge.ltxindustries.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import liedge.limacore.client.model.StaticQuads;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.client.LTXIRenderer;
import liedge.ltxindustries.client.renderer.LTXIRenderTypes;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.util.Util;
import org.joml.Vector3fc;

public final class StargazerRenderer extends WeaponSpecialRenderer
{
    StargazerRenderer(StaticQuads frame, StaticQuads chamber, Vector3fc chamberOrigin, RecoilAnimation recoilAnimation, float recoilDistance, float recoilAngle)
    {
        super(frame, chamber, chamberOrigin, recoilAnimation, recoilDistance, recoilAngle);
    }

    @Override
    protected void submitAdditionalFrame(WeaponRenderState argument, PoseStack poseStack, SubmitNodeCollector nodeCollector, int lightCoords)
    {
        // Render sight
        poseStack.pushPose();

        poseStack.translate(8f * 0.0625f, 14.75f * 0.0625f, 12.75f * 0.0625f);

        nodeCollector.submitCustomGeometry(poseStack, LTXIRenderTypes.WONDERLAND_EPHEMERA, (pose, buffer) ->
        {
            LTXIRenderer.renderArcRing(pose, buffer, 0.05625f, 0.0025f, 0, 360, 24, LTXIConstants.LIME_GREEN);
            LTXIRenderer.renderArcRing(pose, buffer, 0.0078125f, 0.00390625f, 0, 360, 20, LTXIConstants.LIME_GREEN);

            float spin = (Util.getMillis() % 10000L) / 10000f;
            LTXIRenderer.renderArcsRing(pose, buffer, spin * 360f, 3, 55f, 0.00390625f, 0.0625f, 5, LTXIConstants.LIME_GREEN);
        });

        poseStack.popPose();
    }
}