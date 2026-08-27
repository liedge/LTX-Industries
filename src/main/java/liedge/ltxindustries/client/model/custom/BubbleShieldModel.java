package liedge.ltxindustries.client.model.custom;

import com.google.common.primitives.Floats;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.floats.FloatList;
import net.minecraft.util.ARGB;

import java.util.List;

public final class BubbleShieldModel
{
    public static final int SHIELD_POLYGON_COUNT = 122;
    public static final Codec<BubbleShieldModel> CODEC = Geometry.CODEC.listOf(SHIELD_POLYGON_COUNT, SHIELD_POLYGON_COUNT).fieldOf("shapes").xmap(BubbleShieldModel::new, o -> o.geometries).codec();

    private final List<Geometry> geometries;

    private BubbleShieldModel(List<Geometry> geometries)
    {
        this.geometries = geometries;
    }

    public void submitFaces(PoseStack.Pose pose, VertexConsumer buffer, int[] indexes, int color, float alpha)
    {
        for (int i : indexes)
        {
            geometries.get(i).submit(pose, buffer, ARGB.redFloat(color), ARGB.greenFloat(color), ARGB.blueFloat(color), alpha);
        }
    }

    private record Geometry(List<float[]> triangles)
    {
        private static final Codec<float[]> TRIANGLE_CODEC = Codec.FLOAT.listOf(9, 9).xmap(Floats::toArray, FloatList::of);
        private static final Codec<Geometry> CODEC = TRIANGLE_CODEC.listOf().xmap(Geometry::new, Geometry::triangles);

        private void submit(PoseStack.Pose pose, VertexConsumer buffer, float red, float green, float blue, float alpha)
        {
            for (float[] t : triangles)
            {
                buffer.addVertex(pose, t[0], t[1], t[2]).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, t[3], t[4], t[5]).setColor(red, green, blue, alpha);
                buffer.addVertex(pose, t[6], t[7], t[8]).setColor(red, green, blue, alpha);
            }
        }
    }
}