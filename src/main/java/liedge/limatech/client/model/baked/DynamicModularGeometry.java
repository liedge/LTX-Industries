package liedge.limatech.client.model.baked;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.client.model.BlockBenchGroupGeometry;
import liedge.limacore.client.model.LimaGeometryLoader;
import liedge.limatech.LimaTech;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.model.QuadTransformers;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class DynamicModularGeometry extends BlockBenchGroupGeometry<DynamicModularGeometry.GroupData>
{
    private static final ResourceLocation LOADER_ID = LimaTech.RESOURCES.location("dynamic_model");
    public static final LimaGeometryLoader<?> DYNAMIC_MODEL_LOADER = new Loader();

    private DynamicModularGeometry(List<BlockElement> elements, List<GroupData> groups)
    {
        super(elements, groups);
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides)
    {
        TextureAtlasSprite particleIcon = spriteGetter.apply(context.getMaterial("particle"));
        ResourceLocation hint = context.getRenderTypeHint();
        RenderTypeGroup renderTypeGroup = hint != null ? context.getRenderType(hint) : RenderTypeGroup.EMPTY;

        Map<String, SubModelBuilder> builders = new Object2ObjectOpenHashMap<>();
        groups.stream().map(GroupData::submodel).distinct().forEach(name -> builders.put(name, new SubModelBuilder()));

        for (GroupData group : groups)
        {
            for (int elementIndex : group.elements)
            {
                BlockElement element = elements.get(elementIndex);
                for (Direction direction : element.faces.keySet())
                {
                    BlockElementFace face = element.faces.get(direction);
                    TextureAtlasSprite sprite = spriteGetter.apply(context.getMaterial(face.texture()));
                    BakedQuad quad = BlockModel.bakeFace(element, face, sprite, direction, modelState);

                    if (group.emissive)
                    {
                        QuadTransformers.settingMaxEmissivity().processInPlace(quad);
                        builders.get(group.submodel).addEmissiveQuad(quad);
                    }
                    else
                    {
                        builders.get(group.submodel).addNonEmissiveQuad(quad);
                    }
                }
            }
        }

        return new DynamicModularBakedModel(context.useAmbientOcclusion(), context.isGui3d(), context.useBlockLight(), particleIcon, context.getTransforms(), renderTypeGroup, builders);
    }

    private static class Loader extends GeometryLoader<GroupData>
    {
        @Override
        protected GroupData deserializeGroup(JsonObject json, IntList groupElements)
        {
            String submodel = GsonHelper.getAsString(json, "sub_model", "main");
            boolean emissive = GsonHelper.getAsBoolean(json, "emissive", false);
            return new GroupData(submodel, groupElements, emissive);
        }

        @Override
        protected BlockBenchGroupGeometry<GroupData> createGeometry(JsonObject modelJson, List<BlockElement> elements, List<GroupData> groups)
        {
            return new DynamicModularGeometry(elements, groups);
        }

        @Override
        public ResourceLocation getLoaderId()
        {
            return LOADER_ID;
        }
    }

    static class SubModelBuilder
    {
        private final List<BakedQuad> nonEmissiveQuads = new ObjectArrayList<>();
        private final List<BakedQuad> emissiveQuads = new ObjectArrayList<>();

        void addNonEmissiveQuad(BakedQuad quad)
        {
            nonEmissiveQuads.add(quad);
        }

        void addEmissiveQuad(BakedQuad quad)
        {
            emissiveQuads.add(quad);
        }

        DynamicModularBakedModel.SubModel buildSubmodel(DynamicModularBakedModel parent)
        {
            return new DynamicModularBakedModel.SubModel(parent, nonEmissiveQuads, emissiveQuads);
        }
    }

    public record GroupData(String submodel, IntList elements, boolean emissive) {}
}