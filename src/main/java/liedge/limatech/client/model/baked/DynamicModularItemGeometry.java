package liedge.limatech.client.model.baked;

import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import liedge.limacore.client.model.*;
import liedge.limacore.util.LimaJsonUtil;
import liedge.limatech.LimaTech;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DynamicModularItemGeometry extends BlockBenchGroupGeometry
{
    private static final ResourceLocation LOADER_ID = LimaTech.RESOURCES.location("dynamic_model");
    public static final LimaGeometryLoader<?> DYNAMIC_MODEL_LOADER = new Loader();
    public static final String MAIN_SUB_MODEL_NAME = "main";

    private final Map<String, List<String>> submodelGroups;
    private final boolean useCustomRenderer;

    private DynamicModularItemGeometry(List<BlockElement> elements, List<BlockBenchGroupData> groups, Map<String, List<String>> submodelGroups, boolean useCustomRenderer)
    {
        super(elements, groups);
        this.submodelGroups = submodelGroups;
        this.useCustomRenderer = useCustomRenderer;
    }

    @Override
    protected BakedModel bakeGroups(Map<String, BakedQuadGroup> quadGroups, IGeometryBakingContext context, TextureAtlasSprite particleIcon, RenderTypeGroup renderTypeGroup, ItemOverrides overrides)
    {
        Set<String> usedNames = new ObjectOpenHashSet<>();
        Object2ObjectMap<String, BakedQuadGroup> submodels = new Object2ObjectOpenHashMap<>();

        // Build submodels (if any)
        for (Map.Entry<String, List<String>> entry : submodelGroups.entrySet())
        {
            List<BakedQuadGroup> submodelQuads = entry.getValue()
                    .stream()
                    .map(n -> {
                        usedNames.add(n);
                        return Objects.requireNonNull(quadGroups.get(n), "Group " + n + " not found in model groups.");
                    })
                    .toList();

            submodels.put(entry.getKey(), BakedQuadGroup.allOf(submodelQuads));
        }

        // Build main submodel
        List<BakedQuadGroup> mainSubmodelQuads = Sets.difference(quadGroups.keySet(), usedNames).stream().map(n -> Objects.requireNonNull(quadGroups.get(n), "Group " + n + " not found in model groups.")).toList();
        submodels.put(MAIN_SUB_MODEL_NAME, BakedQuadGroup.allOf(mainSubmodelQuads));

        return new DynamicModularItemBakedModel(context.useAmbientOcclusion(), context.isGui3d(), context.useBlockLight(), particleIcon, context.getTransforms(), useCustomRenderer, renderTypeGroup, quadGroups, Object2ObjectMaps.unmodifiable(submodels));
    }

    private static class Loader extends GeometryLoader
    {
        @Override
        protected BlockBenchGroupGeometry createGeometry(JsonObject modelJson, List<BlockElement> elements, List<BlockBenchGroupData> groups)
        {
            Map<String, List<String>> submodelGroups;

            if (modelJson.has("submodels"))
            {
                submodelGroups = new Object2ObjectOpenHashMap<>();

                JsonObject submodelsJson = GsonHelper.getAsJsonObject(modelJson, "submodels");
                submodelsJson.asMap().forEach((name, elem) -> {
                    List<String> names = LimaJsonUtil.stringsArrayStream(elem.getAsJsonArray()).toList();
                    submodelGroups.put(name, names);
                });
            }
            else
            {
                submodelGroups = Map.of();
            }

            // Read Custom renderer property
            boolean useCustomRenderer = GsonHelper.getAsBoolean(modelJson, "custom_renderer", false);

            return new DynamicModularItemGeometry(elements, groups, submodelGroups, useCustomRenderer);
        }

        @Override
        public ResourceLocation getLoaderId()
        {
            return LOADER_ID;
        }
    }
}