package liedge.ltxindustries.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.lib.TickTimer;
import liedge.limacore.util.LimaCollectionsUtil;
import liedge.limacore.util.LimaMathUtil;
import liedge.ltxindustries.client.LTXIRenderUtil;
import liedge.ltxindustries.client.model.custom.BubbleShieldModel;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

import java.util.ArrayDeque;
import java.util.List;
import java.util.stream.IntStream;

public final class BubbleShieldRenderer
{
    public static final BubbleShieldRenderer SHIELD_RENDERER = new BubbleShieldRenderer();

    private final List<FadeAnimation> animations = new ObjectArrayList<>();

    private BubbleShieldRenderer()
    {
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        IntList indexes = LimaCollectionsUtil.toIntList(IntStream.range(0, BubbleShieldModel.SHIELD_POLYGON_COUNT));
        IntLists.shuffle(indexes, LimaMathUtil.RANDOM).forEach(deque::push);

        for (int i = 0; i < 16; i++)
        {
            final int n = Math.min(deque.size(), 8);
            int[] geometryIndexes = new int[n];

            for (int j = 0; j < n; j++)
            {
                geometryIndexes[j] = deque.pop();
            }

            animations.add(new FadeAnimation(geometryIndexes));
        }
    }

    public void tickRenderer()
    {
        animations.forEach(FadeAnimation::tick);
    }

    public void renderBubbleShield(PoseStack poseStack, VertexConsumer buffer, LimaColor color, float partialTick)
    {
        Matrix4f mx4 = poseStack.last().pose();
        animations.forEach(animation -> animation.putInBuffer(buffer, mx4, color, partialTick));
    }

    private static class FadeAnimation
    {
        private final int[] geometryIndexes;
        private final TickTimer animationTimer = new TickTimer();

        private FadeAnimation(int[] geometryIndexes)
        {
            this.geometryIndexes = geometryIndexes;
        }

        private void tick()
        {
            animationTimer.tickTimer();
            if (animationTimer.getTimerState() == TickTimer.State.STOPPED && LimaMathUtil.rollRandomChance(0.08d))
            {
                animationTimer.startTimer(LimaMathUtil.nextIntBetweenInclusive(12, 18), false);
            }
        }

        private void putInBuffer(VertexConsumer buffer, Matrix4f mx4, LimaColor color, float partialTick)
        {
            float alpha = animationTimer.getTimerState() == TickTimer.State.STOPPED ? 0.125f : Mth.clamp(LTXIRenderUtil.animationCurveC(animationTimer.lerpPausedProgress(partialTick)), 0.125f, 0.8f);
            BubbleShieldModel.SHIELD_MODEL.renderFaces(geometryIndexes, buffer, mx4, color, alpha);
        }
    }
}