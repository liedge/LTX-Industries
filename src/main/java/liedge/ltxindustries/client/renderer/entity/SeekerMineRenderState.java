package liedge.ltxindustries.client.renderer.entity;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public class SeekerMineRenderState extends EntityRenderState
{
    private static final double[] NONE = {};

    public float yRot;
    public float wheelXRot;
    public float capXRot;

    public @Nullable Vector3fc targetPos;
}