package liedge.limatech.client.model.custom;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.lib.LimaColor;
import net.minecraft.util.ExtraCodecs;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

public record VertexTriangle(Vector3f a, Vector3f b, Vector3f c)
{
    public static final Codec<VertexTriangle> CODEC = LimaCoreCodecs.triComapFlatMap(ExtraCodecs.VECTOR3F, VertexTriangle::new, triangle -> List.of(triangle.a, triangle.b, triangle.c));

    public void putInBufferDirect(VertexConsumer buffer, Matrix4f mx4, LimaColor color, float alpha)
    {
        buffer.addVertex(mx4, a.x, a.y, a.z).setColor(color.red(), color.green(), color.blue(), alpha);
        buffer.addVertex(mx4, b.x, b.y, b.z).setColor(color.red(), color.green(), color.blue(), alpha);
        buffer.addVertex(mx4, c.x, c.y, c.z).setColor(color.red(), color.green(), color.blue(), alpha);
    }
}