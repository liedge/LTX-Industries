package liedge.ltxindustries.client.model.custom;

import com.google.gson.JsonArray;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.lib.LimaColor;
import liedge.ltxindustries.LTXIndustries;
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
    private static final Codec<List<SimpleTriangularGeometry>> CODEC = SimpleTriangularGeometry.CODEC.listOf(SHIELD_POLYGON_COUNT, SHIELD_POLYGON_COUNT);

    private final List<SimpleTriangularGeometry> geometries = new ObjectArrayList<>();

    private BubbleShieldModel() {}

    @Override
    public void onResourceManagerReload(ResourceManager manager)
    {
        try (BufferedReader reader = manager.openAsReader(LTXIndustries.RESOURCES.location("misc/bubble_shield_model.json")))
        {
            // Read json file
            JsonArray root = GsonHelper.parseArray(reader);

            // Clear current data
            geometries.clear();

            // Decode geometries
            geometries.addAll(LimaCoreCodecs.strictDecode(CODEC, JsonOps.INSTANCE, root));
        }
        catch (JsonParseException | IllegalStateException | IOException ex)
        {
            LTXIndustries.LOGGER.error("bubble_shield_model.json didn't decode correctly, it might be missing, corrupted, or modified by a data-pack. Don't do that.", ex);
            throw new IllegalStateException("Failed to loaded bubble shield model.", ex);
        }

        LTXIndustries.LOGGER.info("Loaded bubble shield model.");
    }

    public void renderFaces(int[] geometryIndexes, VertexConsumer buffer, Matrix4f mx4, LimaColor color, float alpha)
    {
        for (int i : geometryIndexes)
        {
            geometries.get(i).render(buffer, mx4, color, alpha);
        }
    }
}