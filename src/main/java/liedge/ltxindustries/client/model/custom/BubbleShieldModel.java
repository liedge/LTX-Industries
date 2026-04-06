package liedge.ltxindustries.client.model.custom;

import com.google.common.primitives.Floats;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.LTXIndustriesClient;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.ARGB;
import net.minecraft.util.GsonHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public final class BubbleShieldModel implements ResourceManagerReloadListener
{
    public static final BubbleShieldModel INSTANCE = new BubbleShieldModel();
    public static final int SHIELD_POLYGON_COUNT = 122;
    private static final Codec<List<Geometry>> GEOMETRIES_CODEC = Geometry.CODEC.listOf(SHIELD_POLYGON_COUNT, SHIELD_POLYGON_COUNT);

    private final List<Geometry> geometries = new ObjectArrayList<>();

    private BubbleShieldModel() {}

    @Override
    public void onResourceManagerReload(ResourceManager manager)
    {
        try (BufferedReader reader = manager.openAsReader(LTXIndustries.RESOURCES.id("core/bubble_shield_model.json")))
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