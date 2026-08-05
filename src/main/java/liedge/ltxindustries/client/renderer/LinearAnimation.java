package liedge.ltxindustries.client.renderer;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.EasingType;
import net.minecraft.util.Mth;

import java.util.List;

public final class LinearAnimation
{
    public static LinearAnimation.Builder builder()
    {
        return new Builder();
    }

    private final Keyframe[] keyframes;

    private LinearAnimation(Keyframe[] keyframes)
    {
        this.keyframes = keyframes;
    }

    public float apply(float time)
    {
        int prev = Math.max(0, Mth.binarySearch(0, keyframes.length, i -> time <= keyframes[i].timestamp) - 1);
        int next = Math.min(keyframes.length - 1, prev + 1);

        Keyframe prevFrame = keyframes[prev];
        Keyframe nextFrame = keyframes[next];

        float alpha = prev != next ? (time - prevFrame.timestamp) / (nextFrame.timestamp - prevFrame.timestamp) : 0;
        return prevFrame.lerp(prevFrame.easing.apply(alpha));
    }

    public static final class Builder
    {
        private final List<Keyframe> keyframes = new ObjectArrayList<>();

        private Builder() { }

        public Builder with(float timestamp, float start, float end, EasingType easing)
        {
            Preconditions.checkArgument(timestamp >= 0 && timestamp <= 1f, "Keyframe timestamp out of range [0, 1]");
            keyframes.add(new Keyframe(timestamp, start, end, easing));
            return this;
        }

        public LinearAnimation build()
        {
            return new LinearAnimation(keyframes.toArray(Keyframe[]::new));
        }
    }

    private record Keyframe(float timestamp, float start, float end, EasingType easing)
    {
        private float lerp(float alpha)
        {
            return Mth.lerp(alpha, start, end);
        }
    }
}