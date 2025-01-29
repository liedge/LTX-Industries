package liedge.limatech.client.model.custom;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import liedge.limacore.lib.LimaColor;
import org.joml.Matrix4f;

import java.util.List;

public record SimpleTriangularGeometry(List<VertexTriangle> triangles)
{
    public static final Codec<SimpleTriangularGeometry> CODEC = VertexTriangle.CODEC.listOf().xmap(SimpleTriangularGeometry::new, SimpleTriangularGeometry::triangles);

    public void render(VertexConsumer buffer, Matrix4f mx4, LimaColor color, float alpha)
    {
        for (VertexTriangle triangle : triangles)
        {
            triangle.render(buffer, mx4, color, alpha);
        }
    }
}