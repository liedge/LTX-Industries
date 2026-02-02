package liedge.ltxindustries.client.model.custom;

import com.google.common.primitives.Floats;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.lib.LimaColor;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.LTXIndustriesClient;
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
    public static final int SHIELD_POLYGON_COUNT = 122;
    private static final Codec<List<Geometry>> GEOMETRIES_CODEC = Geometry.CODEC.listOf(SHIELD_POLYGON_COUNT, SHIELD_POLYGON_COUNT);

    private final List<Geometry> geometries = new ObjectArrayList<>();

    private BubbleShieldModel() {}

    @Override
    public void onResourceManagerReload(ResourceManager manager)
    {
        try (BufferedReader reader = manager.openAsReader(LTXIndustries.RESOURCES.location("misc/bubble_shield_model.json")))
        {
            // Clear current data
            geometries.clear();

            // Decode geometries
            JsonObject root = GsonHelper.parse(reader);
            JsonArray shapesJson = GsonHelper.getAsJsonArray(root, "shapes");
            geometries.addAll(LimaCoreCodecs.strictDecode(GEOMETRIES_CODEC, JsonOps.INSTANCE, shapesJson));
        }
        catch (JsonParseException | IllegalStateException | IOException ex)
        {
            LTXIndustriesClient.CLIENT_LOGGER.error("bubble_shield_model.json didn't decode correctly, it might be missing, corrupted, or modified by a data-pack. Don't do that.", ex);
            throw new IllegalStateException("Failed to loaded bubble shield model.", ex);
        }

        LTXIndustriesClient.CLIENT_LOGGER.info("Loaded bubble shield model.");
    }

    public void renderFaces(int[] geometryIndexes, VertexConsumer buffer, Matrix4f mx4, LimaColor color, float alpha)
    {
        for (int i : geometryIndexes)
        {
            geometries.get(i).submit(buffer, mx4, color, alpha);
        }
    }

    private record Geometry(List<float[]> triangles)
    {
        private static final Codec<float[]> TRIANGLE_CODEC = Codec.FLOAT.listOf(9, 9).xmap(Floats::toArray, FloatList::of);
        private static final Codec<Geometry> CODEC = TRIANGLE_CODEC.listOf().xmap(Geometry::new, Geometry::triangles);

        void submit(VertexConsumer buffer, Matrix4f mx4, LimaColor color, float alpha)
        {
            for (float[] t : triangles)
            {
                buffer.addVertex(mx4, t[0], t[1], t[2]).setColor(color.red(), color.green(), color.blue(), alpha);
                buffer.addVertex(mx4, t[3], t[4], t[5]).setColor(color.red(), color.green(), color.blue(), alpha);
                buffer.addVertex(mx4, t[6], t[7], t[8]).setColor(color.red(), color.green(), color.blue(), alpha);
            }
        }
    }
}