package liedge.limatech.client.model.custom;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.util.LimaJsonUtil;
import liedge.limatech.LimaTech;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.GsonHelper;
import org.joml.Matrix4f;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public final class BubbleShieldModel implements ResourceManagerReloadListener
{
    public static final BubbleShieldModel SHIELD_MODEL = new BubbleShieldModel();

    private final List<SimpleTriangularGeometry> geometries = new ObjectArrayList<>();

    private BubbleShieldModel() {}

    @Override
    public void onResourceManagerReload(ResourceManager manager)
    {
        // Read json data
        JsonArray root;
        try (BufferedReader reader = manager.openAsReader(LimaTech.RESOURCES.location("misc/bubble_shield_model.json")))
        {
            root = GsonHelper.parseArray(reader);
        }
        catch (IOException ex)
        {
            throw new RuntimeException("Failed to load bubble shield model.", ex);
        }

        // Clear current data
        geometries.clear();

        // Read geometries
        for (JsonElement element : root)
        {
            JsonArray rawGeometry = element.getAsJsonArray();
            geometries.add(LimaJsonUtil.codecDecode(SimpleTriangularGeometry.CODEC, rawGeometry));
        }

        LimaTech.LOGGER.info("Loaded bubble shield model.");
    }

    public void renderFaces(int[] geometryIndexes, VertexConsumer buffer, Matrix4f mx4, LimaColor color, float alpha)
    {
        for (int i : geometryIndexes)
        {
            geometries.get(i).render(buffer, mx4, color, alpha);
        }
    }
}