package liedge.limatech.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import liedge.limacore.client.model.baked.BakedItemLayer;
import liedge.limacore.client.model.baked.LimaLayerBakedModel;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.util.LimaMathUtil;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.client.LimaTechRenderUtil;
import liedge.limatech.client.model.baked.BakedRotation;
import liedge.limatech.client.model.custom.TranslucentFillModel;
import liedge.limatech.client.renderer.LimaTechRenderTypes;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.lib.weapons.ClientWeaponControls;
import liedge.limatech.registry.game.LimaTechItems;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public class LinearFusionRenderProperties extends SimpleWeaponRenderProperties
{
    private final TranslucentFillModel magazineFillModel = TranslucentFillModel.create(7.01f, -0.49f, 4.76f, 8.99f, 6.49f, 7.74f, Direction.Axis.Y,
            BakedRotation.fromAxisAngle(8f, 6.5f, 8f, -22.5f, Direction.Axis.X));

    private BakedItemLayer barrelGlassLayer;

    LinearFusionRenderProperties()
    {
        super(5f, 0.6f, 4);
    }

    @Override
    protected void loadWeaponModelParts(WeaponItem item, LimaLayerBakedModel model)
    {
        barrelGlassLayer = model.getLayer("barrel glass");
    }

    @Override
    protected void renderModelLayers(PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay)
    {
        super.renderModelLayers(poseStack, bufferSource, light, overlay);
        barrelGlassLayer.putQuadsInBuffer(poseStack, bufferSource, light);
    }

    @Override
    protected WeaponItem getRenderableItem()
    {
        return LimaTechItems.LINEAR_FUSION_RIFLE.get();
    }

    @Override
    protected TranslucentFillModel getMagazineFillModel()
    {
        return magazineFillModel;
    }

    @Override
    protected float applyAnimationCurve(float recoilA)
    {
        return LimaTechRenderUtil.animationCurveA(recoilA);
    }

    @Override
    public void renderCrosshair(LocalPlayer player, WeaponItem weaponItem, ClientWeaponControls controls, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight, LimaColor crosshairColor)
    {
        final int centerX = (screenWidth - 1) / 2;
        final int centerY = (screenHeight - 1) / 2;
        final int bracketCenterY = (screenHeight - 7) / 2;

        float bloom = 5f * LimaTechRenderUtil.animationCurveA(controls.lerpTriggerTimer(weaponItem, partialTicks));

        blitSprite(graphics, centerX - 6 - bloom, bracketCenterY, 4, 7, crosshairColor, ANGLE_BRACKET);
        blitMirroredUSprite(graphics, centerX + 3 + bloom, bracketCenterY, 4, 7, crosshairColor, ANGLE_BRACKET);

        graphics.fill(RenderType.gui(), centerX, centerY, centerX + 1, centerY + 1, LimaTechConstants.LIME_GREEN.argb32());

        int triggerTicks = controls.getTicksHoldingTrigger();
        if (triggerTicks > 0)
        {
            float f = Math.min(1f, controls.lerpTriggerTicks(partialTicks) / 10f);
            renderCrosshairChargeArc(graphics, centerX, centerY, 10, 11, f, LimaTechConstants.LIME_GREEN);
            renderCrosshairChargeStops(graphics, centerX, centerY, 9, 12, 0.5f, 90, LimaTechConstants.LIME_GREEN);
            renderCrosshairChargeStops(graphics, centerX, centerY, 9, 12, 0.5f, 240, LimaTechConstants.LIME_GREEN);
        }
    }

    private void renderCrosshairChargeStops(GuiGraphics graphics, int centerX, int centerY, int startRadius, int endRadius, float barHalfWidth, float angleDegrees, LimaColor color)
    {
        VertexConsumer buffer = graphics.bufferSource().getBuffer(RenderType.gui());
        Matrix4f mx4 = graphics.pose().last().pose();
        float rad = LimaMathUtil.toRad(angleDegrees);

        float dx = Mth.cos(rad);
        float dy = -Mth.sin(rad);

        float ix = centerX + dx * startRadius;
        float iy = centerY + dy * startRadius;
        float ox = centerX + dx * endRadius;
        float oy = centerY + dy * endRadius;

        float offsetX = -dy * barHalfWidth * 2f;
        float offsetY = dx * barHalfWidth;

        buffer.addVertex(mx4, ix, iy - offsetY, 0).setColor(color.red(), color.green(), color.blue(), 1f);
        buffer.addVertex(mx4, ix + offsetX, iy + offsetY, 0).setColor(color.red(), color.green(), color.blue(), 1f);
        buffer.addVertex(mx4, ox + offsetX, oy + offsetY, 0).setColor(color.red(), color.green(), color.blue(), 1f);
        buffer.addVertex(mx4, ox, oy - offsetY, 0).setColor(color.red(), color.green(), color.blue(), 1f);
    }

    private void renderCrosshairChargeArc(GuiGraphics graphics, int centerX, int centerY, int innerRadius, int outerRadius, float arcLength, LimaColor color)
    {
        VertexConsumer buffer = graphics.bufferSource().getBuffer(LimaTechRenderTypes.GUI_TRIANGLE_STRIP);
        Matrix4f mx4 = graphics.pose().last().pose();

        float endAngle = -120f + (210f * arcLength);
        for (float a = -120f; a <= endAngle; a += 5)
        {
            float rad = LimaMathUtil.toRad(a);
            float cos = Mth.cos(rad);
            float sin = Mth.sin(rad);

            buffer.addVertex(mx4, centerX + cos * innerRadius, centerY - sin * innerRadius, 0).setColor(color.red(), color.green(), color.blue(), 1f);
            buffer.addVertex(mx4, centerX + cos * outerRadius, centerY - sin * outerRadius, 0).setColor(color.red(), color.green(), color.blue(), 1f);
        }
    }
}