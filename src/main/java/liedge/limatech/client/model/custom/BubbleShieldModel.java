package liedge.limatech.client.model.custom;

import com.google.gson.JsonArray;
import com.mojang.blaze3d.vertex.VertexConsumer;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.util.LimaJsonUtil;
import liedge.limatech.LimaTech;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.GsonHelper;
import org.joml.Matrix4f;

import java.io.BufferedReader;
import java.io.IOException;

public final class BubbleShieldModel implements ResourceManagerReloadListener
{
    private static final int FACES = 32;
    public static final BubbleShieldModel SHIELD_MODEL = new BubbleShieldModel();

    private final VertexTriangle[][] triangles;

    private BubbleShieldModel()
    {
        this.triangles = new VertexTriangle[FACES][];
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager)
    {
        // Read json data
        JsonArray root;
        try (BufferedReader reader = manager.openAsReader(LimaTech.RESOURCES.location("misc/bubble_shield.json")))
        {
            root = GsonHelper.parseArray(reader);
        }
        catch (IOException ex)
        {
            throw new RuntimeException("Failed to load bubble shield model.", ex);
        }

        for (int i = 0; i < FACES; i++)
        {
            JsonArray face = root.get(i).getAsJsonArray();
            triangles[i] = new VertexTriangle[face.size()];

            for (int j = 0; j < face.size(); j++)
            {
                triangles[i][j] = LimaJsonUtil.codecDecode(VertexTriangle.CODEC, face.get(j));
            }
        }

        LimaTech.LOGGER.info("Loaded bubble shield model.");
    }

    public void renderFaces(int[] faceIndexes, VertexConsumer buffer, Matrix4f mx4, LimaColor color, float alpha)
    {
        for (int i : faceIndexes)
        {
            for (VertexTriangle triangle : triangles[i])
            {
                triangle.putInBufferDirect(buffer, mx4, color, alpha);
            }
        }
    }
}