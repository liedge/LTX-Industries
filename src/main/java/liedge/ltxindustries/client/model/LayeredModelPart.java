package liedge.ltxindustries.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import net.neoforged.neoforge.client.model.standalone.UnbakedStandaloneModel;

import java.util.List;

public final class LayeredModelPart
{
    public static final LayeredModelPart EMPTY = new LayeredModelPart();

    public static LayeredModelPart.Unbaked create(List<Identifier> modelIds)
    {
        return new Unbaked(modelIds);
    }

    public static LayeredModelPart.Unbaked create(Identifier... modelIds)
    {
        return create(List.of(modelIds));
    }

    public static LayeredModelPart get(ModelManager manager, StandaloneModelKey<LayeredModelPart> key)
    {
        LayeredModelPart model = manager.getStandaloneModel(key);
        return model != null ? model : EMPTY;
    }

    //private final List<Layer> layers;

    /*
    private LayeredModelPart(List<Layer> layers)
    {
        this.layers = layers;
    }
    */

    public void render(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight)
    {
        /*
        for (Layer layer : layers)
        {
            layer.submitLayer(poseStack, nodeCollector, packedLight);
        }
        */
    }

    public static final class Unbaked implements UnbakedStandaloneModel<LayeredModelPart>
    {
        private final List<Identifier> layers;

        private Unbaked(List<Identifier> layers)
        {
            this.layers = layers;
        }

        @Override
        public LayeredModelPart bake(ModelBaker baker)
        {
            /*
            ObjectList<Layer> bakedLayers = new ObjectArrayList<>();

            for (Identifier layer : layers)
            {
                Layer baked = bakeLayer(baker, layer);
                if (baked != null) bakedLayers.add(baked);
            }
            */

            return EMPTY;
            //return bakedLayers.isEmpty() ? EMPTY : new LayeredModelPart(ObjectLists.unmodifiable(bakedLayers));
        }

        @Override
        public void resolveDependencies(Resolver resolver)
        {
            layers.forEach(resolver::markDependency);
        }

        /*
        private @Nullable Layer bakeLayer(ModelBaker baker, Identifier modelId)
        {
            ResolvedModel model = baker.getModel(modelId);
            TextureSlots textures = model.getTopTextureSlots();
            ContextMap extraData = model.getTopAdditionalProperties();

            List<BakedQuad> quads = model.bakeTopGeometry(textures, baker, BlockModelRotation.IDENTITY).getAll();
            if (quads.isEmpty()) return null;

            RenderType renderType = RenderTypeHelper.detectItemModelRenderType(quads, getRenderGroup(extraData)).apply(ItemStack.EMPTY);

            return new Layer(quads, renderType);
        }
        */

        /*
        private RenderTypeGroup getRenderGroup(ContextMap map)
        {
            RenderTypeGroup group = map.getOptional(NeoForgeModelProperties.RENDER_TYPE);
            if (group == null) group = new RenderTypeGroup(ChunkSectionLayer.SOLID, RenderTypes::entitySolid);
            return group;
        }
        */
    }

    /*
    private record Layer(List<BakedQuad> quads, RenderType renderType)
    {
        private void submitLayer(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight)
        {
            nodeCollector.submitCustomGeometry(poseStack, renderType, (pose, buffer) ->
            {
                for (BakedQuad quad : quads)
                {
                    buffer.putBulkData(pose, quad, 1f, 1f, 1f, 1f, packedLight, OverlayTexture.NO_OVERLAY);
                }
            });
        }
    }
    */
}