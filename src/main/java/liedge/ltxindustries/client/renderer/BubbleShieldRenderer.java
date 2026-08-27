package liedge.ltxindustries.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.lib.TickTimer;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.limacore.util.LimaCollectionsUtil;
import liedge.ltxindustries.client.model.custom.BubbleShieldModel;
import org.jspecify.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.List;
import java.util.stream.IntStream;

public final class BubbleShieldRenderer
{
    private final List<FadeAnimation> animations = new ObjectArrayList<>();
    private @Nullable BubbleShieldModel model;

    public BubbleShieldRenderer()
    {
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        IntList indexes = LimaCollectionsUtil.toIntList(IntStream.range(0, BubbleShieldModel.SHIELD_POLYGON_COUNT));
        IntLists.shuffle(indexes, LimaCoreMath.RANDOM).forEach(deque::push);

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

    public void setModel(@Nullable BubbleShieldModel model)
    {
        this.model = model;
    }

    public void tickRenderer()
    {
        if (model != null) animations.forEach(FadeAnimation::tick);
    }

    public void submit(PoseStack.Pose pose, VertexConsumer buffer, int color, float partialTick)
    {
        if (model != null)
        {
            for (FadeAnimation animation : animations)
            {
                animation.submit(model, pose, buffer, color, partialTick);
            }
        }
    }

    private static class FadeAnimation
    {
        private final int[] geometryIndexes;
        private final TickTimer timer = new TickTimer();

        private FadeAnimation(int[] geometryIndexes)
        {
            this.geometryIndexes = geometryIndexes;
        }

        private void tick()
        {
            timer.tickTimer();
            if (!timer.isRunningClient() && LimaCoreMath.rollRandomChance(0.08d))
            {
                timer.startTimer(20);
            }
        }

        private void submit(BubbleShieldModel model, PoseStack.Pose pose, VertexConsumer buffer, int color, float partialTick)
        {
            float alpha = timer.getTimerState() == TickTimer.State.STOPPED ? 0.125f : LTXIKeyframeTracks.SHIELD_FADE.apply(timer.lerpProgressNotPaused(partialTick));
            model.submitFaces(pose, buffer, geometryIndexes, color, alpha);
        }
    }

    public record State(double yCenter, float yRot, float scale, int color, float partialTick) { }
}