package liedge.ltxindustries.client.renderer;

import net.minecraft.util.EasingType;

public final class LTXIKeyframeTracks
{
    private LTXIKeyframeTracks() { }


    // Standard tracks
    public static final LimaKeyframeTrack WEAPON_CROSSHAIR = LimaKeyframeTrack.builder()
            .start(0f, EasingType.OUT_CIRC)
            .frame(0.175f, 1f, EasingType.CONSTANT)
            .frame(0.2f, 1f, EasingType.IN_SINE)
            .end(0f);

    public static final LimaKeyframeTrack SHIELD_FADE = LimaKeyframeTrack.builder()
            .start(0.125f, EasingType.OUT_EXPO)
            .frame(0.35f, 0.8f, EasingType.IN_SINE)
            .end(0.125f);

    public static final LimaKeyframeTrack MATERIAL_PRESS = LimaKeyframeTrack.builder()
            .start(0f, EasingType.OUT_BOUNCE)
            .frame(0.25f, 1f, EasingType.CONSTANT)
            .frame(0.5f, 1f, EasingType.IN_SINE)
            .end(0.825f, 0f);
}