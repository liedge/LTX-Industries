package liedge.ltxindustries.client.item;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import liedge.limacore.client.gui.FloatingGuiRenderState;
import liedge.limacore.client.gui.LimaGuiUtil;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.lib.TickTimer;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.client.LTXIRenderer;
import liedge.ltxindustries.client.renderer.LTXIRenderPipelines;
import liedge.ltxindustries.item.weapon.StargazerItem;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.lib.weapons.ClientExtendedInput;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public final class StargazerClientItem extends WeaponClientItem
{
    public StargazerClientItem()
    {
        super(1, 7, 5, 7);
    }

    @Override
    public void onMainHandTick(ItemStack stack, WeaponItem weaponItem, ClientExtendedInput controls)
    {
        TickTimer timerB = controls.getAnimationTimerB();
        int triggerTicks = controls.getTicksHoldingTrigger();
        float speed;

        if (timerB.getTimerState() == TickTimer.State.RUNNING)
        {
            speed = 1.25f * LTXIRenderer.sineAnimationCurve(timerB.getProgressPercent());
        }
        else if (triggerTicks > 0)
        {
            speed = 1.25f * Math.min(1f, triggerTicks / (float) StargazerItem.CHARGE_TICKS);
        }
        else
        {
            speed = 0.025f;
        }

        updateSpinAnimation(controls, speed);
    }

    @Override
    protected void extractCrosshairSprites(GuiGraphicsExtractor graphics, RenderPipeline pipeline, LocalPlayer player, WeaponItem weaponItem, ClientExtendedInput controls, int x, int y, LimaColor color, float partialTick)
    {
        int dotY = y + 3;
        float bloom = 5f * triggerCurve(controls, weaponItem, 0.1f, partialTick);

        // Primary crosshair
        blitSprite(graphics, pipeline, ANGLE_BRACKET, x - 6 - bloom, y, 4, 7, color);
        blitSpriteMirrorU(graphics, pipeline, ANGLE_BRACKET, x + 3 + bloom, y, 4, 7, color);
        graphics.fill(x, dotY, x + 1, dotY + 1, LTXIConstants.LIME_GREEN.argb32());

        // Charge arc
        int triggerTicks = controls.getTicksHoldingTrigger();
        if (triggerTicks > 0)
        {
            graphics.submitGuiElementRenderState(new ChargeStops(graphics, x, dotY, LTXIConstants.LIME_GREEN.argb32()));

            float arcLength = Math.min(1f, controls.lerpTriggerTicks(partialTick) / (float) StargazerItem.CHARGE_TICKS);
            float startAngle = 120f - 210f * arcLength;
            float endAngle = 120f;

            GuiElementRenderState arcState = endAngle <= startAngle ? null : new ChargeArc(graphics, x, dotY, startAngle, endAngle, LTXIConstants.LIME_GREEN.argb32());
            if (arcState != null) graphics.submitGuiElementRenderState(arcState);
        }
    }

    private static class ChargeStops extends FloatingGuiRenderState
    {
        private final int x;
        private final int y;
        private final int color;

        ChargeStops(GuiGraphicsExtractor graphics, int x, int y, int color)
        {
            super(RenderPipelines.GUI, TextureSetup.noTexture(), graphics, x - 12, y - 12, x + 12, y + 12);
            this.x = x;
            this.y = y;
            this.color = color;
        }

        @Override
        public void buildVertices(VertexConsumer buffer)
        {
            pose.translate(x, y);
            LimaGuiUtil.putColoredQuad(pose, buffer, 0, -12, 1, -9, color);
            pose.rotate(LimaCoreMath.toRad(210));
            LimaGuiUtil.putColoredQuad(pose, buffer, 0, -12, 1, -9, color);
        }
    }

    private static class ChargeArc extends FloatingGuiRenderState
    {
        private final int x;
        private final int y;
        private final float startAngle;
        private final float endAngle;
        private final int color;

        ChargeArc(GuiGraphicsExtractor graphics, int x, int y, float startAngle, float endAngle, int color)
        {
            super(LTXIRenderPipelines.GUI_TRIANGLES, TextureSetup.noTexture(), graphics, x - 12, y - 12, x + 12, y + 12);
            this.x = x;
            this.y = y;
            this.startAngle = startAngle;
            this.endAngle = endAngle;
            this.color = color;
        }

        @Override
        public void buildVertices(VertexConsumer buffer)
        {
            pose.translate(x, y);

            float arcStart = LimaCoreMath.toRad(startAngle);
            float arcEnd = LimaCoreMath.toRad(endAngle);
            float segLength = LimaCoreMath.toRad(5);
            int segments = Mth.ceil((arcEnd - arcStart) / segLength);

            for (int i = 0; i < segments; i++)
            {
                float a1 = arcStart + i * segLength;
                float a2 = Math.min(arcEnd, arcStart + (i + 1) * segLength);

                if (a2 > a1) submitArcSegment(buffer, a1, a2);
            }
        }

        private void submitArcSegment(VertexConsumer buffer, float a1, float a2)
        {
            float cos1 = Mth.cos(a1);
            float sin1 = Mth.sin(a1);
            float cos2 = Mth.cos(a2);
            float sin2 = Mth.sin(a2);

            final float innerRadius = 10f;
            final float outerRadius = 11f;

            buffer.addVertexWith2DPose(pose, cos1 * innerRadius, sin1 * innerRadius).setColor(color);
            buffer.addVertexWith2DPose(pose, cos2 * innerRadius, sin2 * innerRadius).setColor(color);
            buffer.addVertexWith2DPose(pose, cos1 * outerRadius, sin1 * outerRadius).setColor(color);

            buffer.addVertexWith2DPose(pose, cos1 * outerRadius, sin1 * outerRadius).setColor(color);
            buffer.addVertexWith2DPose(pose, cos2 * innerRadius, sin2 * innerRadius).setColor(color);
            buffer.addVertexWith2DPose(pose, cos2 * outerRadius, sin2 * outerRadius).setColor(color);
        }
    }
}