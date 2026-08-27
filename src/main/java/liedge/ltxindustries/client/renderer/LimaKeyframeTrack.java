package liedge.ltxindustries.client.renderer;

import com.google.common.collect.Comparators;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.floats.FloatOpenHashSet;
import it.unimi.dsi.fastutil.floats.FloatSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.EasingType;
import net.minecraft.util.Mth;

import java.util.Comparator;
import java.util.List;

public final class LimaKeyframeTrack
{
    public static final Codec<LimaKeyframeTrack> CODEC = LimaKeyframe.CODEC.listOf().xmap(LimaKeyframeTrack::new, o -> o.keyframes).validate(LimaKeyframeTrack::validate);

    public static LimaKeyframeTrack.Builder builder()
    {
        return new LimaKeyframeTrack.Builder();
    }

    private final List<LimaKeyframe> keyframes;

    private LimaKeyframeTrack(List<LimaKeyframe> keyframes)
    {
        this.keyframes = keyframes;
    }

    public float apply(float time)
    {
        int prev = Math.max(0, Mth.binarySearch(0, keyframes.size(), i -> time <= keyframes.get(i).time()) - 1);
        int next = Math.min(keyframes.size() - 1, prev + 1);

        LimaKeyframe prevFrame = keyframes.get(prev);

        if (prev == next)
        {
            return prevFrame.value();
        }
        else
        {
            LimaKeyframe nextFrame = keyframes.get(next);

            float prevTime = prevFrame.time();
            float nextTime = nextFrame.time();
            float alpha = (time - prevTime) / (nextTime - prevTime);

            return Mth.lerp(prevFrame.easing().apply(alpha), prevFrame.value(), nextFrame.value());
        }
    }

    private DataResult<LimaKeyframeTrack> validate()
    {
        if (keyframes.isEmpty())
            return DataResult.error(() -> "Keyframe track has no keyframes.");
        else if (!Comparators.isInOrder(keyframes, Comparator.naturalOrder()))
            return DataResult.error(() -> "Keyframe times not in order.");
        else if (keyframes.size() > 1)
        {
            FloatSet set = new FloatOpenHashSet();
            for (LimaKeyframe keyframe : keyframes)
            {
                if (!set.add(keyframe.time()))
                    return DataResult.error(() -> "Duplicate keyframe time " + keyframe.time());
            }
        }

        return DataResult.success(this);
    }

    // Builder
    public static final class Builder
    {
        private final List<LimaKeyframe> keyframes = new ObjectArrayList<>();

        private Builder() { }

        public Builder start(float value, EasingType easing)
        {
            return frame(0f, value, easing);
        }

        public Builder frame(float time, float value, EasingType easing)
        {
            keyframes.add(new LimaKeyframe(time, value, easing));
            return this;
        }

        public LimaKeyframeTrack end(float time, float value)
        {
            return frame(time, value, EasingType.CONSTANT).build();
        }

        public LimaKeyframeTrack end(float value)
        {
            return end(1f, value);
        }

        private LimaKeyframeTrack build()
        {
            return new LimaKeyframeTrack(keyframes).validate().getOrThrow();
        }
    }
}