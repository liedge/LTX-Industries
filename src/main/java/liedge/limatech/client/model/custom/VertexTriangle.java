package liedge.limatech.client.model.custom;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.floats.FloatList;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.lib.LimaColor;
import org.joml.Matrix4f;

import java.util.List;
import java.util.function.IntFunction;

public record VertexTriangle(float aX, float aY, float aZ, float bX, float bY, float bZ, float cX, float cY, float cZ)
{
    public static final Codec<VertexTriangle> CODEC = LimaCoreCodecs.fixedListComapFlatMap(Codec.FLOAT, 9, VertexTriangle::new, VertexTriangle::toList);

    private VertexTriangle(IntFunction<Float> accessor)
    {
        this(accessor.apply(0), accessor.apply(1), accessor.apply(2), accessor.apply(3), accessor.apply(4), accessor.apply(5), accessor.apply(6), accessor.apply(7), accessor.apply(8));
    }

    private List<Float> toList()
    {
        return FloatList.of(aX, aY, aZ, bX, bY, bZ, cX, cY, cZ);
    }

    public void render(VertexConsumer buffer, Matrix4f mx4, LimaColor color, float alpha)
    {
        buffer.addVertex(mx4, aX, aY, aZ).setColor(color.red(), color.green(), color.blue(), alpha);
        buffer.addVertex(mx4, bX, bY, bZ).setColor(color.red(), color.green(), color.blue(), alpha);
        buffer.addVertex(mx4, cX, cY, cZ).setColor(color.red(), color.green(), color.blue(), alpha);
    }
}