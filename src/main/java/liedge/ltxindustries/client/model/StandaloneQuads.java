package liedge.ltxindustries.client.model;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import liedge.ltxindustries.client.LTXIndustriesClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import net.neoforged.neoforge.client.model.standalone.UnbakedStandaloneModel;
import org.joml.Vector3f;

import java.util.List;

public final class StandaloneQuads
{
    public static final StandaloneQuads EMPTY = new StandaloneQuads(List.of());

    public static StandaloneQuads get(StandaloneModelKey<StandaloneQuads> key)
    {
        StandaloneQuads model = Minecraft.getInstance().getModelManager().getStandaloneModel(key);
        return model != null ? model : EMPTY;
    }

    public static UnbakedStandaloneModel<StandaloneQuads> create(Identifier modelId)
    {
        return new Unbaked(modelId);
    }

    private final List<Layer> layers;

    private StandaloneQuads(List<Layer> layers)
    {
        this.layers = layers;
    }

    public void render(PoseStack poseStack, SubmitNodeCollector nodeCollector, int lightCoords)
    {
        for (Layer layer : layers)
        {
            layer.drawLayer(poseStack, nodeCollector, lightCoords);
        }
    }

    private record Layer(RenderType renderType, List<BakedQuad> quads)
    {
        void drawLayer(PoseStack poseStack, SubmitNodeCollector nodeCollector, int lightCoords)
        {
            nodeCollector.submitCustomGeometry(poseStack, renderType, (pose, buffer) ->
            {
                for (BakedQuad quad : quads)
                {
                    putQuad(pose, buffer, quad, lightCoords);
                }
            });
        }

        void putQuad(PoseStack.Pose pose, VertexConsumer buffer, BakedQuad quad, int lightCoords)
        {
            int lightEmission = quad.materialInfo().lightEmission();

            for (int vertex = 0; vertex < 4; vertex++)
            {
                long packedUV = quad.packedUV(vertex);
                Vector3f normal = pose.transformNormal(quad.direction().getUnitVec3f(), new Vector3f());

                buffer.applyBakedNormals(normal, quad.bakedNormals(), vertex, pose.normal());
                buffer
                        .addVertex(pose, quad.position(vertex))
                        .setColor(-1)
                        .setUv(UVPair.unpackU(packedUV), UVPair.unpackV(packedUV))
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(LightCoordsUtil.lightCoordsWithEmission(lightCoords, lightEmission))
                        .setNormal(normal.x, normal.y, normal.z);
            }
        }
    }

    private record Unbaked(Identifier modelId) implements UnbakedStandaloneModel<StandaloneQuads>
    {
        @Override
        public StandaloneQuads bake(ModelBaker baker)
        {
            ResolvedModel model = baker.getModel(modelId);
            List<BakedQuad> unsorted = model.bakeTopGeometry(model.getTopTextureSlots(), baker, BlockModelRotation.IDENTITY).getAll();

            Multimap<RenderType, BakedQuad> map = MultimapBuilder.hashKeys().arrayListValues().build();

            for (BakedQuad quad : unsorted)
            {
                if (map.keySet().size() <= 8)
                {
                    map.put(quad.materialInfo().itemRenderType(), quad);
                }
                else
                {
                    LTXIndustriesClient.CLIENT_LOGGER.warn("Standalone model {} exceeded 8 render types, skipping.", model.debugName());
                    return EMPTY;
                }
            }

            ObjectList<Layer> layers = new ObjectArrayList<>();
            for (RenderType renderType : map.keySet())
            {
                layers.add(new Layer(renderType, List.copyOf(map.get(renderType))));
            }

            return new StandaloneQuads(ObjectLists.unmodifiable(layers));
        }

        @Override
        public void resolveDependencies(Resolver resolver)
        {
            resolver.markDependency(modelId);
        }
    }
}